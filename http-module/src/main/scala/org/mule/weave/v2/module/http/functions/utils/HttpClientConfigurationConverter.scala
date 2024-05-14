package org.mule.weave.v2.module.http.functions.utils

import org.mule.weave.v2.core.util.ObjectValueUtils.selectBoolean
import org.mule.weave.v2.core.util.ObjectValueUtils.selectInt
import org.mule.weave.v2.core.util.ObjectValueUtils.selectObject
import org.mule.weave.v2.model.EvaluationContext
import org.mule.weave.v2.model.structure.ObjectSeq
import org.mule.weave.v2.module.http.functions.utils.HttpClientConfigurationConverter.COMPRESSION_ENFORCED
import org.mule.weave.v2.module.http.functions.utils.HttpClientConfigurationConverter.CONNECTION_TIMEOUT
import org.mule.weave.v2.module.http.functions.utils.HttpClientConfigurationConverter.TLS
import org.mule.weave.v2.module.http.service.HttpClientConfiguration

class HttpClientConfigurationConverter(config: ObjectSeq) {
  def convert()(implicit ctx: EvaluationContext): HttpClientConfiguration = {
    val builder = new HttpClientConfiguration.Builder()
    selectInt(config, CONNECTION_TIMEOUT).foreach(connectionTimeout => builder.setConnectionTimeout(connectionTimeout))
    selectBoolean(config, COMPRESSION_ENFORCED).foreach(compressionEnforced => builder.setCompressionEnforced(compressionEnforced))

    val tlsValue = selectObject(config, TLS)
    if (tlsValue.isDefined) {
      val tlsConfiguration = TlsConfigurationConverter(tlsValue.get).convert()
      builder.setTlsConfiguration(tlsConfiguration)
    }
    builder.build()
  }
}

object HttpClientConfigurationConverter {
  private val CONNECTION_TIMEOUT = "connectionTimeout"
  private val COMPRESSION_ENFORCED = "compressionEnforced"
  private val TLS = "tls"

  def apply(config: ObjectSeq): HttpClientConfigurationConverter = new HttpClientConfigurationConverter(config)
}