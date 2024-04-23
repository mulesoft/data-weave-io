package org.mule.weave.v2.module.http.netty

import io.netty.handler.codec.http.HttpHeaders
import org.asynchttpclient.AsyncHttpClient
import org.asynchttpclient.ListenableFuture
import org.asynchttpclient.RequestBuilder
import org.asynchttpclient.Response
import org.mule.weave.v2.model.ServiceRegistration
import org.mule.weave.v2.module.http.service.HttpClient
import org.mule.weave.v2.module.http.service.HttpClientConfiguration
import org.mule.weave.v2.module.http.service.HttpClientHeaders
import org.mule.weave.v2.module.http.service.HttpClientRequest
import org.mule.weave.v2.module.http.service.HttpClientResponse
import org.mule.weave.v2.module.http.service.HttpClientService

import java.io.InputStream
import java.util
import java.util.Optional
import java.util.concurrent.CompletableFuture

class NettyHttpClientService extends HttpClientService {

  private lazy val httpClientRegistry = HttpClientRegistry()

  override def getClient(configuration: HttpClientConfiguration): HttpClient = {
    httpClientRegistry.get(configuration)
  }

  def stop(): Unit = {
    httpClientRegistry.stop()
  }
}

class NettyHttpClient(client: AsyncHttpClient) extends HttpClient {

  override def request(request: HttpClientRequest): CompletableFuture[HttpClientResponse] = {
    val builder = new RequestBuilder()
    builder.setUrl(request.getUrl)
    builder.setMethod(request.getMethod)

    builder.setFollowRedirect(request.isFollowRedirects)
    request.getBody.ifPresent(is => builder.setBody(is))
    request.getHeaders.forEach((name, values) => {
      builder.addHeader(name, values)
    })
    request.getReadTimeout.ifPresent(timeout => builder.setReadTimeout(timeout))
    request.getRequestTimeout.ifPresent(timeout => builder.setRequestTimeout(timeout))

    request.getQueryParams.forEach((name, values) => {
      values.forEach(value => {
        builder.addQueryParam(name, value)
      })
    })

    val value: ListenableFuture[Response] = client.executeRequest(builder)
    value.toCompletableFuture.thenApply[HttpClientResponse](response => {
      new HttpAsyncResponse(response)
    }).toCompletableFuture
  }

  def close(): Unit = {
    client.close()
  }
}

class HttpAsyncResponse(response: Response) extends HttpClientResponse {

  override def getStatus: Int = {
    response.getStatusCode
  }

  override def getStatusText: Optional[String] = {
    Optional.ofNullable(response.getStatusText)
  }

  override def getHeaders: HttpClientHeaders = {
    new HttpAsyncHeaders(response.getHeaders)
  }

  override def getContentType: Optional[String] = {
    Optional.ofNullable(response.getContentType)
  }

  override def getBody: Optional[InputStream] = {
    Optional.ofNullable(response.getResponseBodyAsStream)
  }
}

class HttpAsyncHeaders(headers: HttpHeaders) extends HttpClientHeaders {

  override def getHeaderNames: util.Set[String] = {
    headers.names()
  }

  override def getHeaderValues(name: String): util.List[String] = {
    headers.getAll(name)
  }

  override def getHeaderValue(name: String): Optional[String] = {
    Optional.ofNullable(headers.get(name))
  }
}

class HttpClientServiceRegistration extends ServiceRegistration[HttpClientService] {
  //Re-use same http client
  private val httpAsyncClientService = new NettyHttpClientService()

  override def service: Class[HttpClientService] = classOf[HttpClientService]

  override def implementation: NettyHttpClientService = {
    httpAsyncClientService
  }
}
