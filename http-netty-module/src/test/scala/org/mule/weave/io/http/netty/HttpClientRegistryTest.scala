package org.mule.weave.io.http.netty

import org.mule.weave.v2.module.http.netty.HttpClientRegistry
import org.mule.weave.v2.module.http.service.HttpClientConfiguration
import org.scalatest.BeforeAndAfterEach
import org.scalatest.freespec.AnyFreeSpec
import org.scalatest.matchers.should.Matchers

class HttpClientRegistryTest extends AnyFreeSpec
  with Matchers
  with BeforeAndAfterEach {

  private var registry: HttpClientRegistry = _

  override protected def beforeEach(): Unit = {
    registry = HttpClientRegistry()
    super.beforeEach()
  }

  override protected def afterEach(): Unit = {
    if (registry != null) {
      registry.cleanup()
    }
    super.afterEach()
  }

  "`HttpClientRegistry` should return the same client instance for the same client configuration" in {
    val configuration = new HttpClientConfiguration.Builder()
      .build()

    val client = registry.get(configuration)

    client should not be null

    // Creates an other configuration with the same arguments
    val configuration2 = new HttpClientConfiguration.Builder()
      .build()

    val client2 = registry.get(configuration2)

    client2 should not be null
    client2 shouldBe client
  }

  "`HttpClientRegistry` should return a different client instance for a different client configuration" in {
    val configuration = new HttpClientConfiguration.Builder()
      .setCompressionHeader(true)
      .build()

    val client = registry.get(configuration)

    client should not be null

    // Creates an other configuration with the different arguments
    val otherConfiguration = new HttpClientConfiguration.Builder()
      .setCompressionHeader(false)
      .build()

    val otherClient = registry.get(otherConfiguration)

    otherClient should not be null
    otherClient should not be client
  }

  "`HttpClientRegistry` cleanup should close client" in {
    val configuration = new HttpClientConfiguration.Builder()
      .build()

    val client = registry.get(configuration)

    client should not be null

    client.isClosed() shouldBe false

    registry.cleanup()

    client.isClosed() shouldBe true

    val otherClient = registry.get(configuration)
    otherClient should not be null
    otherClient should not be client
    otherClient.isClosed() shouldBe false
  }
}
