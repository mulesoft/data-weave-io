package org.mule.weave.v2.module.http.netty

import org.asynchttpclient.AsyncHttpClient
import org.asynchttpclient.RequestBuilder
import org.asynchttpclient.Response
import org.asynchttpclient.handler.BodyDeferringAsyncHandler
import org.asynchttpclient.handler.BodyDeferringAsyncHandler.BodyDeferringInputStream
import org.mule.weave.v2.module.http.functions.utils.StopWatch
import org.mule.weave.v2.module.http.service.HttpClient
import org.mule.weave.v2.module.http.service.HttpClientHeaders
import org.mule.weave.v2.module.http.service.HttpClientRequest
import org.mule.weave.v2.module.http.service.HttpClientResponse
import org.mule.weave.v2.module.http.service.metadata.NumberMetadataValue
import org.mule.weave.v2.module.http.service.metadata.ObjectMetadataValue

import java.io.InputStream
import java.io.PipedInputStream
import java.io.PipedOutputStream
import java.util.Optional
import java.util.concurrent.CompletableFuture
import org.asynchttpclient.AsyncHandler
import org.asynchttpclient.AsyncCompletionHandler
import org.asynchttpclient.HttpResponseStatus
import org.asynchttpclient.HttpResponseBodyPart
import org.asynchttpclient.netty.request.NettyRequest
import java.net.InetSocketAddress
import java.util
import javax.net.ssl.SSLSession
import io.netty.channel.Channel
import io.netty.handler.codec.http.HttpHeaders

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

    if (request.isStreamResponse) {
      // Streaming response
      val pipedOutputStream = new PipedOutputStream()
      val pipedInputStream = new PipedInputStream(pipedOutputStream)
      val bodyDeferringHandler = StopWatchCompletionHandler(stopWatch, pipedOutputStream)
      val responseFuture = client.prepareRequest(builder).execute(bodyDeferringHandler)
      val response = bodyDeferringHandler.getResponse
      val inputStream = new BodyDeferringInputStream(responseFuture, bodyDeferringHandler, pipedInputStream)
      new NettyHttpClientResponse(response, inputStream, stopWatch)
    } else {
      // Non-streaming response - lazy consumption
      val handler = new StopWatchResponseHandler(stopWatch)
      val responseFuture = client.prepareRequest(builder).execute(handler)
      val response = responseFuture.get()
      val inputStream = new LazyResponseInputStream(response, stopWatch)
      new NettyHttpClientResponse(response, inputStream, stopWatch)
    }
  }

  def close(): Unit = {
    client.close()
  }

  def isClosed(): Boolean = {
    client.isClosed
  }
}

class LazyResponseInputStream(response: Response, stopWatch: StopWatch) extends InputStream {
  private var responseStream: InputStream = _

  private def getStream: InputStream = {
    if (responseStream == null) {
      stopWatch.registerTime("response_body_start")
      responseStream = response.getResponseBodyAsStream
      stopWatch.registerTime("response_body_end")
    }
    responseStream
  }

  override def read(): Int = getStream.read()

  override def read(b: Array[Byte], off: Int, len: Int): Int = getStream.read(b, off, len)

  override def available(): Int = getStream.available()

  override def close(): Unit = {
    if (responseStream != null) {
      responseStream.close()
    }
  }
}

class NettyHttpClientResponse(response: Response, bodyStream: InputStream, stopWatch: StopWatch) extends HttpClientResponse {

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
    Optional.ofNullable(bodyStream)
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