package org.mule.weave.v2.module.http.service

import org.scalatest.freespec.AnyFreeSpec
import org.scalatest.matchers.should.Matchers

class HttpClientQueryParamsTest extends AnyFreeSpec with Matchers {

  "HttpClientQueryParams" - {
    "Empty params configuration should work" in {
      val params = new HttpClientQueryParams.Builder().build()
      params.getQueryParams.isEmpty shouldBe true
      params.names().isEmpty shouldBe true
      params.namesIgnoreCase().isEmpty shouldBe true
      params.allValues("any").isEmpty shouldBe true
      params.allValuesIgnoreCase("any").isEmpty shouldBe true
    }

    "Custom params configuration should work" in {
      val params = new HttpClientQueryParams.Builder()
        .addQueryParam("A", "a")
        .addQueryParam(HttpClientQueryParams.HttpQueryParam.of("B", "b"))
        .addQueryParam("a", "c")
        .build()
      params.getQueryParams.size() shouldBe 3
      params.names().size() shouldBe 3
      params.namesIgnoreCase().size() shouldBe 2
      params.allValues("a").get(0) shouldBe "c"
      params.allValuesIgnoreCase("a").size() shouldBe 2
    }
  }
}
