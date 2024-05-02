package org.mule.weave.io.http.mock

import io.vertx.core.AsyncResult
import io.vertx.core.Context
import io.vertx.core.Handler
import io.vertx.core.Promise
import io.vertx.core.Verticle
import io.vertx.core.Vertx
import io.vertx.core.http.HttpMethod
import io.vertx.core.http.HttpServer
import io.vertx.ext.web.Router
import io.vertx.ext.web.RoutingContext
import io.vertx.ext.web.handler.BodyHandler
import io.vertx.ext.web.handler.LoggerHandler

import java.util.concurrent.CountDownLatch
import java.util.concurrent.TimeUnit

class MockServer(val config: MockServerConfig) extends Verticle {

  private val vertx = Vertx.vertx()
  private var server: HttpServer = _

  override def getVertx: Vertx = {
    vertx
  }

  override def init(vertx: Vertx, context: Context): Unit = {}

  override def start(startPromise: Promise[Void]): Unit = {
    val router = Router.router(vertx)

    router
      .route()
      .handler(LoggerHandler.create())
      .handler(BodyHandler.create())

    config.handlers.foreach(h => {
      val method = HttpMethod.valueOf(h.method)
      router.route(method, h.path).handler(h.routingHandler.getHandler())
    })

    server = vertx.createHttpServer()
      .requestHandler(router)
      .listen(config.port, new Handler[AsyncResult[HttpServer]]() {
        override def handle(event: AsyncResult[HttpServer]): Unit = {
          startPromise.handle(event.mapEmpty())
        }
      })
  }

  override def stop(stopPromise: Promise[Void]): Unit = {
    server.close().onComplete(stopPromise)
    server = null
  }

  def startServer(): Unit = {
    val latch = new CountDownLatch(1)
    vertx.deployVerticle(this, new Handler[AsyncResult[String]]() {
      override def handle(event: AsyncResult[String]): Unit = {
        latch.countDown()
        if (event.failed()) {
          throw new RuntimeException("MockServer couldn't be started.", event.cause())
        }
      }
    })
    try {
      if (!latch.await(10, TimeUnit.SECONDS)) {
        throw new RuntimeException("Timeout waiting for server to start")
      }
    } catch {
      case e: InterruptedException =>
        throw new RuntimeException(e)
    }
  }

  def stopServer(): Unit = {
    vertx.close()
  }
}

case class MockServerConfig(port: Int, handlers: Seq[MockServerRoutingHandler] = Seq.empty)

case class MockServerRoutingHandler(method: String, path: String, routingHandler: RoutingHandler)

trait RoutingHandler {
  def getHandler(): Handler[RoutingContext]
}