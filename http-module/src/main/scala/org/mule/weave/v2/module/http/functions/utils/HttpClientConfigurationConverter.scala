package org.mule.weave.v2.module.http.functions.utils

import org.mule.weave.v2.core.util.ObjectValueUtils.selectInt
import org.mule.weave.v2.core.util.ObjectValueUtils.selectString
import org.mule.weave.v2.model.EvaluationContext
import org.mule.weave.v2.model.structure.ObjectSeq
import org.mule.weave.v2.module.http.functions.utils.HttpClientConfigurationConverter.CONNECTION_TIMEOUT
import org.mule.weave.v2.module.http.functions.utils.HttpClientConfigurationConverter.ID
import org.mule.weave.v2.module.http.service.HttpClientConfiguration
import org.mule.weave.v2.parser.exception.WeaveRuntimeException
import org.mule.weave.v2.parser.location.LocationCapable

class HttpClientConfigurationConverter(config: ObjectSeq, location: LocationCapable) {
  def convert()(implicit ctx: EvaluationContext): HttpClientConfiguration = {
    val builder = new HttpClientConfiguration.Builder()
    val id = selectString(config, ID).getOrElse(throw new WeaveRuntimeException(s"Expecting $ID", location.location()))
    builder.setId(id)
    selectInt(config, CONNECTION_TIMEOUT).foreach(connectionTimeout => builder.setConnectionTimeout(connectionTimeout))

    builder.build()
  }
}

object HttpClientConfigurationConverter {

  private val ID = "id"
  private val CONNECTION_TIMEOUT = "connectionTimeout"

  def apply(config: ObjectSeq, location: LocationCapable): HttpClientConfigurationConverter = new HttpClientConfigurationConverter(config, location)
}
