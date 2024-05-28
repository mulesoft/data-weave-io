package org.mule.weave.v2.module.http.netty

import io.netty.channel.Channel
import org.asynchttpclient.AsyncCompletionHandler
import org.asynchttpclient.AsyncHandler
import org.asynchttpclient.HttpResponseStatus
import org.asynchttpclient.Response
import org.asynchttpclient.netty.request.NettyRequest
import org.mule.weave.v2.module.http.functions.utils.StopWatch
import org.mule.weave.v2.module.http.netty.StopWatchCompletionHandler.CONNECT
import org.mule.weave.v2.module.http.netty.StopWatchCompletionHandler.DNS
import org.mule.weave.v2.module.http.netty.StopWatchCompletionHandler.RECEIVE
import org.mule.weave.v2.module.http.netty.StopWatchCompletionHandler.SEND
import org.mule.weave.v2.module.http.netty.StopWatchCompletionHandler.TLS
import org.mule.weave.v2.module.http.netty.StopWatchCompletionHandler.WAIT

import java.net.InetSocketAddress
import java.util
import javax.net.ssl.SSLSession

class StopWatchCompletionHandler(stopWatch: StopWatch) extends AsyncCompletionHandler[Response] {

  override def onHostnameResolutionSuccess(name: String, addresses: util.List[InetSocketAddress]): Unit = {
    stopWatch.registerTime(DNS)
    super.onHostnameResolutionSuccess(name, addresses)
  }

  override def onTcpConnectSuccess(remoteAddress: InetSocketAddress, connection: Channel): Unit = {
    stopWatch.registerTime(CONNECT)
    super.onTcpConnectSuccess(remoteAddress, connection)
  }

  override def onConnectionPooled(connection: Channel): Unit = {
    stopWatch.registerTime(CONNECT)
    super.onConnectionPooled(connection)
  }

  override def onTlsHandshakeSuccess(sslSession: SSLSession): Unit = {
    stopWatch.registerTime(TLS)
    super.onTlsHandshakeSuccess(sslSession)
  }

  override def onRequestSend(request: NettyRequest): Unit = {
    stopWatch.registerTime(SEND)
    super.onRequestSend(request)
  }

  override def onStatusReceived(status: HttpResponseStatus): AsyncHandler.State = {
    stopWatch.registerTime(WAIT)
    super.onStatusReceived(status)
  }

  override def onCompleted(response: Response): Response = {
    stopWatch.registerTime(RECEIVE)
    stopWatch.stop()
    response
  }
}

object StopWatchCompletionHandler {
  val DNS = "dns"
  val CONNECT = "connect"
  val TLS = "tls"
  val SEND = "send"
  val WAIT = "wait"
  val RECEIVE = "receive"
  def apply(stopWatch: StopWatch): StopWatchCompletionHandler = new StopWatchCompletionHandler(stopWatch)
}