package org.mule.weave.io.http.mock.handler

import io.vertx.core.Handler
import io.vertx.ext.web.RoutingContext
import org.mule.weave.io.http.mock.RoutingHandler

class SimpleRoutingHandler(config: SimpleRoutingHandlerConfig) extends RoutingHandler
  with Handler[RoutingContext] {

  override def getHandler(): Handler[RoutingContext] = this

  override def handle(context: RoutingContext): Unit = {
    val httpResponse = context.response()
      .setStatusCode(config.statusCode)
    
    config.headers.foreach(h => {
      h._2.foreach(value => {
        httpResponse.putHeader(h._1, value)
      })
    })

    httpResponse.end(config.response)
  }
}

object SimpleRoutingHandler {
  def apply(config: SimpleRoutingHandlerConfig): SimpleRoutingHandler = new SimpleRoutingHandler(config)
}

case class SimpleRoutingHandlerConfig(statusCode: Int = 200, headers: Map[String, Seq[String]] = Map.empty, response: String)


