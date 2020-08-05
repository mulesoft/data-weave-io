package dw.amf

import amf.ProfileName
import amf.client.plugins.AMFPlugin
import amf.client.plugins.ValidationMode
import amf.core.model.document.PayloadFragment
import amf.core.model.domain.DomainElement
import amf.core.model.domain.Shape
import amf.core.validation.AMFPayloadValidationPlugin
import amf.core.validation.PayloadValidator
import amf.internal.environment.Environment
import amf.plugins.document.webapi.validation.remote.BooleanValidationProcessor
import amf.plugins.document.webapi.validation.remote.PlatformPayloadValidator
import amf.plugins.document.webapi.validation.remote.ValidationProcessor
import amf.plugins.domain.webapi.unsafe.JsonSchemaSecrets

import scala.concurrent.ExecutionContext
import scala.concurrent.Future

object DummyPayloadValidator extends AMFPayloadValidationPlugin with JsonSchemaSecrets {

  override def canValidate(shape: Shape, env: Environment): Boolean = {
    false
  }

  override val ID: String = "AMF Payload Validation"

  override def dependencies(): Seq[AMFPlugin] = Nil

  override def init()(implicit executionContext: ExecutionContext): Future[AMFPlugin] = Future.successful(this)

  override def validator(s: Shape, env: Environment, validationMode: ValidationMode): PayloadValidator =
    new DummyPlatformPayloadValidator(s, env, validationMode)

  override val payloadMediaType: Seq[String] = Seq()
}

class DummyPlatformPayloadValidator(val shape: Shape, val env: Environment, val validationMode: ValidationMode) extends PlatformPayloadValidator(shape, env) {
  override protected def getReportProcessor(profileName: ProfileName): ValidationProcessor = BooleanValidationProcessor

  override type LoadedObj = this.type
  override type LoadedSchema = this.type

  override protected def callValidator(schema: DummyPlatformPayloadValidator.this.type, obj: DummyPlatformPayloadValidator.this.type, fragment: Option[PayloadFragment], validationProcessor: ValidationProcessor): validationProcessor.Return = ???

  override protected def loadDataNodeString(payload: PayloadFragment): Option[DummyPlatformPayloadValidator.this.type] = None

  override protected def loadJson(text: String): DummyPlatformPayloadValidator.this.type = {
    throw new RuntimeException("Not Implemented")
  }

  override protected def loadSchema(jsonSchema: CharSequence, element: DomainElement, validationProcessor: ValidationProcessor): Either[validationProcessor.Return, Option[DummyPlatformPayloadValidator.this.type]] = {
    throw new RuntimeException("Not Implemented")
  }

}