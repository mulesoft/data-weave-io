package org.mule.weave.v2.module.http.service

import org.mule.weave.v2.model.EvaluationContext
import org.mule.weave.v2.model.structure.ArraySeq
import org.mule.weave.v2.model.structure.KeyValuePair
import org.mule.weave.v2.model.structure.ObjectSeq
import org.mule.weave.v2.model.structure.schema.SchemaProperty
import org.mule.weave.v2.model.values.math.Number
import org.mule.weave.v2.module.http.functions.utils.MetadataConverter
import org.mule.weave.v2.module.http.service.metadata.ArrayMetadataValue
import org.mule.weave.v2.module.http.service.metadata.NumberMetadataValue
import org.mule.weave.v2.module.http.service.metadata.ObjectMetadataValue
import org.mule.weave.v2.module.http.service.metadata.StringMetadataValue
import org.scalatest.Assertion
import org.scalatest.freespec.AnyFreeSpec
import org.scalatest.matchers.should.Matchers

import java.util.Optional

class MetadataConverterTest extends AnyFreeSpec with Matchers{

  "MetadataConverter" - {
    implicit val ctx: EvaluationContext = EvaluationContext()

    "should work with different metadata values" in {
      val expectedTotal = 101
      val expectedNumberValue = 100
      val expectedStringValue = "Hi"

      val objectMetadataValueBuilder = new ObjectMetadataValue.Builder()
      objectMetadataValueBuilder.addKeyValuePair("number", new NumberMetadataValue(s"$expectedNumberValue"))
      objectMetadataValueBuilder.addKeyValuePair("string", new StringMetadataValue(expectedStringValue))
      objectMetadataValueBuilder.addKeyValuePair("array", new ArrayMetadataValue.Builder()
        .add(new StringMetadataValue(expectedStringValue))
        .build())

      val builder = new ObjectMetadataValue.Builder()
      builder.addKeyValuePair("object", objectMetadataValueBuilder.build())
      val metadata = builder.build()

      val objectMetadataValue = Optional.ofNullable(metadata)

      val converter = MetadataConverter(objectMetadataValue)
      val schema = converter.convert()

      schema should not be null

      val schemaProperties = schema.properties()
      schemaProperties.size shouldBe 1

      val objectProperties = findProperty(schemaProperties, "object")
      objectProperties.isDefined shouldBe true

      val objectSeq = objectProperties.get.value.evaluate.asInstanceOf[ObjectSeq].toSeq()

      val numberMetadataValue = findKeyValuePair(objectSeq, "number")
      numberMetadataValue.isDefined shouldBe true
      val numberValue = numberMetadataValue.get._2.evaluate.asInstanceOf[Number].toLong
      numberValue shouldBe expectedNumberValue

      val stringMetadataValue = findKeyValuePair(objectSeq, "string")
      stringMetadataValue.isDefined shouldBe true
      val stringValue = stringMetadataValue.get._2.evaluate
      stringValue shouldBe expectedStringValue

      val arrayMetadataValue = findKeyValuePair(objectSeq, "array")
      arrayMetadataValue.isDefined shouldBe true
      val arraySeq = arrayMetadataValue.get._2.evaluate.asInstanceOf[ArraySeq]
      arraySeq.size() shouldBe 1
      arraySeq.toSeq().head.evaluate shouldBe expectedStringValue
    }

    def findKeyValuePair(objectSeq: Seq[KeyValuePair], name: String): Option[KeyValuePair] = {
      objectSeq.find(kvp => {
        val value = kvp._1.evaluate
        value.name == name
      })
    }

    def findProperty(schemaProperties: Seq[SchemaProperty], name: String)(implicit ctx: EvaluationContext): Option[SchemaProperty] = {
      schemaProperties.find(p => {
        val value = p.name.evaluate
        value == name
      })
    }

    def assertTotalProperty(schemaProperties: Seq[SchemaProperty], expected: Long)(implicit ctx: EvaluationContext): Assertion = {
      val totalProperty = findProperty(schemaProperties, "total")

      totalProperty.isDefined shouldBe true

      val totalValue = totalProperty.get.value.evaluate
      totalValue.asInstanceOf[Number].toLong shouldBe expected
    }
  }
}
