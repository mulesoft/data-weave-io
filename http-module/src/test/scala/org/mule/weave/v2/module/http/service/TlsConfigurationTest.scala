package org.mule.weave.v2.module.http.service

import org.scalatest.freespec.AnyFreeSpec
import org.scalatest.matchers.should.Matchers

class TlsConfigurationTest extends AnyFreeSpec with Matchers {

  "TlsConfiguration" - {
    "default configuration should be secure" in {
      val tlsConfiguration = new TlsConfiguration.Builder().build()
      tlsConfiguration.isInsecure shouldBe false
    }

    "allows insecure configuration" in {
      val insecure = true

      val tlsConfiguration = new TlsConfiguration.Builder()
        .setInsecure(insecure)
        .build()

      tlsConfiguration.isInsecure shouldBe insecure
    }

    "2 different configuration with same data should be equals" in {
      val builder = new TlsConfiguration.Builder()
        .setInsecure(true)

      val a = builder.build()
      val b = builder.build()
      val c = builder.setInsecure(false).build()

      a.equals(b) shouldBe true
      a.equals(c) shouldBe false
    }
  }
}
