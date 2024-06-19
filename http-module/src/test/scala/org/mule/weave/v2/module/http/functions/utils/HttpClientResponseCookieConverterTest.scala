package org.mule.weave.v2.module.http.functions.utils

import org.mule.weave.v2.core.util.ObjectValueUtils
import org.mule.weave.v2.model.EvaluationContext

import org.scalatest.freespec.AnyFreeSpec
import org.scalatest.matchers.should.Matchers

import java.net.HttpCookie

class HttpClientResponseCookieConverterTest extends AnyFreeSpec with Matchers {

  "HttpClientResponseCookieConverter" - {

    implicit val evaluationContext: EvaluationContext = EvaluationContext()

    "should parse cookie successfully" in {
      val name = "cookie"
      val value = "value"
      val maxAge = 120
      val httpOnly = true
      val secure = true
      val domain = "domain"
      val comment = "comment"
      val path = "/path"

      val httpCookie = new HttpCookie(name, value)
      httpCookie.setMaxAge(maxAge)
      httpCookie.setHttpOnly(httpOnly)
      httpCookie.setSecure(secure)
      httpCookie.setDomain(domain)
      httpCookie.setComment(comment)
      httpCookie.setPath(path)

      val cookie = HttpClientResponseCookieConverter(httpCookie).convert()
      val cookieObj = cookie.evaluate

      val maybeName = ObjectValueUtils.selectString(cookieObj, "name")
      maybeName.get shouldBe name

      val maybeValue = ObjectValueUtils.selectString(cookieObj, "value")
      maybeValue.get shouldBe value

      val maybeMaxAge = ObjectValueUtils.selectNumber(cookieObj, "maxAge")
      maybeMaxAge.get.toLong shouldBe maxAge

      val maybeHttpOnly = ObjectValueUtils.selectBoolean(cookieObj, "httpOnly")
      maybeHttpOnly.get shouldBe httpOnly

      val maybeSecure = ObjectValueUtils.selectBoolean(cookieObj, "secure")
      maybeSecure.get shouldBe secure

      val maybeDomain = ObjectValueUtils.selectString(cookieObj, "domain")
      maybeDomain.get shouldBe domain

      val maybeComment = ObjectValueUtils.selectString(cookieObj, "comment")
      maybeComment.get shouldBe comment

      val maybePath = ObjectValueUtils.selectString(cookieObj, "path")
      maybePath.get shouldBe path
    }
  }
}
