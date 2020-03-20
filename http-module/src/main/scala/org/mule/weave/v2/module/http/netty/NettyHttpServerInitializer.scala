package org.mule.weave.v2.module.http.netty

import io.netty.channel.ChannelInitializer
import io.netty.channel.socket.SocketChannel
import io.netty.handler.codec.http.HttpObjectAggregator
import io.netty.handler.codec.http.HttpServerCodec
import org.mule.weave.v2.module.http.service.HttpServerRequest
import org.mule.weave.v2.module.http.service.HttpServerResponse

class NettyHttpServerInitializer(callback: HttpServerRequest => HttpServerResponse) extends ChannelInitializer[SocketChannel] {

  def initChannel(ch: SocketChannel): Unit = {
    val pipeline = ch.pipeline

    pipeline.addLast(new HttpServerCodec())
    pipeline.addLast(new HttpObjectAggregator(8 * 2 << 10))
    pipeline.addLast(new HttpServiceHandler(callback))
  }
}
