package org.mule.weave.io.http.mock.functions

import org.mule.weave.io.http.mock.MockServerConfig
import org.mule.weave.io.http.mock.MockServerRoutingHandler
import org.mule.weave.io.http.mock.handler.SimpleRoutingHandler
import org.mule.weave.io.http.mock.handler.SimpleRoutingHandlerConfig
import org.mule.weave.v2.core.util.ObjectValueUtils.select
import org.mule.weave.v2.core.util.ObjectValueUtils.selectInt
import org.mule.weave.v2.core.util.ObjectValueUtils.selectObject
import org.mule.weave.v2.core.util.ObjectValueUtils.selectString
import org.mule.weave.v2.model.EvaluationContext
import org.mule.weave.v2.model.structure.ObjectSeq
import org.mule.weave.v2.model.types.ArrayType
import org.mule.weave.v2.model.types.ObjectType
import org.mule.weave.v2.model.types.StringType
import org.mule.weave.v2.parser.exception.WeaveRuntimeException
import org.mule.weave.v2.parser.location.UnknownLocation

object MockServerConfigConverter {
  val PORT: String = "port"
  val HANDLERS: String = "handlers"

  def convert(config: ObjectSeq)(implicit ctx: EvaluationContext): MockServerConfig = {
    val port = selectInt(config, PORT).getOrElse(throw new WeaveRuntimeException(s"Expecting $PORT", UnknownLocation))
    val maybeHandlers = select(config, HANDLERS)
    val handlers = if (maybeHandlers.isDefined) {
      ArrayType
        .coerce(maybeHandlers.get)
        .evaluate
        .toSeq()
        .map(handler => {
          val handlerObj = ObjectType.coerce(handler).evaluate
          MockServerHandlerConvert.convert(handlerObj)
        })
    } else {
      Seq.empty
    }
    MockServerConfig(port, handlers)
  }
}

object MockServerHandlerConvert {
  val METHOD: String = "method"
  val PATH: String = "path"

  def convert(handlerConfig: ObjectSeq)(implicit ctx: EvaluationContext): MockServerRoutingHandler = {
    val method = selectString(handlerConfig, METHOD).getOrElse(throw new WeaveRuntimeException(s"Expecting $METHOD", UnknownLocation))
    val path = selectString(handlerConfig, PATH).getOrElse(throw new WeaveRuntimeException(s"Expecting $PATH", UnknownLocation))
    val handler = SimpleRoutingHandlerConvert.convert(handlerConfig)
    MockServerRoutingHandler(method, path, handler)
  }
}

object SimpleRoutingHandlerConvert {
  val STATUS: String = "status"
  val HEADERS: String = "headers"
  val RESPONSE: String = "response"

  def convert(handlerObj: ObjectSeq)(implicit ctx: EvaluationContext): SimpleRoutingHandler = {
    val statusCode = selectInt(handlerObj, STATUS).getOrElse(200)
    val headers = extractHeaders(handlerObj)
    val response = selectString(handlerObj, RESPONSE).getOrElse("")
    val config = SimpleRoutingHandlerConfig(statusCode, headers, response)
    SimpleRoutingHandler(config)
  }

  private def extractHeaders(request: ObjectSeq)(implicit ctx: EvaluationContext): Map[String, Seq[String]] = {
    var headers = Map.empty[String, Seq[String]]
    val headersValue = selectObject(request, HEADERS).getOrElse(ObjectSeq.empty)
    headersValue.toSeq().foreach(kvp => {
      val name = kvp._1.evaluate.name
      val value = StringType.coerce(kvp._2).evaluate.toString
      val maybeValues = headers.get(name)
      if (maybeValues.isDefined) {
        val values = maybeValues.get :+ value
        headers += (name -> values)
      } else {
        headers += (name -> Seq(value))
      }
    })
    headers
  }
}
