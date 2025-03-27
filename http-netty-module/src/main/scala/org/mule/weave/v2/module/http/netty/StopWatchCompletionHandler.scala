package org.mule.weave.v2.module.http.netty

import io.netty.channel.Channel
import io.netty.handler.codec.http.HttpHeaders
import org.asynchttpclient.AsyncHandler
import org.asynchttpclient.HttpResponseBodyPart
import org.asynchttpclient.HttpResponseStatus
import org.asynchttpclient.Response
import org.asynchttpclient.handler.BodyDeferringAsyncHandler
import org.asynchttpclient.netty.request.NettyRequest
import org.mule.weave.v2.module.http.functions.utils.StopWatch
import org.mule.weave.v2.module.http.netty.StopWatchCompletionHandler.CONNECT
import org.mule.weave.v2.module.http.netty.StopWatchCompletionHandler.DNS
import org.mule.weave.v2.module.http.netty.StopWatchCompletionHandler.RECEIVE
import org.mule.weave.v2.module.http.netty.StopWatchCompletionHandler.SEND
import org.mule.weave.v2.module.http.netty.StopWatchCompletionHandler.TLS
import org.mule.weave.v2.module.http.netty.StopWatchCompletionHandler.WAIT

import java.io.OutputStream
import java.net.InetSocketAddress
import java.util
import javax.net.ssl.SSLSession

class StopWatchCompletionHandler(stopWatch: StopWatch, outputStream: OutputStream) extends BodyDeferringAsyncHandler(outputStream) {

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

  override def onHeadersReceived(httpHeaders: HttpHeaders): AsyncHandler.State = super.onHeadersReceived(httpHeaders)

  override def onBodyPartReceived(httpResponseBodyPart: HttpResponseBodyPart): AsyncHandler.State = super.onBodyPartReceived(httpResponseBodyPart)

  override def onThrowable(throwable: Throwable): Unit = super.onThrowable(throwable)

  override def onCompleted(): Response = {
    stopWatch.registerTime(RECEIVE)
    stopWatch.stop()
    super.onCompleted()
  }
}

object StopWatchCompletionHandler {
  val DNS = "dns"
  val CONNECT = "connect"
  val TLS = "tls"
  val SEND = "send"
  val WAIT = "wait"
  val RECEIVE = "receive"

  def apply(stopWatch: StopWatch, outputStream: OutputStream): StopWatchCompletionHandler = new StopWatchCompletionHandler(stopWatch, outputStream)
}