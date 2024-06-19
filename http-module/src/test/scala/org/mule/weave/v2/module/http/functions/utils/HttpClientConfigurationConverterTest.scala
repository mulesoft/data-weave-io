package org.mule.weave.v2.module.http.functions.utils

import org.mule.weave.v2.model.EvaluationContext
import org.mule.weave.v2.model.structure.KeyValuePair
import org.mule.weave.v2.model.structure.ObjectSeq
import org.mule.weave.v2.model.values.BooleanValue
import org.mule.weave.v2.model.values.KeyValue
import org.mule.weave.v2.model.values.NumberValue
import org.mule.weave.v2.model.values.ObjectValueBuilder

import org.scalatest.freespec.AnyFreeSpec
import org.scalatest.matchers.should.Matchers

class HttpClientConfigurationConverterTest extends AnyFreeSpec with Matchers {

  "HttpClientConfigurationConverter" - {
    implicit val evaluationContext: EvaluationContext = EvaluationContext()

    "should parse configuration successfully" in {
      val connectionTimeout = 1000
      val compressionHeader = true
      val decompress = false
      val insecure = true
      val tls = new ObjectValueBuilder()
        .addPair("insecure", insecure)
        .build
      val config = ObjectSeq(
        Seq(
          KeyValuePair(KeyValue("connectionTimeout"), NumberValue(connectionTimeout)),
          KeyValuePair(KeyValue("compressionHeader"), BooleanValue(compressionHeader)),
          KeyValuePair(KeyValue("decompress"), BooleanValue(decompress)),
          KeyValuePair(KeyValue("tls"), tls)
        )
      )

      val configuration = HttpClientConfigurationConverter(config).convert()

      configuration.getConnectionTimeout shouldBe connectionTimeout
      configuration.isCompressionHeader shouldBe compressionHeader
      configuration.isDecompress shouldBe decompress
      configuration.getTlsConfiguration.isInsecure shouldBe insecure
    }
  }
}
