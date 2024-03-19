package org.mule.weave.v2.module.http.functions.utils

import org.mule.weave.v2.core.util.ObjectValueUtils.selectInt
import org.mule.weave.v2.model.structure.ObjectSeq
import org.mule.weave.v2.module.http.functions.utils.HttpClientConfigurationConverter.CONNECTION_TIMEOUT
import org.mule.weave.v2.module.http.service.HttpClientConfiguration

class HttpClientConfigurationConverter {
  def convert(config: ObjectSeq)(implicit ctx: _root_.org.mule.weave.v2.model.EvaluationContext): HttpClientConfiguration = {
    val builder = new HttpClientConfiguration.Builder()
    selectInt(config, CONNECTION_TIMEOUT).foreach(connectionTimeout => builder.withConnectionTimeout(connectionTimeout))

    builder.build()
  }
}

object HttpClientConfigurationConverter {

  private val CONNECTION_TIMEOUT = "connectionTimeout"

  def apply(): HttpClientConfigurationConverter = new HttpClientConfigurationConverter()
}
