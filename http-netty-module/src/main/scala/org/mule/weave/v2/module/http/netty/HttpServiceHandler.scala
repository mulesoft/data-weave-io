package org.mule.weave.v2.module.http.netty

import io.netty.buffer.ByteBuf
import io.netty.buffer.ByteBufInputStream
import io.netty.buffer.Unpooled
import io.netty.channel.ChannelFuture
import io.netty.channel.ChannelHandler.Sharable
import io.netty.channel.ChannelHandlerContext
import io.netty.channel.SimpleChannelInboundHandler
import io.netty.handler.codec.http.DefaultHttpHeaders
import io.netty.handler.codec.http.DefaultHttpResponse
import io.netty.handler.codec.http.FullHttpRequest
import io.netty.handler.codec.http.HttpChunkedInput
import io.netty.handler.codec.http.HttpHeaderNames
import io.netty.handler.codec.http.HttpHeaderNames.CONTENT_LENGTH
import io.netty.handler.codec.http.HttpHeaderNames.TRANSFER_ENCODING
import io.netty.handler.codec.http.HttpHeaderValues
import io.netty.handler.codec.http.HttpHeaderValues.CHUNKED
import io.netty.handler.codec.http.HttpHeaders
import io.netty.handler.codec.http.HttpResponseStatus.valueOf
import io.netty.handler.codec.http.HttpUtil
import io.netty.handler.codec.http.HttpVersion.HTTP_1_1
import io.netty.handler.stream.ChunkedStream
import org.mule.weave.v2.module.http.service.HttpServerRequest
import org.mule.weave.v2.module.http.service.HttpServerResponse

import java.io.IOException
import java.io.InputStream
import scala.collection.mutable.ArrayBuffer

@Sharable
class HttpServiceHandler(callback: HttpServerRequest => HttpServerResponse) extends SimpleChannelInboundHandler[FullHttpRequest] {

  @throws[Exception]
  protected def channelRead0(ctx: ChannelHandlerContext, request: FullHttpRequest): Unit = {

    val weaveRequest: HttpServerRequest = toWeaveRequest(request)
    val weaveResponse: HttpServerResponse = callback.apply(weaveRequest)
    try {
      val headers = toHttpHeaders(weaveResponse.headers)
      val contentLength = headers.contains(CONTENT_LENGTH)
      val keepAlive = HttpUtil.isKeepAlive(request)
      if (!keepAlive) {
        headers.set(HttpHeaderNames.CONNECTION, HttpHeaderValues.CLOSE)
      }

      // TODO: Check whether we can enforce the content length at the weave level
      val contentFuture = if (!contentLength) {
        headers.set(TRANSFER_ENCODING, CHUNKED)
        val headerResponse = new DefaultHttpResponse(HTTP_1_1, valueOf(weaveResponse.statusCode), headers)
        ctx.write(headerResponse)
        ctx.writeAndFlush(new HttpChunkedInput(new ChunkedStream(weaveResponse.body)))
      } else {
        val headerResponse = new DefaultHttpResponse(HTTP_1_1, valueOf(weaveResponse.statusCode), headers)
        ctx.write(headerResponse)
        ctx.writeAndFlush(new HttpChunkedInput(new WeaveChunkedStream(weaveResponse.body, headers.get(CONTENT_LENGTH).toLong)), ctx.newProgressivePromise());
      }

      contentFuture.addListener((future: ChannelFuture) => {
        weaveResponse.closeCallBack.apply()
      })

      if (!keepAlive) {
        contentFuture.addListener((future: ChannelFuture) => {
          future.channel().close()
        })
      }
    } catch {
      case _: Exception => {
        //We should close this when an exception happens
        weaveResponse.closeCallBack.apply()
      }
    }
  }

  def toWeaveRequest(request: FullHttpRequest): HttpServerRequest = {
    val uriParts = request.uri().split('?').iterator
    val path = uriParts.next()
    val queryString = if (uriParts.hasNext) uriParts.next() else ""
    HttpServerRequest(new ByteBufInputStream(request.content()), path, request.method.name, fromHttpHeaders(request.headers()), fromQueryString(queryString))
  }

  def toHttpHeaders(headers: Map[String, String]): HttpHeaders = {
    headers.foldLeft(new DefaultHttpHeaders()) {
      case (httpHeaders, (key, value)) =>
        httpHeaders.add(key, value).asInstanceOf[DefaultHttpHeaders]
    }
  }

  def fromHttpHeaders(httpHeaders: HttpHeaders): Seq[(String, String)] = {
    val headers = ArrayBuffer[(String, String)]()
    httpHeaders.forEach(entry => headers.+=((entry.getKey, entry.getValue)))
    headers
  }

  // TODO: Use io.netty.handler.codec.http.QueryStringDecoder instead
  def fromQueryString(queryString: String): Seq[(String, String)] = {
    val queryParams = ArrayBuffer[(String, String)]()
    queryString.split('&').foreach(query => {
      val valueIndex = query.indexOf("=")
      if (valueIndex > 0) {
        queryParams.+=((query.substring(0, valueIndex), query.substring(valueIndex + 1)))
      } else {
        queryParams.+=((query, ""))
      }
    })
    queryParams
  }

  @throws[IOException]
  def toByteBuf(input: InputStream): ByteBuf = {
    try {
      // TODO: Check whether there's a better way of doing this stream to byte buf handling
      if (input != null) {
        val buf = Unpooled.buffer
        var n = 0
        do n = buf.writeBytes(input, 1024) while ({
          n > 0
        })
        buf
      } else {
        Unpooled.buffer(0)
      }
    } finally {
      input.close()
    }
  }

  @throws[Exception]
  override def channelReadComplete(ctx: ChannelHandlerContext): Unit = {
    ctx.flush
  }

}
