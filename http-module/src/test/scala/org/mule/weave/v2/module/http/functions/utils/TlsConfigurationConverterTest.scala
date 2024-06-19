package org.mule.weave.v2.module.http.functions.utils

import org.mule.weave.v2.model.EvaluationContext
import org.mule.weave.v2.model.structure.KeyValuePair
import org.mule.weave.v2.model.structure.ObjectSeq
import org.mule.weave.v2.model.values.BooleanValue
import org.mule.weave.v2.model.values.KeyValue

import org.scalatest.freespec.AnyFreeSpec
import org.scalatest.matchers.should.Matchers

class TlsConfigurationConverterTest extends AnyFreeSpec with Matchers {

  "TlsConfigurationConverter" - {
    implicit val evaluationContext: EvaluationContext = EvaluationContext()

    "should parse configuration successfully" in {
      val insecure = false
      val config = ObjectSeq(
        KeyValuePair(KeyValue("insecure"), BooleanValue(insecure))
      )

      val configuration = TlsConfigurationConverter(config).convert()

      configuration.isInsecure shouldBe insecure
    }
  }
}
