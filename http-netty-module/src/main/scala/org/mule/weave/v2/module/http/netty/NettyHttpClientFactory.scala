package org.mule.weave.v2.module.http.netty

import org.asynchttpclient.Dsl.asyncHttpClient
import org.asynchttpclient.Dsl.config
import org.mule.weave.v2.core.exception.WeaveIllegalArgumentException
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
    asyncConfig.setCompressionEnforced(configuration.isAcceptEncodingCompressionHeader)

    if (!configuration.isDecompress) {
      throw new WeaveIllegalArgumentException("Unsupported `decompress` value. Supported `decompress: true`", UnknownLocation)
    }
    val client = asyncHttpClient(asyncConfig)
    new NettyHttpClient(client)
  }
}