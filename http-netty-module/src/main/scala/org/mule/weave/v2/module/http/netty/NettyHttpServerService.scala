package org.mule.weave.v2.module.http.netty

import java.net.InetSocketAddress

import org.mule.weave.v2.module.http.service.HttpServerConfig
import org.mule.weave.v2.module.http.service.HttpServerRequest
import org.mule.weave.v2.module.http.service.HttpServerResponse
import org.mule.weave.v2.module.http.service.HttpServerService
import org.mule.weave.v2.module.http.service.HttpServerStatus
import io.netty.bootstrap.ServerBootstrap
import io.netty.channel.ChannelFuture
import io.netty.channel.nio.NioEventLoopGroup
import io.netty.channel.socket.nio.NioServerSocketChannel

import org.mule.weave.v2.model.ServiceRegistration
import org.mule.weave.v2.module.http.service.FailedStatus
import org.mule.weave.v2.module.http.service.RunningStatus

import scala.util.Failure
import scala.util.Success
import scala.util.Try

class NettyHttpServerService extends HttpServerService {

  /**
    * Starts a Netty server with the specified config
    *
    * @param config   The https server config
    * @param callback The callback handler for the requests
    */
  override def server(config: HttpServerConfig, callback: HttpServerRequest => HttpServerResponse): HttpServerStatus = {
    // TODO: Play with these thread counts, some people suggest using just 1 acceptor thread
    // TODO: Investigate OS specific implementations like io.netty.channel.epoll.EpollEventLoopGroup for linux
    val boss = new NioEventLoopGroup()
    val workers = new NioEventLoopGroup()
    val bootstrap = new ServerBootstrap
    bootstrap.group(boss, workers)
      .channel(classOf[NioServerSocketChannel])
      .localAddress(new InetSocketAddress(config.host, config.port))
      .childHandler(new NettyHttpServerInitializer(config, callback))

    // TODO: Validate this is not blocking everything and the behavior for the status is correct
    val triedUnit = Try(bootstrap.bind.sync)
    triedUnit match {
      case Failure(exception) => {
        exception.printStackTrace()
        FailedStatus(exception.getMessage)
      }
      case Success(serverChannelFuture: ChannelFuture) => {
        RunningStatus(config.host, config.port, () => {
          println("Stopping server.")
          boss.shutdownGracefully()
          workers.shutdownGracefully()
          serverChannelFuture.channel.closeFuture.sync
          println("Server stopped.")
        })
      }
    }
  }
}

class HttpServerServiceRegistration extends ServiceRegistration[HttpServerService] {
  override def service = classOf[HttpServerService]

  override def implementation = new NettyHttpServerService()
}

