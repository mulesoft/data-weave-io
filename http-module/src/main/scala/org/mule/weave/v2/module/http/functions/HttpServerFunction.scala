package org.mule.weave.v2.module.http.functions

import java.io.ByteArrayInputStream
import java.io.InputStream
import java.io.PrintWriter
import java.io.StringWriter
import java.util
import org.mule.weave.v2.core.functions.{ EmptyFunctionValue, SecureBinaryFunctionValue }
import org.mule.weave.v2.interpreted.ExecutionContext
import org.mule.weave.v2.model.EvaluationContext
import org.mule.weave.v2.model.ServiceManager
import org.mule.weave.v2.model.capabilities.UnknownLocationCapable
import org.mule.weave.v2.model.service.WeaveRuntimePrivilege
import org.mule.weave.v2.model.structure.KeyValuePair
import org.mule.weave.v2.model.types.BinaryType
import org.mule.weave.v2.model.types.FunctionType
import org.mule.weave.v2.model.types.ObjectType
import org.mule.weave.v2.model.values.{ BooleanValue, KeyValue, NumberValue, ObjectValue, StringValue, Value, ValuesHelper }
import org.mule.weave.v2.module.DataFormatManager
import org.mule.weave.v2.module.http.HttpHeader.CONTENT_LENGTH_HEADER
import org.mule.weave.v2.module.http.HttpHeader.CONTENT_TYPE_HEADER
import org.mule.weave.v2.module.http.functions.HttpServerFunction._
import org.mule.weave.v2.module.http.functions.exceptions.InvalidHttpBodyException
import org.mule.weave.v2.module.http.service.FailedStatus
import org.mule.weave.v2.module.http.service.HttpServerConfig
import org.mule.weave.v2.module.http.service.HttpServerRequest
import org.mule.weave.v2.module.http.service.HttpServerResponse
import org.mule.weave.v2.module.http.service.HttpServerService
import org.mule.weave.v2.module.http.service.HttpServerStatus
import org.mule.weave.v2.module.http.service.RunningStatus
import org.mule.weave.v2.module.http.values.HttpBodyValue
import org.mule.weave.v2.module.reader.AutoPersistedOutputStream
import org.mule.weave.v2.parser.exception.LocatableException
import org.mule.weave.v2.parser.exception.WeaveRuntimeException
import org.mule.weave.v2.parser.location.UnknownLocation
import org.mule.weave.v2.util.ObjectValueUtils._

import scala.collection.JavaConverters._

class HttpServerFunction extends SecureBinaryFunctionValue {

  override val L: ObjectType = ObjectType

  override val R: FunctionType = FunctionType

  override val requiredPrivilege: WeaveRuntimePrivilege = HttpWeaveRuntimePrivilege.HTTP_SERVER

  override protected def onSecureExecution(value1: L.V, value2: R.V)(implicit context: EvaluationContext): Value[_] = {
    val config = ObjectType.coerce(value1)
    val callBack = FunctionType.coerce(value2)
    val manager: ServiceManager = context.serviceManager
    val httpServerService: HttpServerService = manager.lookupCustomService(classOf[HttpServerService])
      .getOrElse(throw new WeaveRuntimeException("No HTTP Service Was found", UnknownLocation))

    val configObject = config.materialize.evaluate

    val port: Int = selectInt(configObject, PORT_KEY_NAME).getOrElse(8081)
    val host: String = selectString(configObject, HOST_KEY_NAME).getOrElse("localhost")
    val maxContentLength: Int = selectInt(configObject, MAX_CONTENT_LENGTH).getOrElse(65536)

    val serverHandler: HttpServerStatus = httpServerService.server(
      HttpServerConfig(port, host, maxContentLength),
      (request) => {
        val newThreadContext: ExecutionContext = context.asInstanceOf[ExecutionContext].spawnNewThread()
        try {
          val requestValue = toRequestObjectValue(request)
          val callbackResult = callBack.call(ValuesHelper.array(requestValue))(newThreadContext)
          val closeCallback: () => Unit = () => {
            newThreadContext.close()
          }
          toHttpResponse(callbackResult, closeCallback)(newThreadContext)
        } catch {
          case e: LocatableException => {
            context.serviceManager.loggingService.logError(e.getMessage())
            newThreadContext.close()
            HttpServerResponse(new ByteArrayInputStream(e.getMessage().getBytes("UTF-8")), Map(CONTENT_TYPE_HEADER -> "text/plain"), () => {}, 500)
          }
          case e: Exception => {
            val writer = new StringWriter()
            e.printStackTrace(new PrintWriter(writer))
            val exceptionMessage = writer.toString
            context.serviceManager.loggingService.logError(exceptionMessage)
            newThreadContext.close()
            HttpServerResponse(new ByteArrayInputStream(exceptionMessage.getBytes("UTF-8")), Map(CONTENT_TYPE_HEADER -> "text/plain"), () => {}, 500)
          }
        }
      })

    serverHandler match {
      case RunningStatus(host, port, _) => {
        manager.loggingService.logInfo(s"Http Server started at ${host}:${port}")
      }
      case FailedStatus(errorMessage) => {
        manager.loggingService.logError(s"Unable to start http server. Reason ${errorMessage}")
      }
    }

    var resultKeys: Seq[KeyValuePair] = Seq()

    resultKeys :+= KeyValuePair(KeyValue("running"), BooleanValue(value = serverHandler.running()))
    resultKeys :+= KeyValuePair(KeyValue("host"), StringValue(host))
    resultKeys :+= KeyValuePair(KeyValue("port"), NumberValue(port))
    resultKeys :+= KeyValuePair(KeyValue("stop"), new StopServerFunction(serverHandler))

    context.serviceManager.resourceManager.registerCloseable(serverHandler)

    ObjectValue(resultKeys, UnknownLocationCapable)
  }

  def toSeqObjectValue(headers: Seq[(String, String)]): ObjectValue = {
    ObjectValue(headers.map((entry) => {
      KeyValuePair(KeyValue(entry._1), StringValue(entry._2))
    }))
  }

  def toRequestObjectValue(request: HttpServerRequest): ObjectValue = {
    ObjectValue(
      Seq(
        KeyValuePair(KeyValue(BODY_KEY_NAME), HttpBodyValue(request)),
        KeyValuePair(KeyValue(METHOD_KEY_NAME), StringValue(request.method)),
        KeyValuePair(KeyValue(PATH_KEY_NAME), StringValue(request.path)),
        KeyValuePair(KeyValue(QUERY_PARAMS_KEY_NAME), toSeqObjectValue(request.queryParams)),
        KeyValuePair(KeyValue(HEADERS_KEY_NAME), toSeqObjectValue(request.headers))))
  }

  def toHttpResponse(value: Value[_], closeCallback: () => Unit)(implicit ctx: EvaluationContext): HttpServerResponse = {

    val httpResponseObject = ObjectType.coerce(value).evaluate.materialize()
    var headers: Map[String, String] = selectStringMap(httpResponseObject, HEADERS_KEY_NAME).getOrElse(Map())
    val headersMap = new util.TreeMap[String, String](String.CASE_INSENSITIVE_ORDER)
    headersMap.putAll(headers.asJava)
    val bodyStream: Option[InputStream] = {
      select(httpResponseObject, BODY_KEY_NAME).map {
        case httpBodyValue: HttpBodyValue => {
          httpBodyValue.sourceProvider.asInputStream
        }
        case binary if binary.valueType.isInstanceOf(BinaryType) => {
          BinaryType.coerce(binary).evaluate
        }
        case body => {
          val contentType = headersMap.get(CONTENT_TYPE_HEADER)
          if (contentType != null) {
            DataFormatManager.byContentType(contentType) match {
              case Some(dataFormat) => {
                val writer = dataFormat.writer(None)
                writer.startDocument(value)
                writer.writeValue(body)
                writer.endDocument(value)
                writer.close()
                writer.result match {
                  case is: AutoPersistedOutputStream => {
                    val stream = is.toInputStream
                    val theSize = stream.size()
                    headers = headers.+(CONTENT_LENGTH_HEADER -> theSize.toString)
                    stream
                  }
                  case _: AnyRef => {
                    throw new InvalidHttpBodyException(this.location())
                  }
                }
              }
              case None => {
                BinaryType.coerce(body).evaluate
              }
            }
          } else {
            BinaryType.coerce(body).evaluate
          }
        }
      }
    }
    val statusCode = selectInt(httpResponseObject, STATUS_CODE_KEY_NAME).getOrElse(200)
    val stream: InputStream = bodyStream match {
      case Some(inputStream) => {
        inputStream
      }
      case None => {
        //Set content length to 0
        headers = headers.+(CONTENT_LENGTH_HEADER -> "0")
        EMPTY_INPUT_STREAM
      }
    }

    HttpServerResponse(stream, headers, closeCallback, statusCode)
  }
}

class StopServerFunction(serverHandler: HttpServerStatus) extends EmptyFunctionValue {
  override def doExecute()(implicit ctx: EvaluationContext): Value[_] = {
    val stopped = serverHandler.stop()
    if (stopped) {
      ctx.serviceManager.loggingService.logInfo(s"Stopped server")
    } else {
      ctx.serviceManager.loggingService.logError(s"Error while stopping server")
    }
    BooleanValue(stopped)
  }
}

object HttpServerFunction {
  val EMPTY_INPUT_STREAM = new ByteArrayInputStream(new Array[Byte](0))
  val BODY_KEY_NAME = "body"
  val HEADERS_KEY_NAME = "headers"
  val QUERY_PARAMS_KEY_NAME = "queryParams"
  val PATH_KEY_NAME = "path"
  val METHOD_KEY_NAME = "method"
  val STATUS_CODE_KEY_NAME = "status"
  val PORT_KEY_NAME = "port"
  val HOST_KEY_NAME = "host"
  val MAX_CONTENT_LENGTH = "maxContentLength"

}

