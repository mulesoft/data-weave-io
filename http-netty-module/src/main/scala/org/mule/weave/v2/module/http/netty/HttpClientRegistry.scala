package org.mule.weave.v2.module.http.netty

import org.mule.weave.v2.cache.service.Cache
import org.mule.weave.v2.module.http.service.HttpClientConfiguration

import java.io.IOException

class HttpClientRegistry {

  private var cache = Cache.builder()
    .build[HttpClientConfiguration, NettyHttpClient]()

  def get(configuration: HttpClientConfiguration): NettyHttpClient = {
    cache.get(configuration, _ => NettyHttpClientFactory.create(configuration))
  }

  def cleanup(): Unit = {
    // Close all client
    val clients = cache.asMap().values()
    clients.forEach(client => closeSilently(client))
    // Clear cache
    cache = Cache.builder()
      .build[HttpClientConfiguration, NettyHttpClient]()
  }

  private def closeSilently(client: NettyHttpClient): Unit = {
    try {
      client.close()
    } catch {
      case _: IOException =>
      // Nothing to do
    }
  }
}

object HttpClientRegistry {
  def apply(): HttpClientRegistry = new HttpClientRegistry()
}
