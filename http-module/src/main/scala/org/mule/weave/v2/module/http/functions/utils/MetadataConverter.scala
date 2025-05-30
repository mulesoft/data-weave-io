package org.mule.weave.v2.module.http.functions.utils

import org.mule.weave.v2.model.structure.KeyValuePair
import org.mule.weave.v2.model.structure.schema.Schema
import org.mule.weave.v2.model.structure.schema.SchemaProperty
import org.mule.weave.v2.model.values.ArrayValue
import org.mule.weave.v2.model.values.KeyValue
import org.mule.weave.v2.model.values.KeyValuePairValue
import org.mule.weave.v2.model.values.NumberValue
import org.mule.weave.v2.model.values.ObjectValueBuilder
import org.mule.weave.v2.model.values.StringValue
import org.mule.weave.v2.model.values.Value
import org.mule.weave.v2.model.values.math.Number
import org.mule.weave.v2.module.http.service.metadata.ArrayMetadataValue
import org.mule.weave.v2.module.http.service.metadata.KeyValuePairMetadataValue
import org.mule.weave.v2.module.http.service.metadata.MetadataValue
import org.mule.weave.v2.module.http.service.metadata.NumberMetadataValue
import org.mule.weave.v2.module.http.service.metadata.ObjectMetadataValue
import org.mule.weave.v2.module.http.service.metadata.StringMetadataValue

import java.util.Optional
import scala.collection.JavaConverters._

class MetadataConverter(metadata: Optional[ObjectMetadataValue]) {

  def convert(): Schema = {
    val props = if (metadata.isPresent) {
      metadata.get().getProperties.asScala.map(prop => {
        val value = toValue(prop.getValue)
        SchemaProperty(StringValue(prop.getKey), value)
      })
    } else {
      Seq.empty
    }
    Schema(props)
  }

  private def toValue(value: MetadataValue): Value[_] = {
    value match {
      case s: StringMetadataValue => StringValue(s.getValue)
      case n: NumberMetadataValue => NumberValue(Number(n.getValue))
      case a: ArrayMetadataValue =>
        val elements = a.getElements.asScala.map(e => toValue(e))
        ArrayValue(elements)
      case kvm: KeyValuePairMetadataValue =>
        val value = toValue(kvm.getValue)
        val kv = KeyValuePair(KeyValue(kvm.getKey), value)
        KeyValuePairValue(kv)
      case o: ObjectMetadataValue =>
        val builder = new ObjectValueBuilder
        o.getProperties.forEach(p => {
          val value: Value[_] = toValue(p.getValue)
          builder.addPair(p.getKey, value)
        })
        builder.build
    }
  }
}

object MetadataConverter {

  def apply(metadata: Optional[ObjectMetadataValue]): MetadataConverter = new MetadataConverter(metadata)
}
