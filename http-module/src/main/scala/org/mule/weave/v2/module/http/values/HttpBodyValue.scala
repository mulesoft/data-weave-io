package org.mule.weave.v2.module.http.values

import org.mule.weave.v2.core.io.SeekableStream
import org.mule.weave.v2.model.EvaluationContext
import org.mule.weave.v2.model.structure.schema.Schema
import org.mule.weave.v2.model.structure.schema.SchemaProperty
import org.mule.weave.v2.model.types.Type
import org.mule.weave.v2.model.values._
import org.mule.weave.v2.model.values.wrappers.DelegateValue
import org.mule.weave.v2.module.DataFormatManager
import org.mule.weave.v2.module.http.HttpHeader.CONTENT_TYPE_HEADER
import org.mule.weave.v2.module.http.service.HttpServerRequest
import org.mule.weave.v2.module.reader.Reader
import org.mule.weave.v2.module.reader.SourceProvider
import org.mule.weave.v2.module.reader.SourceProvider
import org.mule.weave.v2.parser.location.Location
import org.mule.weave.v2.parser.location.SimpleLocation
import org.mule.weave.v2.parser.module.MimeType

import java.nio.charset.Charset

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
  private val DEFAULT_CHARSET = Charset.forName("UTF-8")

  def apply(httpRequest: HttpServerRequest)(implicit ctx: EvaluationContext): HttpBodyValue = {
    val maybeContentType = httpRequest.headers.find(header => header._1.equalsIgnoreCase(CONTENT_TYPE_HEADER)).map(_._2)
    val maybeMimeType = maybeContentType.map(contentType => MimeType.fromSimpleString(contentType))
    val charset = maybeMimeType.flatMap(mimeType => {
      val maybeCharset = mimeType.getCharset()
      maybeCharset.map(cs => Charset.forName(cs))
    }).getOrElse(DEFAULT_CHARSET)
    val sourceProvider = SourceProvider(SeekableStream(httpRequest.body), charset, maybeMimeType)
    new HttpBodyValue(sourceProvider, maybeContentType, Map(), SimpleLocation("server.request.body"))
  }
}
