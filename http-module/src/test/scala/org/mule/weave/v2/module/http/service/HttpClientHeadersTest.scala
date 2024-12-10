package org.mule.weave.v2.module.http.service

import org.mule.weave.v2.module.http.service.HttpClientHeaders.HttpHeader
import org.scalatest.Assertion
import org.scalatest.freespec.AnyFreeSpec
import org.scalatest.matchers.should.Matchers

import java.util

class HttpClientHeadersTest extends AnyFreeSpec with Matchers {

  "HttpClientHeaders" - {
    "Empty headers configuration should work" in {
      def doTest(headers: HttpClientHeaders): Assertion = {
        headers.getHeaders.isEmpty shouldBe true
        headers.names().isEmpty shouldBe true
        headers.namesIgnoreCase().isEmpty shouldBe true
        headers.firstValue("any").isEmpty shouldBe true
        headers.firstValueIgnoreCase("any").isEmpty shouldBe true
        headers.allValues("any").isEmpty shouldBe true
        headers.allValuesIgnoreCase("any").isEmpty shouldBe true
      }
      val headers = new HttpClientHeaders.Builder()
        .build()
      doTest(headers)
      doTest(HttpClientHeaders.of(new util.HashMap[String, util.List[String]]()))
    }

    "Custom headers configuration should work" in {
      val headers = new HttpClientHeaders.Builder()
        .addHeader("Content-Type", "application/json")
        .addHeader(HttpHeader.of("CONTENT-TYPE", "application/xml"))
        .addHeader("content-type", "text/plain")
        .addHeader("content-type", "application/octet-stream")
        .build()
      headers.getHeaders.size() shouldBe 4
      headers.names().size() shouldBe 3
      headers.namesIgnoreCase().size() shouldBe 1
      headers.firstValue("content-type").get() shouldBe "text/plain"
      headers.firstValueIgnoreCase("content-type").get shouldBe "application/json"
      headers.allValues("CONTENT-TYPE").get(0) shouldBe "application/xml"
      headers.allValuesIgnoreCase("Content-Type").size() shouldBe 4
    }

    "HttpClientHeaders.of should work" in {
      val map = new util.HashMap[String, util.List[String]]()
      map.put("A", util.Collections.singletonList("a"))
      val values = new util.ArrayList[String]()
      values.add("B1")
      values.add("B2")
      map.put("b", values)

      val headers = HttpClientHeaders.of(map)
      headers.getHeaders.size() shouldBe 3
      headers.names().size() shouldBe 2
      headers.namesIgnoreCase().size() shouldBe 2
    }
  }
}
