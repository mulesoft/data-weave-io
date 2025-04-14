package org.mule.weave.v2.module.http.service

import org.scalatest.freespec.AnyFreeSpec
import org.scalatest.matchers.should.Matchers

import java.io.ByteArrayInputStream

class HttpClientRequestTest extends AnyFreeSpec with Matchers {

  "HttpClientRequest" - {
    "should fail if 'method' or 'url' is not configure" in {
      var caught = intercept[NullPointerException] {
        new HttpClientRequest.Builder().setMethod("GET").build()
      }
      caught.getMessage shouldBe "http client request 'url' must not be null"

      caught = intercept[NullPointerException] {
        new HttpClientRequest.Builder().setUrl("url").build()
      }
      caught.getMessage shouldBe "http client request 'method' must not be null"
    }

    "default configuration should work" in {
      val method = "GET"
      val url = "http://domain"

      val request = new HttpClientRequest.Builder()
        .setMethod(method)
        .setUrl(url)
        .build()

      request.getMethod shouldBe method
      request.getUrl shouldBe url
      request.getHeaders.getHeaders.isEmpty shouldBe true
      request.getQueryParams.getQueryParams.isEmpty shouldBe true
      request.getBody shouldBe null
      request.isFollowRedirects shouldBe false
      request.getRequestTimeout shouldBe 60000
      request.getReadTimeout shouldBe 60000
      request.isStreamResponse shouldBe false
    }

    "custom configuration should work" in {
      val method = "GET"
      val url = "http://domain"
      val followRedirects = true
      val streamResponse = true
      val requestTimeout = 10000
      val readTimeout = 2000
      val bytes = new Array[Byte](0)

      val request = new HttpClientRequest.Builder()
        .setMethod(method)
        .setUrl(url)
        .addHeader("header", "value")
        .addQueryParam("param", "value")
        .setBody(new ByteArrayInputStream(bytes))
        .setFollowRedirect(followRedirects)
        .setRequestTimeout(requestTimeout)
        .setReadTimeout(readTimeout)
        .setStreamResponse(streamResponse)
        .build()

      request.getMethod shouldBe method
      request.getUrl shouldBe url
      request.getHeaders.getHeaders.size() shouldBe 1
      request.getHeaders.allValues("header").get(0) shouldBe "value"
      request.getQueryParams.getQueryParams.size() shouldBe 1
      request.getQueryParams.allValues("param").get(0) shouldBe "value"
      request.getBody should not be null
      request.isFollowRedirects shouldBe followRedirects
      request.getRequestTimeout shouldBe requestTimeout
      request.getReadTimeout shouldBe readTimeout
      request.isStreamResponse shouldBe streamResponse
    }
  }
}
