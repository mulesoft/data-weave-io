package org.mule.weave.v2.module.http.functions.utils

import org.mule.weave.v2.core.util.ObjectValueUtils
import org.mule.weave.v2.model.EvaluationContext
import org.mule.weave.v2.model.values.NumberValue
import org.mule.weave.v2.module.http.SimpleHttpClientResponse
import org.mule.weave.v2.module.http.service.HttpClientHeaders
import org.mule.weave.v2.module.http.service.HttpClientResponse
import org.scalatest.Assertion
import org.scalatest.freespec.AnyFreeSpec
import org.scalatest.matchers.should.Matchers

import java.io.ByteArrayInputStream
import java.util
import java.util.Collections

class HttpClientResponseConverterTest extends AnyFreeSpec with Matchers {

  "HttpClientResponseConverter" - {
    implicit val evaluationContext: EvaluationContext = EvaluationContext()

    "should parse response successfully" in {
      val status = 200
      val statusText = "OK"
      val contentType = "application/json"
      val content = new String("Hi").getBytes
      val body = new ByteArrayInputStream(content)

      val headers = new util.HashMap[String, util.List[String]]()
      headers.put("header", Collections.singletonList("value"))
      headers.put("Content-Length", Collections.singletonList(content.length.toString))
      headers.put(HttpClientResponse.SET_COOKIE, Collections.singletonList("cookie=value"))

      val httpClientResponse = SimpleHttpClientResponse(status, statusText, HttpClientHeaders.of(headers), contentType, body)
      val stopWatch = StopWatch(on = true)
      val sleep = 1000
      Thread.sleep(sleep)

      val response = HttpClientResponseConverter(httpClientResponse, stopWatch).convert()
      val responseObj = response.evaluate

      // status
      val maybeStatus = ObjectValueUtils.selectInt(responseObj, "status")
      maybeStatus.get shouldBe status

      // status-text
      val maybeStatusText = ObjectValueUtils.selectString(responseObj, "statusText")
      maybeStatusText.get shouldBe statusText

      // headers
      val maybeHeaders = ObjectValueUtils.selectObject(responseObj, "headers")
      val headersObj = maybeHeaders.get.materialize()
      val maybeHeaderValue = ObjectValueUtils.selectString(headersObj, "header")
      maybeHeaderValue.get shouldBe "value"

      // body
      val maybeBody = ObjectValueUtils.select(responseObj, "body")
      maybeBody.isDefined shouldBe true

      // cookies
      val maybeCookies = ObjectValueUtils.selectObject(responseObj, "cookies")
      val cookiesObj = maybeCookies.get.materialize()
      val maybeCookie= ObjectValueUtils.selectObject(cookiesObj, "cookie")
      val cookieValueObj = maybeCookie.get.materialize()
      val maybeCookieValue = ObjectValueUtils.selectString(cookieValueObj, "value")
      maybeCookieValue.get shouldBe "value"

      // Content-Type
      val maybeContentType = ObjectValueUtils.selectString(responseObj, "contentType")
      maybeContentType.get shouldBe contentType

      // Metadata
      val maybeSchema = response.schema
      maybeSchema.isDefined shouldBe true
    }

    "should ignore body field when content-length header is 0" in {
      val contentType = "application/json"
      val body = new ByteArrayInputStream(new Array[Byte](0))

      val headers = new util.HashMap[String, util.List[String]]()
      headers.put("content-length", Collections.singletonList("0"))
      headers.put("Content-Type", Collections.singletonList(contentType))

      val httpClientResponse = SimpleHttpClientResponse(200, "OK", HttpClientHeaders.of(headers), contentType, body)
      val response = HttpClientResponseConverter(httpClientResponse, StopWatch(on = true)).convert()
      val responseObj = response.evaluate
      // body
      val maybeBody = ObjectValueUtils.select(responseObj, "body")
      maybeBody.isDefined shouldBe false
    }

    "should ignore body field when content-length is undefined and status code is one of (204, 205, 304)" in {
      def doTest(statusCode: Int, statusText: String): Assertion = {
        val contentType = "application/json"
        val body = new ByteArrayInputStream(new Array[Byte](0))
        val headers = new util.HashMap[String, util.List[String]]()
        headers.put("Content-Type", Collections.singletonList(contentType))

        val httpClientResponse = SimpleHttpClientResponse(statusCode, statusText, HttpClientHeaders.of(headers), contentType, body)
        val response = HttpClientResponseConverter(httpClientResponse, StopWatch(on = true)).convert()
        val responseObj = response.evaluate
        // body
        val maybeBody = ObjectValueUtils.select(responseObj, "body")
        maybeBody.isDefined shouldBe false
      }
      doTest(204, "N0_CONTENT")
      doTest(205, "RESET_CONTENT")
      doTest(304, "NOT_MODIFIED")
    }

    "should send body when field when content-length is undefined or not a number" in {
      def doTest(headers: util.Map[String, util.List[String]], contentType: String): Assertion = {
        val body = new ByteArrayInputStream(new Array[Byte](0))
        val httpClientResponse = SimpleHttpClientResponse(200, "OK", HttpClientHeaders.of(headers), contentType, body)
        val response = HttpClientResponseConverter(httpClientResponse, StopWatch(on = true)).convert()
        val responseObj = response.evaluate
        // body
        val maybeBody = ObjectValueUtils.select(responseObj, "body")
        maybeBody.isDefined shouldBe true
      }
      var headers = new util.HashMap[String, util.List[String]]()
      headers.put("Content-Type", Collections.singletonList("application/json"))
      // No content-length
      doTest(headers, "application/json")
      // Not a number content-length
      headers = new util.HashMap[String, util.List[String]]()
      headers.put("content-length", Collections.singletonList("Hi"))
      doTest(headers, "application/json")
    }
  }
}
