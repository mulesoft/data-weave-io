package org.mule.weave.v2.module.http.functions.utils

import org.mule.weave.v2.core.util.ObjectValueUtils
import org.mule.weave.v2.model.EvaluationContext
import org.mule.weave.v2.model.values.NumberValue
import org.mule.weave.v2.module.http.SimpleHttpClientHeaders
import org.mule.weave.v2.module.http.SimpleHttpClientResponse
import org.mule.weave.v2.module.http.service.HttpClientResponse

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
      val body = new ByteArrayInputStream(new String("Hi").getBytes)

      val headers = new util.HashMap[String, util.List[String]]()
      headers.put("header", Collections.singletonList("value"))
      headers.put(HttpClientResponse.SET_COOKIE, Collections.singletonList("cookie=value"))

      val httpClientResponse = SimpleHttpClientResponse(status, statusText, SimpleHttpClientHeaders(headers), contentType, body)
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

      val props = maybeSchema.get.properties()
      val maybeTotal = props.find(p => p.name.evaluate == "total")
      maybeTotal.isDefined shouldBe true
      val totalNumber = maybeTotal.get.value.materialize.asInstanceOf[NumberValue]
      assert(totalNumber.evaluate.toLong >= sleep)
    }

    "should avoid body field when body input stream is empty" in {
      val contentType = "application/json"
      val body = new ByteArrayInputStream(new Array[Byte](0))

      val headers = new util.HashMap[String, util.List[String]]()
      headers.put("Content-Type", Collections.singletonList("contentType"))

      val httpClientResponse = SimpleHttpClientResponse(200, "OK", SimpleHttpClientHeaders(headers), contentType, body)
      val response = HttpClientResponseConverter(httpClientResponse, StopWatch(on = true)).convert()
      val responseObj = response.evaluate
      // body
      val maybeBody = ObjectValueUtils.select(responseObj, "body")
      maybeBody.isDefined shouldBe false
    }
  }
}
