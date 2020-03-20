package org.mule.weave.v2.module.http.undertow

import java.util
import java.util.HashMap

import io.undertow.Undertow
import io.undertow.server.HttpHandler
import io.undertow.server.HttpServerExchange
import io.undertow.server.handlers.encoding.EncodingHandler
import io.undertow.util.Headers
import io.undertow.util.HttpString
import io.undertow.util.Protocols
import org.mule.weave.v2.module.http.service.FailedStatus
import org.mule.weave.v2.module.http.service.HttpServerConfig
import org.mule.weave.v2.module.http.service.HttpServerRequest
import org.mule.weave.v2.module.http.service.HttpServerResponse
import org.mule.weave.v2.module.http.service.HttpServerService
import org.mule.weave.v2.module.http.service.HttpServerStatus
import org.mule.weave.v2.module.http.service.RunningStatus

import scala.collection.mutable.ArrayBuffer
import scala.util.Failure
import scala.util.Success
import scala.util.Try

class UndertowHttpServerService extends HttpServerService {

  override def server(config: HttpServerConfig, callback: (HttpServerRequest) => HttpServerResponse): HttpServerStatus = {
    val handlerWrapper = new EncodingHandler.Builder().build(new util.HashMap[String, AnyRef]())
    val handler = handlerWrapper.wrap(new UndertowServerHandler(callback))

    val server = Undertow.builder
      .addHttpListener(config.port, config.host)
      .setHandler(handler)
      .build

    val triedUnit = Try(server.start())
    triedUnit match {
      case Failure(exception) => {
        exception.printStackTrace()
        FailedStatus(exception.getMessage)
      }
      case Success(_) => {
        RunningStatus(config.host, config.port, () => server.stop())
      }
    }
  }
}

import org.mule.weave.v2.model.ServiceRegistration

class HttpServerServiceRegistration extends ServiceRegistration[HttpServerService] {
  override def service = classOf[HttpServerService]

  override def implementation = new UndertowHttpServerService()
}

class UndertowServerHandler(callback: (HttpServerRequest) => HttpServerResponse) extends HttpHandler {

  override def handleRequest(exchange: HttpServerExchange): Unit = {
    if (exchange.isInIoThread) {
      val upgrade = exchange.getRequestHeaders.get(Headers.UPGRADE)
      if (upgrade != null && upgrade.contains("h2c")) { // reset protocol
        exchange.setProtocol(Protocols.HTTP_1_1)
      }
      exchange.dispatch(this)
    } else {
      exchange.startBlocking()
      val request: HttpServerRequest = buildRequest(exchange)
      try {
        val response = callback.apply(request)
        exchange.setStatusCode(response.statusCode)
        response.headers.foreach({
          case (key, value) => {
            exchange.getResponseHeaders.add(new HttpString(key), value)
          }
        })
        if (response.body != null) {
          new InputStreamSender(response.body, exchange, response.closeCallBack).send()
        } else {
          exchange.endExchange()
        }
      } catch {
        //Only on failure endExchange
        case e: Error => {
          System.err.println("[ERROR] Unexpected error occurred.")
          e.printStackTrace()
          exchange.endExchange()
        }
      }
    }
  }

  private def buildRequest(exchange: HttpServerExchange) = {
    val body = exchange.getInputStream
    val requestMultiMap = exchange.getRequestHeaders
    val headers: ArrayBuffer[(String, String)] = ArrayBuffer[(String, String)]()
    requestMultiMap.forEach((headerValue) => {
      val headerName = headerValue.getHeaderName
      headerValue.toArray.foreach((value) => {
        headers.+=((headerName.toString, value))
      })
    })
    val queryParametersMultiMap = exchange.getQueryParameters
    val queryParams = ArrayBuffer[(String, String)]()
    queryParametersMultiMap.forEach((name, queue) => {
      queue.toArray.foreach((value) => {
        queryParams.+=((name, value.toString))
      })
    })
    val relativePath = exchange.getRelativePath
    HttpServerRequest(body, relativePath, exchange.getRequestMethod.toString, headers, queryParams)
  }
}
