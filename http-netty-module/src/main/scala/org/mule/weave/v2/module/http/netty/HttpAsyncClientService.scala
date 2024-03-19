package org.mule.weave.v2.module.http.netty

import io.netty.handler.codec.http.HttpHeaders
import org.asynchttpclient.AsyncHttpClient
import org.asynchttpclient.Dsl._
import org.asynchttpclient.ListenableFuture
import org.asynchttpclient.RequestBuilder
import org.asynchttpclient.Response
import org.mule.weave.v2.model.ServiceRegistration
import org.mule.weave.v2.module.http.service.HttpClientConfiguration
import org.mule.weave.v2.module.http.service.HttpClientHeaders
import org.mule.weave.v2.module.http.service.HttpClientOptions
import org.mule.weave.v2.module.http.service.HttpClientRequest
import org.mule.weave.v2.module.http.service.HttpClientResponse
import org.mule.weave.v2.module.http.service.HttpClientService

import java.io.InputStream
import java.util
import java.util.Optional
import java.util.concurrent.CompletableFuture

class HttpAsyncClientService extends HttpClientService {

  //Make it lazy don't create if it is not required
  private lazy val client: AsyncHttpClient = asyncHttpClient()

  override def request(config: HttpClientOptions): CompletableFuture[HttpClientResponse] = {
    val builder = new RequestBuilder()
    builder.setUrl(config.getUrl)
    builder.setMethod(config.getMethod)

    builder.setFollowRedirect(config.isAllowRedirect)
    config.getBody.ifPresent((is) => builder.setBody(is))

    config.getHeaders.forEach((name, values) => {
      builder.addHeader(name, values)
    })

    config.getReadTimeout.ifPresent((timeout) => builder.setReadTimeout(timeout))
    config.getRequestTimeout.ifPresent((timeout) => builder.setRequestTimeout(timeout))

    config.getQueryParams.forEach((name, values) => {
      values.forEach(v => {
        builder.addQueryParam(name, v)
      })
    })

    val value: ListenableFuture[Response] = client.executeRequest(builder)
    value.toCompletableFuture.thenApply[HttpClientResponse](response => {
      new HttpAsyncResponse(response)
    })
  }

  override def sendRequest(request: HttpClientRequest, configuration: HttpClientConfiguration): HttpAsyncResponse = {
    val builder = new RequestBuilder()
    builder.setUrl(request.getUrl)
    builder.setMethod(request.getMethod)
    getAsyncHttpClient(configuration).executeRequest(builder).toCompletableFuture
      .thenApply[HttpAsyncResponse](response => {
        new HttpAsyncResponse(response)
      })
      .get()
  }

  def stop(): Unit = {
    client.close()
  }

  private def getAsyncHttpClient(configuration: HttpClientConfiguration): AsyncHttpClient = {
    if (configuration.getConnectionTimeout.isPresent) {
      asyncHttpClient(config()
        .setConnectTimeout(configuration.getConnectionTimeout.get()))
    } else {
      client
    }
  }
}

class HttpAsyncResponse(response: Response) extends HttpClientResponse {

  /** Example: 200 */
  override def getStatus: Int = {
    response.getStatusCode
  }

  override def getContentType: Optional[String] = {
    Optional.ofNullable(response.getContentType)
  }

  /** Response headers * */
  override def getHeaders: HttpClientHeaders = {
    new HttpAsyncHeaders(response.getHeaders)
  }

  /** Response's raw body */
  override def getBody: Optional[InputStream] = {
    Optional.ofNullable(response.getResponseBodyAsStream)
  }

  override def getStatusText: Optional[String] = {
    Optional.ofNullable(response.getStatusText)
  }

}

class HttpAsyncHeaders(headers: HttpHeaders) extends HttpClientHeaders {

  override def getHeaderNames: util.Set[String] = {
    val names = headers.names()
    names
  }

  override def getHeaderValues(name: String): util.List[String] = {
    val values = headers.getAll(name)
    values
  }

  override def getHeaderValue(name: String): Optional[String] = {
    Optional.ofNullable(headers.get(name))
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
