package org.mule.weave.v2.module.http.functions

import org.mule.weave.v2.core.util.ObjectValueUtils.selectString
import org.mule.weave.v2.core.util.ObjectValueUtils.selectStringAnyMap
import org.mule.weave.v2.model.EvaluationContext
import org.mule.weave.v2.model.structure.ObjectSeq
import org.mule.weave.v2.parser.exception.WeaveRuntimeException
import org.mule.weave.v2.parser.location.LocationCapable

case class SerializationConfig(
  contentType: String,
  readerProperties: Map[String, Any],
  writerProperties: Map[String, Any])

object SerializationConfig {
  private val CONTENT_TYPE = "contentType"
  private val READER_PROPERTIES = "readerProperties"
  private val WRITER_PROPERTIES = "writerProperties"

  def parse(config: ObjectSeq, location: LocationCapable)(implicit ctx: EvaluationContext): SerializationConfig = {
    val contentType = selectString(config, CONTENT_TYPE).getOrElse(throw new WeaveRuntimeException(s"Missing '$CONTENT_TYPE' value", location.location()))
    val readerProperties = selectStringAnyMap(config, READER_PROPERTIES).getOrElse(Map.empty)
    val writerProperties = selectStringAnyMap(config, WRITER_PROPERTIES).getOrElse(Map.empty)
    SerializationConfig(contentType, readerProperties, writerProperties)
  }
}
