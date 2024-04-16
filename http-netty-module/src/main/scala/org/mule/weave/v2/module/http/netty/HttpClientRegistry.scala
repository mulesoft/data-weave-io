package org.mule.weave.v2.module.http.netty

import org.asynchttpclient.Dsl.asyncHttpClient
import org.asynchttpclient.Dsl.config
import org.mule.weave.v2.module.http.service.HttpClientConfiguration

import java.io.IOException
import java.util.concurrent.ConcurrentHashMap

class HttpClientRegistry {

  private val cache = new ConcurrentHashMap[String, NettyHttpClient]()

  def get(configuration: HttpClientConfiguration): NettyHttpClient = {
    cache.computeIfAbsent(configuration.getId, _ => createClient(configuration))
  }

  private def createClient(configuration: HttpClientConfiguration): NettyHttpClient = {
    val asyncConfig = config()
    if (configuration.getConnectionTimeout.isPresent) {
      asyncConfig.setConnectTimeout(configuration.getConnectionTimeout.get())
    }
    val client = asyncHttpClient(asyncConfig)
    new NettyHttpClient(client)
  }

  def stop(): Unit = {
    this.cache.values().forEach(client => closeSilently(client))
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
