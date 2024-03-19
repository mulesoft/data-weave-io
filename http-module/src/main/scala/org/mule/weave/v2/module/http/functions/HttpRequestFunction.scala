package org.mule.weave.v2.module.http.functions

import org.mule.weave.v2.core.functions.SecureTernaryFunctionValue
import org.mule.weave.v2.core.util.ObjectValueUtils
import org.mule.weave.v2.core.util.ObjectValueUtils.select
import org.mule.weave.v2.core.util.ObjectValueUtils.selectBoolean
import org.mule.weave.v2.core.util.ObjectValueUtils.selectObject
import org.mule.weave.v2.core.util.ObjectValueUtils.selectString
import org.mule.weave.v2.core.util.ObjectValueUtils.selectStringAnyMap
import org.mule.weave.v2.model.EvaluationContext
import org.mule.weave.v2.model.service.WeaveRuntimePrivilege
import org.mule.weave.v2.model.structure.ObjectSeq
import org.mule.weave.v2.model.types._
import org.mule.weave.v2.model.values._
import org.mule.weave.v2.model.values.wrappers.LazyValue
import org.mule.weave.v2.module.DataFormat
import org.mule.weave.v2.module.DataFormatManager
import org.mule.weave.v2.module.core.multipart.MultiPartDataFormat
import org.mule.weave.v2.module.core.multipart.MultiPartWriterSettings
import org.mule.weave.v2.module.http.HttpHeader
import org.mule.weave.v2.module.http.functions.exceptions.InvalidUrlException
import org.mule.weave.v2.module.http.functions.exceptions.UrlConnectionException
import org.mule.weave.v2.module.http.functions.utils.HttpClientResponseConverter
import org.mule.weave.v2.module.http.service.HttpClientOptions
import org.mule.weave.v2.module.http.service.HttpClientResponse
import org.mule.weave.v2.module.http.service.HttpClientService
import org.mule.weave.v2.module.reader.DefaultAutoPersistedOutputStream
import org.mule.weave.v2.module.writer.Writer
import org.mule.weave.v2.parser.exception.WeaveRuntimeException
import org.mule.weave.v2.parser.location.UnknownLocation
import org.mule.weave.v2.parser.module.MimeType

import java.io.InputStream
import java.net.ConnectException
import java.net.UnknownHostException
import java.util.concurrent.CompletableFuture
import java.util.concurrent.ExecutionException
import scala.collection.mutable
import scala.collection.mutable.ArrayBuffer

// request("GET", "URL", headers = "{}", body = Any | Null , config)
class HttpRequestFunction extends SecureTernaryFunctionValue {
  override val First: StringType = StringType

  override val Second: UnionType = UnionType(Seq(StringType, ObjectType))

  override val Third: ObjectType = ObjectType

  override val thirdDefaultValue: Option[ValueProvider] = Some(new ValueProvider {
    override def value()(implicit ctx: EvaluationContext): Value[_] = {
      ObjectValue.empty
    }
  })

  override val requiredPrivilege: WeaveRuntimePrivilege = HttpWeaveRuntimePrivilege.HTTP_CLIENT

  override protected def onSecureExecution(methodValue: First.V, urlValue: Second.V, requestValue: Third.V)(implicit ctx: EvaluationContext): Value[_] = {
    val method: String = methodValue.evaluate.toString

    val (url, queryParams) = urlValue.evaluate match {
      case url: CharSequence => {
        (url.toString, Map.empty[String, Seq[String]])
      }
      case urlBuilder: ObjectSeq => {
        val queryParams: mutable.Map[String, ArrayBuffer[String]] = mutable.HashMap()
        val urlObjectSeq: ObjectSeq = urlBuilder.materialize()
        val queryParamsValue = selectObject(urlObjectSeq, "queryParams").getOrElse(ObjectSeq.empty)
        val url = selectString(urlObjectSeq, "url").getOrElse(throw new WeaveRuntimeException("Expecting url", UnknownLocation))
        queryParamsValue.toSeq().foreach((kvp) => {
          val headerName: String = kvp._1.evaluate.name
          val headerValue: String = StringType.coerce(kvp._2).evaluate.toString
          queryParams.getOrElseUpdate(headerName, ArrayBuffer()).+=(headerValue)
        })
        (url, queryParams.toMap)
      }
    }

    val request: ObjectSeq = requestValue.evaluate.materialize()
    val config: ObjectSeq = selectObject(request, "config").getOrElse(ObjectSeq.empty)

    val headers: mutable.Map[String, ArrayBuffer[String]] = mutable.HashMap()
    val headersValue = selectObject(request, "headers").getOrElse(ObjectSeq.empty)
    headersValue.toSeq().foreach((kvp) => {
      val headerName: String = kvp._1.evaluate.name
      val headerValue: String = StringType.coerce(kvp._2).evaluate.toString
      headers.getOrElseUpdate(headerName, ArrayBuffer()).+=(headerValue)
    })

    val httpClientService: HttpClientService = ctx.serviceManager
      .lookupCustomService(classOf[HttpClientService])
      .getOrElse(throw new WeaveRuntimeException("HttpClientService was not registered", UnknownLocation))

    val readerProperties: Map[String, Any] = selectStringAnyMap(config, "readerProperties").getOrElse(Map())

    val writerProperties: Map[String, Any] = selectStringAnyMap(config, "writerProperties").getOrElse(Map())

    val followRedirects = selectBoolean(config, "followRedirects").getOrElse(false)

    val bodyValue = select(request, "body")
    val httpBody: Option[InputStream] = bodyValue.flatMap((body) => {
      body match {
        case nullType if (NullType.accepts(nullType)) => None
        case bt if (BinaryType.accepts(bt)) => {
          Some(BinaryType.coerce(body).evaluate.spinOff())
        }
        case _ => {
          val maybeContentType: Option[String] = headers.get(HttpHeader.CONTENT_TYPE_HEADER).map(_.head)
          val contentType = maybeContentType match {
            case Some(value) => value
            case None => {
              val defaultContentType: String = selectString(config, "defaultContentType").getOrElse("application/json")
              //If it was not defined then add it with the defaultContentType
              headers.put(HttpHeader.CONTENT_TYPE_HEADER, ArrayBuffer(defaultContentType))
              defaultContentType
            }
          }
          val dataFormat: DataFormat[_, _] = DataFormatManager.byContentType(contentType)
            .getOrElse({
              throw new WeaveRuntimeException(s"Unable to find data format for `${contentType}`", UnknownLocation)
            })
          val serviceManager = ctx.serviceManager
          val outputStream = new DefaultAutoPersistedOutputStream(serviceManager.workingDirectoryService, serviceManager.memoryService, serviceManager.settingsService)
          val writer: Writer = dataFormat.writer(Some(outputStream))
          writerProperties.foreach((prop) => {
            writer.setOption(requestValue.location(), prop._1, prop._2)
          })
          writer.startDocument(this)
          writer.writeValue(body)
          writer.endDocument(this)
          writer.close()
          if (dataFormat.isInstanceOf[MultiPartDataFormat]) {
            writer.settings.asInstanceOf[MultiPartWriterSettings].boundary match {
              case Some(boundary) => {
                val mimeType = MimeType.fromSimpleString(contentType)
                val newProperties = new mutable.HashMap[String, String]()
                newProperties.++=(mimeType.parameters)
                //Make sure we are using the correct boundary for MultiPart
                newProperties.put("boundary", boundary)
                val newMimeType = new MimeType(mimeType.mainType, mimeType.subtype, newProperties.toMap).toString()
                headers.put(HttpHeader.CONTENT_TYPE_HEADER, ArrayBuffer(newMimeType))
              }
              case _ =>
            }
          }
          Some(outputStream.toInputStream)
        }
      }
    })

    val maybeReadTimeOut = ObjectValueUtils.selectInt(config, "readTimeout")
    val maybeRequestTimeout = ObjectValueUtils.selectInt(config, "requestTimeout")
    try {

      val builder = new HttpClientOptions.Builder()
        .withUrl(url)
        .withMethod(method)
        .withAllowRedirect(followRedirects)

      headers.toMap.foreach(entry => {
        entry._2.foreach(value => {
          builder.withHeader(entry._1, value)
        })
      })

      queryParams.foreach(entry => {
        entry._2.foreach(value => {
          builder.withQueryParam(entry._1, value)
        })
      })

      if (httpBody.isDefined) {
        builder.withBody(httpBody.get)
      }

      if (maybeReadTimeOut.isDefined) {
        builder.withReadTimeout(maybeRequestTimeout.get)
      }

      if (maybeRequestTimeout.isDefined) {
        builder.withRequestTimeout(maybeRequestTimeout.get)
      }

      val options: HttpClientOptions = builder.build()
      val future: CompletableFuture[_ <: HttpClientResponse] = httpClientService
        .request(options)
      new LazyValue({
        try {
          future
            .thenApply[ObjectValue]((result: HttpClientResponse) => {
              HttpClientResponseConverter().convert(result, readerProperties)
            })
            .get()
        } catch {
          case ee: ExecutionException => {
            ee.getCause match {
              case ce: Exception => {
                throw new UrlConnectionException(url, ce.getMessage, this.location())
              }
            }
            throw new WeaveRuntimeException(ee.getLocalizedMessage, urlValue.location())
          }
        }
      }, this)
    } catch {
      case uh: UnknownHostException => {
        throw new UrlConnectionException(url, uh.getMessage, this.location())
      }
      case iae: IllegalArgumentException => {
        throw new InvalidUrlException(url, this.location())
      }
      case ce: ConnectException => {
        throw new UrlConnectionException(url, ce.getMessage, this.location())
      }
    }
  }
}

