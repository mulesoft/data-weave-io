package org.mule.weave.v2.module.http.service

import java.io.InputStream

trait HttpServerService {

  /**
    *
    * Starts a server wit the specified config
    *
    * @param config   The https server config
    * @param callback The callback handler for the requests
    */
  def server(config: HttpServerConfig, callback: (HttpServerRequest) => HttpServerResponse): HttpServerStatus
}

case class HttpServerConfig(port: Int, host: String) {}

case class HttpServerRequest(body: InputStream, path: String, method: String, headers: Seq[(String, String)], queryParams: Seq[(String, String)]) {}

case class HttpServerResponse(body: InputStream, headers: Map[String, String], closeCallBack: () => Unit, statusCode: Int = 200) {}

sealed trait HttpServerStatus extends AutoCloseable {
  def running(): Boolean

  /** Stops the server and returns true if it was successful */
  def stop(): Boolean

  /** Stops the server */
  def close(): Unit
}

case class RunningStatus(host: String, port: Int, closeDelegate: () => Unit) extends HttpServerStatus {
  override def running(): Boolean = true

  override def close() = {
    closeDelegate()
  }

  override def stop(): Boolean = {
    try {
      closeDelegate()
      true
    } catch {
      case _: Throwable => false
    }
  }
}

case class FailedStatus(errorMessage: String) extends HttpServerStatus {
  override def running(): Boolean = false

  override def stop(): Boolean = true

  override def close() = {}
}
