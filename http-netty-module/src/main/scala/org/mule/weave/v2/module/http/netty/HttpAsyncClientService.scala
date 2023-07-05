package org.mule.weave.v2.module.http.netty

import java.io.InputStream
import java.util.concurrent.CompletableFuture
import io.netty.handler.codec.http.HttpHeaders
import org.asynchttpclient.AsyncHttpClient
import org.asynchttpclient.Dsl._
import org.asynchttpclient.ListenableFuture
import org.asynchttpclient.RequestBuilder
import org.asynchttpclient.Response
import org.asynchttpclient.proxy.ProxyServer

import org.mule.weave.v2.core.model.ServiceRegistration
import org.mule.weave.v2.module.http.service.HttpClientHeaders
import org.mule.weave.v2.module.http.service.HttpClientOptions
import org.mule.weave.v2.module.http.service.HttpClientResponse
import org.mule.weave.v2.module.http.service.HttpClientService

import scala.collection.JavaConverters._

class HttpAsyncClientService extends HttpClientService {

  //Make it lazy don't create if it is not required
  lazy val client: AsyncHttpClient = asyncHttpClient()

  override def request(config: HttpClientOptions): CompletableFuture[_ <: HttpClientResponse] = {
    val builder = new RequestBuilder()
    builder.setUrl(config.url)
    builder.setMethod(config.method)

    builder.setFollowRedirect(config.allowRedirect)
    config.body.foreach((is) => builder.setBody(is))
    config.headers.foreach((header) => {
      builder.addHeader(header._1, header._2.asJava)
    })
    config.readTimeout.foreach((timeout) => builder.setReadTimeout(timeout))
    config.requestTimeout.foreach((timeout) => builder.setRequestTimeout(timeout))

    config.queryParams.foreach((qp) => {
      val paramName = qp._1
      paramName
    })

    config.proxyConfig.foreach((proxy) => {
      val server = new ProxyServer.Builder(proxy.host, proxy.port)
      //TODO configure proxy correctly
      builder.setProxyServer(server)
    })

    val value: ListenableFuture[Response] = client.executeRequest(builder)
    value.toCompletableFuture.thenApply[HttpAsyncResponse]((response) => {
      new HttpAsyncResponse(response)
    }).toCompletableFuture
  }

  def stop(): Unit = {
    client.close()
  }
}

class HttpAsyncResponse(response: Response) extends HttpClientResponse {

  /** Example: 200 */
  override def status: Int = {
    response.getStatusCode
  }

  def contentType: String = {
    response.getContentType
  }

  /** Response headers * */
  override def headers: HttpClientHeaders = {
    new HttpAsyncHeaders(response.getHeaders)
  }

  /** Response's raw body */
  override def body: Option[InputStream] = {
    Option(response.getResponseBodyAsStream)
  }

  override def statusText: Option[String] = {
    Option(response.getStatusText)
  }

}

class HttpAsyncHeaders(headers: HttpHeaders) extends HttpClientHeaders {

  override def headerNames: Array[String] = {
    val names = headers.names()
    names.toArray(new Array[String](names.size()))
  }

  override def headerValues(name: String): Array[String] = {
    val values = headers.getAll(name)
    values.toArray(new Array[String](values.size()))
  }

  override def headerValue(name: String): Option[String] = {
    Option(headers.get(name))
  }
}

class HttpClientServiceRegistration extends ServiceRegistration[HttpClientService] {
  //Re-use same http client
  private val httpAsyncClientService = new HttpAsyncClientService()

  override def service: Class[HttpClientService] = classOf[HttpClientService]

  override def implementation: HttpAsyncClientService = {
    httpAsyncClientService
  }
}
