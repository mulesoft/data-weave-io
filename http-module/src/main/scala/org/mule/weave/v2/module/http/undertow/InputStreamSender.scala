package org.mule.weave.v2.module.http.undertow

import java.io.IOException
import java.io.InputStream
import java.nio.ByteBuffer

import io.undertow.UndertowLogger
import io.undertow.io.IoCallback
import io.undertow.io.Sender
import io.undertow.server.HttpServerExchange
import io.undertow.util.StatusCodes
import org.xnio.IoUtils

import scala.util.Try

class InputStreamSender(inputStream: InputStream, exchange: HttpServerExchange, closeCallBack: () => Unit, start: Int = -1, end: Int = -1, range: Boolean = false, completionCallback: IoCallback = IoCallback.END_EXCHANGE) extends Runnable with IoCallback {

  private val buffer = new Array[Byte](1024)
  private var toSkip = start
  private val remaining = end - start + 1
  private val sender = exchange.getResponseSender

  def send(): Unit = {
    if (exchange.isInIoThread) exchange.dispatch(this)
    else run()
  }

  override def run(): Unit = {
    if (range && remaining == 0) { //we are done, just return
      onComplete()
      return
    }
    try {
      var res = inputStream.read(buffer)
      if (res == -1) {
        onComplete()
        return
      }
      var bufferStart = 0
      var length: Int = res
      if (range && toSkip > 0) { //skip to the start of the requested range
        //not super efficient, but what can you do
        while (toSkip > res) {
          toSkip -= res
          res = inputStream.read(buffer)
          if (res == -1) {
            onComplete()
            return
          }
        }
        bufferStart = toSkip
        length -= toSkip
        toSkip = 0
      }
      if (range && length > remaining) length = remaining.toInt
      sender.send(ByteBuffer.wrap(buffer, bufferStart, length), this)
    } catch {
      case e: IOException =>
        onException(exchange, sender, e)
      case e: Error =>
        exchange.setStatusCode(StatusCodes.INTERNAL_SERVER_ERROR)
        onComplete()
    }
  }

  private def onComplete(): Unit = {
    IoUtils.safeClose(inputStream)
    Try(closeCallBack())
    Try(completionCallback.onComplete(exchange, sender))
  }

  override def onComplete(exchange: HttpServerExchange, sender: Sender): Unit = {
    if (exchange.isInIoThread) {
      exchange.dispatch(this)
    } else {
      run()
    }
  }

  override def onException(exchange: HttpServerExchange, sender: Sender, exception: IOException): Unit = {
    UndertowLogger.REQUEST_IO_LOGGER.ioException(exception)
    IoUtils.safeClose(inputStream)
    closeCallBack()
    if (!exchange.isResponseStarted) exchange.setStatusCode(StatusCodes.INTERNAL_SERVER_ERROR)
    completionCallback.onException(exchange, sender, exception)
  }
}
