package org.mule.weave.v2.module.http.netty

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
import java.util.Optional

class NettyHttpClient(client: AsyncHttpClient) extends HttpClient {

  override def request(request: HttpClientRequest): HttpClientResponse = {
    val stopWatch = StopWatch(on = true)

    val builder = new RequestBuilder()
    builder.setUrl(request.getUrl)
    builder.setMethod(request.getMethod)

    if (request.getQueryParams != null) {
      request.getQueryParams.getQueryParams.forEach(q => {
        builder.addQueryParam(q.getName, q.getValue)
      })
    }

    if (request.getHeaders != null) {
      request.getHeaders.getHeaders.forEach(h => {
        builder.addHeader(h.getName, h.getValue)
      })
    }

    if (request.getBody != null) {
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

  private lazy val _headers = {
    val builder = new HttpClientHeaders.Builder()
    val responseHeaders = response.getHeaders
    if (responseHeaders != null) {
      val iterator = responseHeaders.iteratorAsString()
      while (iterator.hasNext) {
        val next = iterator.next()
        builder.addHeader(next.getKey, next.getValue)
      }
    }
    builder.build()
  }

  override def getStatus: Int = {
    response.getStatusCode
  }

  override def getStatusText: Optional[String] = {
    Optional.ofNullable(response.getStatusText)
  }

  override def getHeaders: HttpClientHeaders = {
    _headers
  }

  override def getContentType: Optional[String] = {
    Optional.ofNullable(response.getContentType)
  }

  override def getBody: Optional[InputStream] = {
    if (response.hasResponseBody) {
      Optional.ofNullable(response.getResponseBodyAsStream)
    } else {
      Optional.empty()
    }
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