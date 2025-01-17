package org.mule.weave.v2.module.http.functions.utils

import org.mule.weave.v2.module.http.SimpleHttpClientHeaders
import org.mule.weave.v2.module.http.SimpleHttpClientResponse
import org.mule.weave.v2.module.http.service.HttpClientConfiguration
import org.mule.weave.v2.module.http.service.HttpClientRequest
import org.mule.weave.v2.module.http.service.HttpClientResponse
import org.mule.weave.v2.module.http.service.TlsConfiguration
import org.scalatest.freespec.AnyFreeSpec
import org.scalatest.matchers.should.Matchers

import java.io.ByteArrayInputStream
import java.util
import java.util.Collections

class HttpClientLoggingUtilTest extends AnyFreeSpec with Matchers {

  "HttpClientLoggingUtil" - {
    "`appendClientConfiguration` should works" in {
      val expected = "connectionTimeout: 5000 (ms), compressionHeader: false, decompress: true, tls: {insecure: false}"

      val config = new HttpClientConfiguration.Builder()
        .setTlsConfiguration(new TlsConfiguration.Builder().build())
        .build()

      val buffer = new StringBuilder()
      HttpClientLoggingUtil.appendClientConfiguration(buffer, config)

      buffer.toString() shouldBe expected
    }

    "`appendRequest` should works" in {
      val expected = "url: (GET) http://domain?p=value, Content-Type: application/json, with-body: false, config: {followRedirects: false, requestTimeout: 60000 (ms), readTimeout: 60000 (ms)}"

      val config = new HttpClientRequest.Builder()
        .setUrl("http://domain")
        .setMethod("GET")
        .addQueryParam("p", "value")
        .addHeader("Content-Type", "application/json")
        .build()

      val buffer = new StringBuilder()
      HttpClientLoggingUtil.appendRequest(buffer, config)

      buffer.toString() shouldBe expected
    }

    "`appendResponse` should works" in {
      val expected = "status: 200 (OK), Content-Type: application/json, with-body: true"

      val status = 200
      val statusText = "OK"
      val contentType = "application/json"
      val content = new String("Hi").getBytes
      val body = new ByteArrayInputStream(content)
      val headers = new util.HashMap[String, util.List[String]]()
      headers.put("header", Collections.singletonList("value"))
      headers.put("Content-Length", Collections.singletonList(content.length.toString))
      headers.put(HttpClientResponse.SET_COOKIE, Collections.singletonList("cookie=value"))
      val httpClientResponse = SimpleHttpClientResponse(status, statusText, new SimpleHttpClientHeaders(headers), contentType, body)

      val buffer = new StringBuilder()
      HttpClientLoggingUtil.appendResponse(buffer, httpClientResponse)

      buffer.toString() shouldBe expected
    }
  }

}
