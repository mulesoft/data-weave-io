package org.mule.weave.v2.module.http.netty

import java.io.ByteArrayInputStream
import java.io.IOException
import java.io.InputStream
import java.net.InetSocketAddress

import io.netty.buffer.ByteBuf
import io.netty.buffer.ByteBufInputStream
import io.netty.buffer.Unpooled
import io.netty.buffer.Unpooled.copiedBuffer
import io.netty.channel.ChannelFuture
import io.netty.channel.ChannelFutureListener
import io.netty.channel.ChannelHandlerContext
import io.netty.channel.ChannelInboundHandlerAdapter
import io.netty.handler.codec.EmptyHeaders
import io.netty.handler.codec.http.DefaultFullHttpResponse
import io.netty.handler.codec.http.DefaultHttpHeaders
import io.netty.handler.codec.http.EmptyHttpHeaders
import io.netty.handler.codec.http.FullHttpRequest
import io.netty.handler.codec.http.HttpHeaders
import io.netty.handler.codec.http.HttpResponseStatus
import io.netty.handler.codec.http.HttpResponseStatus.valueOf
import io.netty.handler.codec.http.HttpVersion.HTTP_1_1
import org.mule.weave.v2.module.http.service.HttpServerRequest
import org.mule.weave.v2.module.http.service.HttpServerResponse

import scala.collection.mutable.ArrayBuffer

class HttpServiceHandler(callback: HttpServerRequest => HttpServerResponse) extends ChannelInboundHandlerAdapter {

  @throws[Exception]
  override def channelRead(ctx: ChannelHandlerContext, msg: Any): Unit = {
    val localAddress = ctx.channel.localAddress.asInstanceOf[InetSocketAddress]
    try if (msg.isInstanceOf[FullHttpRequest]) {
      val request = msg.asInstanceOf[FullHttpRequest]
      val httpRequest: HttpServerRequest = toHttpRequest(request)
      val httpResponse: HttpServerResponse = callback.apply(httpRequest)
      val nettyResponse = new DefaultFullHttpResponse(
        HTTP_1_1,
        valueOf(httpResponse.statusCode),
        toByteBuf(httpResponse.body), toHttpHeaders(httpResponse.headers), EmptyHttpHeaders.INSTANCE)

      ctx.writeAndFlush(nettyResponse).addListener(new ChannelFutureListener {
        override def operationComplete(future: ChannelFuture): Unit = {
          httpResponse.closeCallBack.apply()
          ctx.close()
        }
      })

    } finally {

    }
  }

  def toHttpRequest(request: FullHttpRequest): HttpServerRequest = {
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
  }

  @throws[Exception]
  override def channelReadComplete(ctx: ChannelHandlerContext): Unit = {
    ctx.flush
  }

}
