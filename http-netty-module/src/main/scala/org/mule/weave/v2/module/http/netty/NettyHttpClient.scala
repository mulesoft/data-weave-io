package org.mule.weave.v2.module.http.netty

import io.netty.handler.codec.http.HttpHeaders
import org.asynchttpclient.AsyncHttpClient
import org.asynchttpclient.RequestBuilder
import org.asynchttpclient.Response
import org.mule.weave.v2.module.http.functions.utils.StopWatch
import org.mule.weave.v2.module.http.service.HttpClient
import org.mule.weave.v2.module.http.service.HttpClientHeaders
import org.mule.weave.v2.module.http.service.HttpClientRequest
import org.mule.weave.v2.module.http.service.HttpClientResponse
import org.mule.weave.v2.module.http.service.metadata.NumberMetadataValue
import org.mule.weave.v2.module.http.service.metadata.ObjectMetadataValue

import java.io.InputStream
import java.util
import java.util.Optional

class NettyHttpClient(client: AsyncHttpClient) extends HttpClient {

  override def request(request: HttpClientRequest): HttpClientResponse = {
    val stopWatch = StopWatch(on = true)

    val builder = new RequestBuilder()
    builder.setUrl(request.getUrl)
    builder.setMethod(request.getMethod)
    request.getQueryParams.forEach((name, values) => {
      values.forEach(value => {
        builder.addQueryParam(name, value)
      })
    })

    request.getHeaders.forEach((name, values) => {
      builder.addHeader(name, values)
    })

    if (Option(request.getBody).isDefined) {
      builder.setBody(request.getBody)
    }

    builder.setFollowRedirect(request.isFollowRedirects)
    builder.setReadTimeout(request.getReadTimeout)
    builder.setRequestTimeout(request.getRequestTimeout)

    client.executeRequest(builder, StopWatchCompletionHandler(stopWatch))
      .toCompletableFuture
      .thenApply[HttpClientResponse](response => {
        new NettyHttpClientResponse(response, stopWatch)
      })
      .get()
  }

  def close(): Unit = {
    client.close()
  }
}

class NettyHttpClientResponse(response: Response, stopWatch: StopWatch) extends HttpClientResponse {

  private val TIMERS_KEY = "timers"

  override def getStatus: Int = {
    response.getStatusCode
  }

  override def getStatusText: Optional[String] = {
    Optional.ofNullable(response.getStatusText)
  }

  override def getHeaders: HttpClientHeaders = {
    new NettyHttpClientHeaders(response.getHeaders)
  }

  override def getContentType: Optional[String] = {
    Optional.ofNullable(response.getContentType)
  }

  override def getBody: Optional[InputStream] = {
    // response.hasResponseBody
    Optional.ofNullable(response.getResponseBodyAsStream)
  }

  override def getMetadata: Optional[ObjectMetadataValue] = {
    // Forcing stop
    stopWatch.stop()

    val timersBuilder = new ObjectMetadataValue.Builder()
    stopWatch.getTimes.foreach(time => {
      timersBuilder.addKeyValuePair(time._1, new NumberMetadataValue(time._2.toString))
    })
    val builder = new ObjectMetadataValue.Builder()
    builder.addKeyValuePair(TIMERS_KEY, timersBuilder.build())
    val metadata = builder.build()
    Optional.ofNullable(metadata)
  }
}

class NettyHttpClientHeaders(headers: HttpHeaders) extends HttpClientHeaders {

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