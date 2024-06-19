package org.mule.weave.v2.module.http.service

import org.mule.weave.v2.module.http.SimpleHttpClientResponse

import org.scalatest.freespec.AnyFreeSpec
import org.scalatest.matchers.should.Matchers

import java.util.Optional

class HttpClientResponseTest extends AnyFreeSpec with Matchers {

  "Empty Cookies and Location should be returned on null headers values" in {
    val status = 200

    val response = SimpleHttpClientResponse(status)

    response.getStatus shouldBe status
    response.getHeaders shouldBe null
    response.getCookies.size() shouldBe 0
    response.getLocation shouldBe Optional.empty()
  }
}