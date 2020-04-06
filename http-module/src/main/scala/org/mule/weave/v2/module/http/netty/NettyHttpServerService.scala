package org.mule.weave.v2.module.http.netty

import java.io.IOException
import java.net.InetSocketAddress

import org.mule.weave.v2.module.http.service.HttpServerConfig
import org.mule.weave.v2.module.http.service.HttpServerRequest
import org.mule.weave.v2.module.http.service.HttpServerResponse
import org.mule.weave.v2.module.http.service.HttpServerService
import org.mule.weave.v2.module.http.service.HttpServerStatus
import io.netty.channel.nio.NioEventLoopGroup
import io.netty.bootstrap.ServerBootstrap
import io.netty.channel.ChannelFuture
import io.netty.channel.EventLoopGroup
import io.netty.channel.nio.NioEventLoopGroup
import io.netty.channel.socket.nio.NioServerSocketChannel
import io.netty.handler.logging.LogLevel
import io.netty.handler.logging.LogLevel.INFO
import io.netty.handler.logging.LoggingHandler
import org.mule.weave.v2.module.http.service.FailedStatus
import org.mule.weave.v2.module.http.service.RunningStatus

import scala.util.Failure
import scala.util.Success
import scala.util.Try

class NettyHttpServerService extends HttpServerService {

  /**
    * Starts a server wit the specified config
    *
    * @param config   The https server config
    * @param callback The callback handler for the requests
    */
  override def server(config: HttpServerConfig, callback: HttpServerRequest => HttpServerResponse): HttpServerStatus = {
    val eventLoopGroup = new NioEventLoopGroup()
    val bootstrap = new ServerBootstrap
    bootstrap.group(eventLoopGroup)
      .channel(classOf[NioServerSocketChannel])
      .localAddress(new InetSocketAddress(config.host, config.port))
      //      .handler(new LoggingHandler(INFO))
      .childHandler(new NettyHttpServerInitializer(callback))
    val triedUnit = Try(bootstrap.bind.sync)
    triedUnit match {
      case Failure(exception) => {
        exception.printStackTrace()
        FailedStatus(exception.getMessage)
      }
      case Success(serverChannelFuture: ChannelFuture) => {
        RunningStatus(config.host, config.port, () => {
          eventLoopGroup.shutdownGracefully()
          serverChannelFuture.channel.closeFuture.sync
        })
      }
    }
  }
}

import org.mule.weave.v2.model.ServiceRegistration

class HttpServerServiceRegistration extends ServiceRegistration[HttpServerService] {
  override def service = classOf[HttpServerService]

  override def implementation = new NettyHttpServerService()
}

