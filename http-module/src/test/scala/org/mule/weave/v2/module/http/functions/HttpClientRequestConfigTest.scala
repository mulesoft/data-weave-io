package org.mule.weave.v2.module.http.functions

import org.mule.weave.v2.model.EvaluationContext
import org.mule.weave.v2.model.structure.KeyValuePair
import org.mule.weave.v2.model.structure.ObjectSeq
import org.mule.weave.v2.model.values.BooleanValue
import org.mule.weave.v2.model.values.KeyValue
import org.mule.weave.v2.model.values.NumberValue

import org.scalatest.freespec.AnyFreeSpec
import org.scalatest.matchers.should.Matchers

class HttpClientRequestConfigTest extends AnyFreeSpec with Matchers {

  "HttpClientRequestConfig" - {
    implicit val evaluationContext: EvaluationContext = EvaluationContext()

    "should parse configuration successfully" in {
      val followRedirects = true
      val readTimeout = 2000
      val requestTimeout = 1000

      val config = ObjectSeq(
        Seq(
          KeyValuePair(KeyValue("followRedirects"), BooleanValue(followRedirects)),
          KeyValuePair(KeyValue("readTimeout"), NumberValue(readTimeout)),
          KeyValuePair(KeyValue("requestTimeout"), NumberValue(requestTimeout))
        )
      )

      val requestConfig = HttpClientRequestConfig.parse(config)

      requestConfig.followRedirects shouldBe followRedirects
      requestConfig.readTimeout.get shouldBe readTimeout
      requestConfig.requestTimeout.get shouldBe requestTimeout
    }
  }

}
