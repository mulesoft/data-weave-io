package org.mule.weave.v2.module.http.service

import org.scalatest.freespec.AnyFreeSpec
import org.scalatest.matchers.should.Matchers

class HttpClientConfigurationTest extends AnyFreeSpec with Matchers {

  "HttpClientConfiguration" - {
    "default configuration should work" in {
      val configuration = new HttpClientConfiguration.Builder()
        .build()

      configuration.getConnectionTimeout shouldBe 5000
      configuration.isCompressionHeader shouldBe false
      configuration.isDecompress shouldBe true
      configuration.getTlsConfiguration shouldBe null
    }

    "custom configuration should work" in {
      val connectionTimeout = -1
      val compressionHeader = true
      val decompress = false
      val insecure = true
      val tls = new TlsConfiguration.Builder()
        .setInsecure(insecure)
        .build()

      val configuration = new HttpClientConfiguration.Builder()
        .setConnectionTimeout(connectionTimeout)
        .setCompressionHeader(compressionHeader)
        .setDecompress(decompress)
        .setTlsConfiguration(tls)
        .build()

      configuration.getConnectionTimeout shouldBe connectionTimeout
      configuration.isCompressionHeader shouldBe compressionHeader
      configuration.isDecompress shouldBe decompress
      configuration.getTlsConfiguration.isInsecure shouldBe insecure
    }

    "2 different configuration with same data should be equals" in {
      val tls = new TlsConfiguration.Builder()
        .setInsecure(true)
        .build()

      val builder = new HttpClientConfiguration.Builder()
        .setConnectionTimeout(-1)
        .setCompressionHeader(true)
        .setDecompress(false)
        .setTlsConfiguration(tls)

      val a = builder.build()
      val b = builder.build()
      val c = builder.setConnectionTimeout(500).build()

      a.equals(b) shouldBe true
      a.equals(c) shouldBe false
    }
  }

}
