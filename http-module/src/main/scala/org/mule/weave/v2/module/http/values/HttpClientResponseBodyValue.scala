package org.mule.weave.v2.module.http.values

import org.mule.weave.v2.model.EvaluationContext
import org.mule.weave.v2.model.structure.ObjectSeq
import org.mule.weave.v2.model.structure.schema.Schema
import org.mule.weave.v2.model.structure.schema.SchemaProperty
import org.mule.weave.v2.model.types.StringType
import org.mule.weave.v2.model.types.Type
import org.mule.weave.v2.model.values.BinaryValue
import org.mule.weave.v2.model.values.SchemaValue
import org.mule.weave.v2.model.values.StringValue
import org.mule.weave.v2.model.values.Value
import org.mule.weave.v2.model.values.wrappers.DelegateValue
import org.mule.weave.v2.module.DataFormatManager
import org.mule.weave.v2.module.core.multipart.MultiPartDataFormat
import org.mule.weave.v2.module.http.functions.utils.MimeTypeUtil
import org.mule.weave.v2.module.reader.SourceProvider
import org.mule.weave.v2.parser.location.Location
import org.mule.weave.v2.parser.module.MimeType

class HttpClientResponseBodyValue(mimeType: String, sourceProvider: SourceProvider, readerProperties: ObjectSeq, location: Location) extends DelegateValue {

  private var bodyValue: Value[Any] = _
  private var valueTypeValue: Type = _
  private var needsMaterialize: Boolean = false

  override def materialize(implicit ctx: EvaluationContext): Value[_] = {
    needsMaterialize = true
    this
  }

  override def eagerMaterialize(implicit ctx: EvaluationContext): Value[Any] = {
    bodyValue = getBodyValue().eagerMaterialize
    this
  }

  override def value(implicit ctx: EvaluationContext): Value[Any] = {
    getBodyValue()
  }

  private def getBodyValue()(implicit ctx: EvaluationContext): Value[Any] = {
    if (bodyValue == null) {
      bodyValue = MimeTypeUtil.fromSimpleString(mimeType) match {
        case Some(mt) =>
          readFromBinary(mt, sourceProvider, readerProperties)
        case None =>
          BinaryValue(sourceProvider.asInputStream)
      }
    }
    if (needsMaterialize) {
      bodyValue = bodyValue.materialize
      needsMaterialize = false
    }
    bodyValue
  }

  override def schema(implicit ctx: EvaluationContext): Option[Schema] = {
    Some(
      Schema(
        Seq(
          SchemaProperty(StringValue("mimeType"), StringValue(mimeType)),
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

  private def readFromBinary(mimeType: MimeType, sourceProvider: SourceProvider, readerProperties: ObjectSeq)(implicit ctx: EvaluationContext): Value[_] = {
    DataFormatManager.byContentType(mimeType).map(df => {
      val reader = df.reader(sourceProvider)
      val keyValuePairs = readerProperties.toIterator()
      for (kvp <- keyValuePairs) {
        val propertyName = kvp._1.evaluate(ctx).name
        val value = StringType.coerce(kvp._2).evaluate(ctx)
        reader.setOption(kvp._1.location(), propertyName, value)
      }
      // Configure boundary
      if (df.isInstanceOf[MultiPartDataFormat]) {
        val boundary = mimeType.parameters.get("boundary")
        if (boundary.isDefined) {
          reader.setOption(location, "boundary", boundary.get)
        }
      }
      reader.read(location.locationString)
    }).getOrElse({
      BinaryValue(sourceProvider.asInputStream)
    })
  }
}

object HttpClientResponseBodyValue {

  def apply(mimeType: String, sourceProvider: SourceProvider, readerProperties: ObjectSeq, location: Location): HttpClientResponseBodyValue = new HttpClientResponseBodyValue(mimeType, sourceProvider, readerProperties, location)
}