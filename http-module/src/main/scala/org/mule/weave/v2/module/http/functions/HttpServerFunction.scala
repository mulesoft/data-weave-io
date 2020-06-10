package org.mule.weave.v2.module.http.functions

import java.io.ByteArrayInputStream
import java.io.InputStream
import java.io.PrintWriter
import java.io.StringWriter
import java.nio.charset.Charset
import java.util

import org.mule.weave.v2.core.functions.BinaryFunctionValue
import org.mule.weave.v2.core.functions.EmptyFunctionValue
import org.mule.weave.v2.interpreted.ExecutionContext
import org.mule.weave.v2.model.EvaluationContext
import org.mule.weave.v2.model.ServiceManager
import org.mule.weave.v2.model.capabilities.UnknownLocationCapable
import org.mule.weave.v2.model.structure.KeyValuePair
import org.mule.weave.v2.model.types.BinaryType
import org.mule.weave.v2.model.types.FunctionType
import org.mule.weave.v2.model.types.ObjectType
import org.mule.weave.v2.model.values.BinaryValue
import org.mule.weave.v2.model.values.BooleanValue
import org.mule.weave.v2.model.values.KeyValue
import org.mule.weave.v2.model.values.NumberValue
import org.mule.weave.v2.model.values.ObjectValue
import org.mule.weave.v2.model.values.StringValue
import org.mule.weave.v2.model.values.Value
import org.mule.weave.v2.model.values.ValuesHelper
import org.mule.weave.v2.model.values.wrappers.DelegateValue
import org.mule.weave.v2.module.DataFormatManager
import org.mule.weave.v2.module.MimeType
import org.mule.weave.v2.module.http.HttpHeader.CONTENT_TYPE_HEADER
import org.mule.weave.v2.module.http.functions.HttpServerFunction._
import org.mule.weave.v2.module.http.netty.NettyHttpServerService
import org.mule.weave.v2.module.http.service.FailedStatus
import org.mule.weave.v2.module.http.service.HttpServerConfig
import org.mule.weave.v2.module.http.service.HttpServerRequest
import org.mule.weave.v2.module.http.service.HttpServerResponse
import org.mule.weave.v2.module.http.service.HttpServerService
import org.mule.weave.v2.module.http.service.HttpServerStatus
import org.mule.weave.v2.module.http.service.RunningStatus
import org.mule.weave.v2.module.reader.AutoPersistedOutputStream
import org.mule.weave.v2.module.reader.SourceProvider
import org.mule.weave.v2.parser.location.Location
import org.mule.weave.v2.parser.location.SimpleLocation
import org.mule.weave.v2.util.ObjectValueUtils._

class HttpServerFunction extends BinaryFunctionValue {

  override val L = ObjectType

  override val R = FunctionType

  override def doExecute(value1: L.V, value2: R.V)(implicit context: EvaluationContext): Value[_] = {
    val config = ObjectType.coerce(value1)
    val callBack = FunctionType.coerce(value2)
    val manager: ServiceManager = context.serviceManager
    val httpServerService = manager.lookupCustomService(classOf[HttpServerService], new NettyHttpServerService())
    val configObject = config.materialize.evaluate
    val port = selectInt(configObject, PORT_KEY_NAME).getOrElse(8081)
    val host = selectString(configObject, HOST_KEY_NAME).getOrElse("localhost")

    val serverHandler: HttpServerStatus = httpServerService.server(
      HttpServerConfig(port, host),
      (request) => {
        val newThreadContext: ExecutionContext = context.asInstanceOf[ExecutionContext].spawnNewThread()
        try {
          val requestValue = toRequestObjectValue(request)
          val callbackResult = callBack.call(ValuesHelper.array(requestValue))(newThreadContext)
          toHttpResponse(callbackResult, () => newThreadContext.close())(newThreadContext)
        } catch {
          case e: Exception => {
            val writer = new StringWriter()
            e.printStackTrace(new PrintWriter(writer))
            val exceptionMessage = writer.toString
            context.serviceManager.loggingService.logError(exceptionMessage)
            newThreadContext.close()
            HttpServerResponse(new ByteArrayInputStream(exceptionMessage.getBytes("UTF-8")), Map(), () => {}, 500)
          }
        }
      })

    serverHandler match {
      case RunningStatus(host, port, _) => manager.loggingService.logInfo(s"Http Server started at ${host}:${port}")
      case FailedStatus(errorMessage)   => manager.loggingService.logError(s"Unable to start http server. Reason ${errorMessage}")
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
    import scala.collection.JavaConverters._
    val httpResponseObject = ObjectType.coerce(value).evaluate.materialize()
    val headers = selectStringMap(httpResponseObject, HEADERS_KEY_NAME).getOrElse(Map())
    val headersMap = new util.TreeMap[String, String](String.CASE_INSENSITIVE_ORDER)
    headersMap.putAll(headers.asJava)
    val bodyStream: Option[InputStream] = select(httpResponseObject, BODY_KEY_NAME).map {
      case httpBodyValue: HttpBodyValue                        => httpBodyValue.httpRequest.body
      case binary if binary.valueType.isInstanceOf(BinaryType) => BinaryType.coerce(binary).evaluate
      case body => {
        val contentType = headersMap.get(CONTENT_TYPE_HEADER)
        if (contentType != null) {
          DataFormatManager.byContentType(contentType) match {
            case Some(dataFormat) => {
              val writer = dataFormat.writer(None)
              writer.startDocument(value)
              writer.writeValue(body)
              writer.endDocument(value)
              writer.result match {
                case is: AutoPersistedOutputStream => is.toInputStream
                case a: AnyRef                     => new ByteArrayInputStream(String.valueOf(a).getBytes)
              }
            }
            case None => BinaryType.coerce(body).evaluate
          }
        } else {
          BinaryType.coerce(body).evaluate
        }
      }
    }
    val statusCode = selectInt(httpResponseObject, STATUS_CODE_KEY_NAME).getOrElse(200)
    HttpServerResponse(bodyStream.orNull, headers, closeCallback, statusCode)
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
  val BODY_KEY_NAME = "body"
  val HEADERS_KEY_NAME = "headers"
  val QUERY_PARAMS_KEY_NAME = "queryParams"
  val PATH_KEY_NAME = "path"
  val METHOD_KEY_NAME = "method"
  val STATUS_CODE_KEY_NAME = "status"
  val PORT_KEY_NAME = "port"
  val HOST_KEY_NAME = "host"

}

class HttpBodyValue(val httpRequest: HttpServerRequest) extends DelegateValue {

  var body: Value[Any] = _

  override def value(implicit ctx: EvaluationContext): Value[Any] = {
    if (body == null) {
      val mayBeContentType = httpRequest.headers.find((header) => header._1.equalsIgnoreCase(CONTENT_TYPE_HEADER))
      body = mayBeContentType match {
        case Some(contentType) => {
          val mimeType = MimeType.fromSimpleString(contentType._2)
          val mayBeCharset = mimeType.getCharset()
          val maybeFormat = DataFormatManager.byContentType(contentType._2)
          maybeFormat match {
            case Some(dataFormat) => {
              val sourceProvider = SourceProvider(httpRequest.body, mayBeCharset.map(Charset.forName).getOrElse(ctx.serviceManager.charsetProviderService.defaultCharset()))
              val reader = dataFormat.reader(sourceProvider)
              reader.read(BODY_KEY_NAME)
            }
            case None => BinaryValue(httpRequest.body)
          }
        }
        case None => BinaryValue(httpRequest.body)
      }
    }
    body
  }

  override def location(): Location = SimpleLocation(BODY_KEY_NAME)

}

object HttpBodyValue {
  def apply(httpRequest: HttpServerRequest): HttpBodyValue = new HttpBodyValue(httpRequest)
}
