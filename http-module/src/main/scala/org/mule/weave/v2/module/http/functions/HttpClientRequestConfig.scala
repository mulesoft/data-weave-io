package org.mule.weave.v2.module.http.functions

import org.mule.weave.v2.core.util.ObjectValueUtils.selectBoolean
import org.mule.weave.v2.core.util.ObjectValueUtils.selectInt
import org.mule.weave.v2.model.EvaluationContext
import org.mule.weave.v2.model.structure.ObjectSeq

case class HttpClientRequestConfig(
  followRedirects: Boolean,
  readTimeout: Option[Int],
  requestTimeout: Option[Int],
  streamResponse: Boolean,
  enableMetrics: Boolean)

object HttpClientRequestConfig {
  private val FOLLOW_REDIRECTS = "followRedirects"
  private val READ_TIMEOUT = "readTimeout"
  private val REQUEST_TIMEOUT = "requestTimeout"
  private val STREAM_RESPONSE = "streamResponse"
  private val ENABLE_METRICS = "enableMetrics"

  def parse(config: ObjectSeq)(implicit ctx: EvaluationContext): HttpClientRequestConfig = {
    val followRedirects = selectBoolean(config, FOLLOW_REDIRECTS).getOrElse(false)
    val maybeReadTimeout = selectInt(config, READ_TIMEOUT)
    val maybeRequestTimeout = selectInt(config, REQUEST_TIMEOUT)
    val streamResponse = selectBoolean(config, STREAM_RESPONSE).getOrElse(false)
    val enableMetrics = selectBoolean(config, ENABLE_METRICS).getOrElse(false)
    HttpClientRequestConfig(followRedirects, maybeReadTimeout, maybeRequestTimeout, streamResponse, enableMetrics)
  }
}
