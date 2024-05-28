package org.mule.weave.v2.module.http.netty

import io.netty.channel.Channel
import org.asynchttpclient.AsyncCompletionHandler
import org.asynchttpclient.AsyncHandler
import org.asynchttpclient.HttpResponseStatus
import org.asynchttpclient.Response
import org.asynchttpclient.netty.request.NettyRequest
import org.mule.weave.v2.module.http.functions.utils.StopWatch

import java.net.InetSocketAddress
import java.util
import javax.net.ssl.SSLSession

class StopWatchCompletionHandler(stopWatch: StopWatch) extends AsyncCompletionHandler[Response] {

  override def onHostnameResolutionSuccess(name: String, addresses: util.List[InetSocketAddress]): Unit = {
    stopWatch.registerTime("hostnameResolutionSuccess")
    super.onHostnameResolutionSuccess(name, addresses)
  }

  override def onTcpConnectSuccess(remoteAddress: InetSocketAddress, connection: Channel): Unit = {
    stopWatch.registerTime("tcpConnectSuccess")
    super.onTcpConnectSuccess(remoteAddress, connection)
  }

  override def onConnectionPooled(connection: Channel): Unit = {
    stopWatch.registerTime("connectionPooled")
    super.onConnectionPooled(connection)
  }

  override def onTlsHandshakeSuccess(sslSession: SSLSession): Unit = {
    stopWatch.registerTime("tlsHandshakeSuccess")
    super.onTlsHandshakeSuccess(sslSession)
  }

  override def onRequestSend(request: NettyRequest): Unit = {
    stopWatch.registerTime("requestSend")
    super.onRequestSend(request)
  }

  override def onStatusReceived(status: HttpResponseStatus): AsyncHandler.State = {
    stopWatch.registerTime("statusReceived")
    super.onStatusReceived(status)
  }

  override def onCompleted(response: Response): Response = {
    stopWatch.registerTime("completed")
    stopWatch.stop()
    response
  }
}