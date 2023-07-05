package org.mule.weave.v2.module.http.values

import org.mule.weave.v2.core.model.EvaluationContext
import org.mule.weave.v2.core.model.structure.schema.Schema
import org.mule.weave.v2.core.model.structure.schema.SchemaProperty
import org.mule.weave.v2.core.model.types.Type
import org.mule.weave.v2.core.model.values.BinaryValue
import org.mule.weave.v2.core.model.values.NullValue
import org.mule.weave.v2.core.model.values.SchemaValue
import org.mule.weave.v2.core.model.values.StringValue
import org.mule.weave.v2.core.model.values.Value
import org.mule.weave.v2.core.model.values.wrappers.DelegateValue
import org.mule.weave.v2.core.module.DataFormatManager
import org.mule.weave.v2.core.module.reader.Reader
import org.mule.weave.v2.core.module.reader.SourceProvider
import org.mule.weave.v2.module.http.HttpHeader.CONTENT_TYPE_HEADER
import org.mule.weave.v2.module.http.service.HttpServerRequest
import org.mule.weave.v2.parser.location.Location
import org.mule.weave.v2.parser.location.SimpleLocation

class HttpBodyValue(val sourceProvider: SourceProvider, mayBeContentType: Option[String], readerProperties: Map[String, Any], location: Location) extends DelegateValue {

  var bodyValue: Value[Any] = _
  var valueTypeValue: Type = _
  var needsMaterialize: Boolean = false

  override def materialize(implicit ctx: EvaluationContext): Value[_] = {
    if (bodyValue == null) {
      needsMaterialize = true
      this
    } else {
      bodyValue.materialize
    }
  }

  override def value(implicit ctx: EvaluationContext): Value[Any] = {
    if (bodyValue == null) {
      bodyValue = mayBeContentType match {
        case Some(contentType) => {
          DataFormatManager.byContentType(contentType).map((df) => {
            val reader: Reader = df.reader(sourceProvider)
            readerProperties.foreach((ro) => {
              reader.setOption(location, ro._1, ro._2)
            })
            reader.read(location.locationString)
          }).getOrElse({
            BinaryValue(sourceProvider.asInputStream)
          })
        }
        case None => {
          BinaryValue(sourceProvider.asInputStream)
        }
      }
      if (needsMaterialize) {
        bodyValue = bodyValue.materialize
      }
    }
    bodyValue
  }

  override def schema(implicit ctx: EvaluationContext): Option[Schema] = {
    Some(
      Schema(
        Seq(
          SchemaProperty(StringValue("mimeType"), mayBeContentType.map(StringValue(_)).getOrElse(NullValue)),
          SchemaProperty(StringValue("raw"), BinaryValue(sourceProvider.asInputStream)))))
  }

  override def valueType(implicit ctx: EvaluationContext): Type = {
    if (valueTypeValue == null) {
      val superValueType = super.valueType
      valueTypeValue = Type.extend(superValueType.name, superValueType, () => Some(SchemaValue(schema.get)))
    }
    valueTypeValue
  }

  override def location(): Location = {
    location
  }
}

object HttpBodyValue {
  def apply(httpRequest: HttpServerRequest): HttpBodyValue = {
    val headers: Seq[(String, String)] = httpRequest.headers
    val mayBeContentType: Option[String] = headers.find((header) => header._1.equalsIgnoreCase(CONTENT_TYPE_HEADER)).map(_._2)
    new HttpBodyValue(SourceProvider(httpRequest.body), mayBeContentType, Map(), SimpleLocation("server.request.body"))
  }
}
