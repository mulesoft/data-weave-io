package org.mule.weave.v2.module.http

import org.mule.weave.v2.module.http.service.HttpClientHeaders

import java.util
import java.util.Optional

class SimpleHttpClientHeaders(headers: util.Map[String, util.List[String]]) extends HttpClientHeaders {

  override def getHeaderNames: util.Set[String] = headers.keySet()

  override def getHeaderValues(name: String): util.List[String] = headers.get(name)

  override def getHeaderValue(name: String): Optional[String] = {
    val header = headers.get(name)
    if (header != null) {
      header.stream().findFirst()
    } else {
      Optional.empty()
    }
  }
}

object SimpleHttpClientHeaders {
  def apply(headers: util.Map[String, util.List[String]]): SimpleHttpClientHeaders = new SimpleHttpClientHeaders(headers)
}