package org.mule.weave.v2.module.http

import org.mule.weave.v2.module.http.service.HttpClientHeaders
import org.mule.weave.v2.module.http.service.HttpClientResponse

import java.io.InputStream
import java.util.Optional

class SimpleHttpClientResponse(status: Int, statusText: String, headers: HttpClientHeaders, contentType: String, body: InputStream) extends HttpClientResponse {

  override def getStatus: Int = status

  override def getStatusText: Optional[String] = Optional.ofNullable(statusText)

  override def getHeaders: HttpClientHeaders = headers

  override def getContentType: Optional[String] = Optional.ofNullable(contentType)

  override def getBody: Optional[InputStream] = Optional.ofNullable(body)
}

object SimpleHttpClientResponse {

  def apply(status: Int, statusText: String = null, headers: HttpClientHeaders = null, contentType: String = null, body: InputStream = null): SimpleHttpClientResponse =
    new SimpleHttpClientResponse(status, statusText, headers, contentType, body)
}