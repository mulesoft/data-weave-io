package org.mule.weave.v2.module.http.values

import org.mule.weave.v2.model.EvaluationContext
import org.mule.weave.v2.model.structure.schema.Schema
import org.mule.weave.v2.model.structure.schema.SchemaProperty
import org.mule.weave.v2.model.types.Type
import org.mule.weave.v2.model.values.BinaryValue
import org.mule.weave.v2.model.values.NullValue
import org.mule.weave.v2.model.values.SchemaValue
import org.mule.weave.v2.model.values.StringValue
import org.mule.weave.v2.model.values.Value
import org.mule.weave.v2.model.values.wrappers.DelegateValue
import org.mule.weave.v2.module.DataFormatManager
import org.mule.weave.v2.module.http.HttpHeader.CONTENT_TYPE_HEADER
import org.mule.weave.v2.module.http.service.HttpServerRequest
import org.mule.weave.v2.module.reader.SourceProvider
import org.mule.weave.v2.parser.location.Location
import org.mule.weave.v2.parser.location.SimpleLocation

class HttpBodyValue(val sourceProvider: SourceProvider, mayBeContentType: Option[String], readerProperties: Map[String, Any], location: Location) extends DelegateValue {

  var bodyValue: Value[Any] = _
  var valueTypeValue: Type = _

  override def value(implicit ctx: EvaluationContext): Value[Any] = {
    if (bodyValue == null) {
      bodyValue = mayBeContentType match {
        case Some(contentType) => {
          DataFormatManager.byContentType(contentType).map((df) => {
            val reader = df.reader(sourceProvider)
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
      valueTypeValue = super.valueType.withSchema(() => Some(SchemaValue(schema.get)))
    }
    valueTypeValue
  }

  override def location(): Location = {
    location
  }
}

object HttpBodyValue {
  def apply(httpRequest: HttpServerRequest): HttpBodyValue = {
    val mayBeContentType = httpRequest.headers.find((header) => header._1.equalsIgnoreCase(CONTENT_TYPE_HEADER)).map(_._2)
    new HttpBodyValue(SourceProvider(httpRequest.body), mayBeContentType, Map(), SimpleLocation("server.request.body"))
  }
}
