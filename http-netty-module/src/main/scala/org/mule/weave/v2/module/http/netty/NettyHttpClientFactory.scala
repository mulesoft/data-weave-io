package org.mule.weave.v2.module.http.netty

import org.asynchttpclient.Dsl.asyncHttpClient
import org.asynchttpclient.Dsl.config
import org.mule.weave.v2.core.exception.WeaveIllegalArgumentException
import org.mule.weave.v2.module.http.netty.cookie.EmptyCookieStore
import org.mule.weave.v2.module.http.service.HttpClientConfiguration
import org.mule.weave.v2.parser.location.UnknownLocation

object NettyHttpClientFactory {

  def create(configuration: HttpClientConfiguration): NettyHttpClient = {
    val asyncConfig = config()
    asyncConfig.setConnectTimeout(configuration.getConnectionTimeout)
    if (Option(configuration.getTlsConfiguration).isDefined) {
      if (configuration.getTlsConfiguration.isInsecure) {
        asyncConfig.setUseInsecureTrustManager(true)
      }
    }

    // Using empty cookie store
    asyncConfig.setCookieStore(EmptyCookieStore())

    // "com.mulesoft.dw.netty.client.honor_proxy_properties" allows disabling the system properties configuration
    // for proxy setup.
    if (!NettyHttpClientConfigProperties.HONOR_PROXY_PROPERTIES) {
      asyncConfig.setUseProxyProperties(false)
    } else {
      asyncConfig.setUseProxyProperties(true)
    }

    asyncConfig.setCompressionEnforced(configuration.isCompressionHeader)

    if (!configuration.isDecompress) {
      throw new WeaveIllegalArgumentException("Unsupported `decompress` value. Supported `decompress: true`", UnknownLocation)
    }
    val client = asyncHttpClient(asyncConfig)
    new NettyHttpClient(client)
  }
}