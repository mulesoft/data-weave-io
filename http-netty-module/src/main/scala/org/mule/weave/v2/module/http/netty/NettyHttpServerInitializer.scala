package org.mule.weave.v2.module.http.netty

import io.netty.channel.ChannelInitializer
import io.netty.channel.socket.SocketChannel
import io.netty.handler.codec.http.HttpObjectAggregator
import io.netty.handler.codec.http.HttpRequestDecoder
import io.netty.handler.codec.http.HttpResponseEncoder
import io.netty.handler.stream.ChunkedWriteHandler
import org.mule.weave.v2.module.http.service.HttpServerRequest
import org.mule.weave.v2.module.http.service.HttpServerResponse

class NettyHttpServerInitializer(callback: HttpServerRequest => HttpServerResponse) extends ChannelInitializer[SocketChannel] {

  def initChannel(ch: SocketChannel): Unit = {
    val pipeline = ch.pipeline

    pipeline.addLast(new HttpResponseEncoder)
    pipeline.addLast(new HttpRequestDecoder)
    // TODO: Evaluate removing this and just handling chunking at the request level too for better performance
    pipeline.addLast(new HttpObjectAggregator(65536))
    pipeline.addLast(new ChunkedWriteHandler)
    pipeline.addLast(new HttpServiceHandler(callback))
  }
}
