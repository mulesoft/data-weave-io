package org.mule.weave.v2.module.http.functions.utils

import org.mule.weave.v2.core.util.ObjectValueUtils.selectBoolean
import org.mule.weave.v2.core.util.ObjectValueUtils.selectInt
import org.mule.weave.v2.core.util.ObjectValueUtils.selectObject
import org.mule.weave.v2.model.EvaluationContext
import org.mule.weave.v2.model.structure.ObjectSeq
import org.mule.weave.v2.module.http.functions.utils.HttpClientConfigurationConverter.CONNECTION_TIMEOUT
import org.mule.weave.v2.module.http.functions.utils.HttpClientConfigurationConverter.TLS
import org.mule.weave.v2.module.http.functions.utils.TlsConfigurationConverter.INSECURE
import org.mule.weave.v2.module.http.service.HttpClientConfiguration
import org.mule.weave.v2.module.http.service.TlsConfiguration

class HttpClientConfigurationConverter(config: ObjectSeq) {
  def convert()(implicit ctx: EvaluationContext): HttpClientConfiguration = {
    val builder = new HttpClientConfiguration.Builder()
    selectInt(config, CONNECTION_TIMEOUT).foreach(connectionTimeout => builder.setConnectionTimeout(connectionTimeout))

    val tlsValue = selectObject(config, TLS)
    if (tlsValue.isDefined) {
      val tlsConfiguration = TlsConfigurationConverter(tlsValue.get, builder).convert()
      builder.setTlsConfiguration(tlsConfiguration)
    }

    builder.build()
  }
}

object HttpClientConfigurationConverter {
  private val CONNECTION_TIMEOUT = "connectionTimeout"
  private val TLS = "tsl"

  def apply(config: ObjectSeq): HttpClientConfigurationConverter = new HttpClientConfigurationConverter(config)
}


class TlsConfigurationConverter(config: ObjectSeq) {

  def convert()(implicit ctx: EvaluationContext): TlsConfiguration = {
    val builder = new TlsConfiguration.Builder()
    selectBoolean(config, INSECURE).foreach(insecure => builder.setInsecure(insecure))
    builder.build()
  }
}

object TlsConfigurationConverter {

  private val INSECURE = "insecure"

  def apply(config: ObjectSeq): TlsConfigurationConverter = new TlsConfigurationConverter(config)
}