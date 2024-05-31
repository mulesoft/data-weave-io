package org.mule.weave.v2.module.http.functions.utils

import org.mule.weave.v2.core.util.ObjectValueUtils.selectBoolean
import org.mule.weave.v2.model.EvaluationContext
import org.mule.weave.v2.model.structure.ObjectSeq
import org.mule.weave.v2.module.http.functions.utils.TlsConfigurationConverter.INSECURE

import org.mule.weave.v2.module.http.service.TlsConfiguration

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