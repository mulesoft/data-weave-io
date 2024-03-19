package org.mule.weave.v2.module.http.functions.utils

import org.mule.weave.v2.core.util.ObjectValueUtils.selectString
import org.mule.weave.v2.model.structure.ObjectSeq
import org.mule.weave.v2.module.http.functions.utils.HttpClientRequestConverter.METHOD
import org.mule.weave.v2.module.http.functions.utils.HttpClientRequestConverter.URL
import org.mule.weave.v2.module.http.service.HttpClientRequest
import org.mule.weave.v2.parser.exception.WeaveRuntimeException
import org.mule.weave.v2.parser.location.UnknownLocation

class HttpClientRequestConverter {

  def convert(requestObjectSeq: ObjectSeq)(implicit ctx: _root_.org.mule.weave.v2.model.EvaluationContext): HttpClientRequest = {
    val url = selectString(requestObjectSeq, URL).getOrElse(throw new WeaveRuntimeException(s"Expecting $URL", UnknownLocation))
    val method = selectString(requestObjectSeq, METHOD).getOrElse(throw new WeaveRuntimeException(s"Expecting $METHOD", UnknownLocation))

    new HttpClientRequest.Builder()
      .withUrl(url)
      .withMethod(method)
      .build()
  }
}

object HttpClientRequestConverter {

  private val URL = "url"
  private val METHOD = "method"

  def apply(): HttpClientRequestConverter = new HttpClientRequestConverter()
}
