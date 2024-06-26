package org.mule.weave.v2.module.http.functions.utils

import org.mule.weave.v2.model.EvaluationContext
import org.mule.weave.v2.model.capabilities.UnknownLocationCapable
import org.mule.weave.v2.model.structure.KeyValuePair
import org.mule.weave.v2.model.structure.ObjectSeq
import org.mule.weave.v2.model.values.BinaryValue
import org.mule.weave.v2.model.values.KeyValue
import org.mule.weave.v2.model.values.ObjectValueBuilder
import org.mule.weave.v2.model.values.StringValue
import org.mule.weave.v2.module.http.functions.HttpClientRequestConfig
import org.mule.weave.v2.module.http.functions.exceptions.DuplicatedCookieFieldException
import org.mule.weave.v2.parser.exception.WeaveRuntimeException
import org.scalatest.freespec.AnyFreeSpec
import org.scalatest.matchers.should.Matchers

class HttpClientRequestConverterTest extends AnyFreeSpec with Matchers {

  "HttpClientRequestConverter" - {
    implicit val evaluationContext: EvaluationContext = EvaluationContext()

    val readTimeout = 1000
    val requestTimeout = 2000
    val requestConfig = HttpClientRequestConfig(followRedirects = true, Some(readTimeout), Some(requestTimeout))

    "should fail if 'method' or 'url' is not configure" in {
      var caught = intercept[WeaveRuntimeException] {
        val method = "GET"
        val request = ObjectSeq(
          Seq(KeyValuePair(KeyValue("method"), StringValue(method)))
        )
        HttpClientRequestConverter(request, requestConfig, UnknownLocationCapable).convert()
      }
      caught.getMessage shouldBe "Missing 'url' value"

      caught = intercept[WeaveRuntimeException] {
        val url = "http://domain"
        val request = ObjectSeq(
          Seq(KeyValuePair(KeyValue("url"), StringValue(url)))
        )
        HttpClientRequestConverter(request, requestConfig, UnknownLocationCapable).convert()
      }
      caught.getMessage shouldBe "Missing 'method' value"
    }

    "should parse request successfully" in {
      val method = "GET"
      val url = "http://domain"
      val queryParams = new ObjectValueBuilder()
        .addPair("param", "value")
        .build
      val urlObj = new ObjectValueBuilder()
        .addPair("url", url)
        .addPair("queryParams", queryParams)
        .build
      val headers = new ObjectValueBuilder()
        .addPair("header", "value")
        .build
      val cookies = new ObjectValueBuilder()
        .addPair("a", "A")
        .build
      val bytes = new Array[Byte](0)
      val request = ObjectSeq(
        Seq(
          KeyValuePair(KeyValue("method"), StringValue(method)),
          KeyValuePair(KeyValue("url"), urlObj),
          KeyValuePair(KeyValue("headers"), headers),
          KeyValuePair(KeyValue("cookies"), cookies),
          KeyValuePair(KeyValue("body"), BinaryValue(bytes))
        )
      )

      val clientRequest = HttpClientRequestConverter(request, requestConfig, UnknownLocationCapable).convert()

      clientRequest.getMethod shouldBe method
      clientRequest.getUrl shouldBe url
      clientRequest.getHeaders.size() shouldBe 2
      clientRequest.getHeaders.get("header").get(0) shouldBe "value"
      clientRequest.getHeaders.get("Cookie").get(0) shouldBe "a=A"
      clientRequest.getQueryParams.size() shouldBe 1
      clientRequest.getQueryParams.get("param").get(0) shouldBe "value"
      clientRequest.getBody should not be null
      clientRequest.isFollowRedirects shouldBe requestConfig.followRedirects
      clientRequest.getRequestTimeout shouldBe requestConfig.requestTimeout.get
      clientRequest.getReadTimeout shouldBe requestConfig.readTimeout.get
    }

    "should fail with duplicate 'cookie' field" in {
      val caught = intercept[DuplicatedCookieFieldException] {
        val method = "GET"
        val url = "http://domain"
        val urlObj = new ObjectValueBuilder()
          .addPair("url", url)
          .build
        val headers = new ObjectValueBuilder()
          .addPair("Cookie", "key=value")
          .build
        val cookies = new ObjectValueBuilder()
          .addPair("a", "A")
          .build
        val request = ObjectSeq(
          Seq(
            KeyValuePair(KeyValue("method"), StringValue(method)),
            KeyValuePair(KeyValue("url"), urlObj),
            KeyValuePair(KeyValue("headers"), headers),
            KeyValuePair(KeyValue("cookies"), cookies)
          )
        )
        HttpClientRequestConverter(request, requestConfig, UnknownLocationCapable).convert()
      }
      caught.getMessage should not be null
    }
  }
}
