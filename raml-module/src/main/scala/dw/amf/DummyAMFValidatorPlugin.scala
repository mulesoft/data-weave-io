package dw.amf

import amf._
import amf.client.execution.BaseExecutionEnvironment
import amf.client.plugins.AMFFeaturePlugin
import amf.client.plugins.AMFPlugin
import amf.core.annotations.SourceVendor
import amf.core.benchmark.ExecutionLog
import amf.core.errorhandling.ErrorHandler
import amf.core.model.document.BaseUnit
import amf.core.model.document.Document
import amf.core.model.document.Fragment
import amf.core.model.document.Module
import amf.core.rdf.RdfModel
import amf.core.remote._
import amf.core.services.RuntimeValidator
import amf.core.services.RuntimeValidator.CustomShaclFunctions
import amf.core.services.ValidationOptions
import amf.core.validation.AMFValidationReport
import amf.core.validation.EffectiveValidations
import amf.core.validation.ValidationResultProcessor
import amf.core.validation.core.ValidationReport
import amf.core.validation.core.ValidationSpecification
import amf.internal.environment.Environment
import amf.plugins.document.vocabularies.AMLPlugin
import amf.plugins.features.validation.CustomValidationReport
import amf.plugins.features.validation.model.ValidationDialectText
import amf.plugins.syntax.SYamlSyntaxPlugin

import scala.concurrent.ExecutionContext
import scala.concurrent.Future

object DummyAMFValidatorPlugin extends AMFFeaturePlugin with RuntimeValidator with ValidationResultProcessor {

  val url = "http://a.ml/dialects/profile.raml"

  override val ID = "AMF Validation"

  override def init()(implicit executionContext: ExecutionContext): Future[AMFPlugin] = {
    // Registering ourselves as the runtime validator
    RuntimeValidator.register(DummyAMFValidatorPlugin)
    ExecutionLog.log("Register RDF framework")
    ExecutionLog.log(s"dw.amf.AMFValidatorPlugin#init: registering validation dialect")
    AMLPlugin.registry.registerDialect(url, ValidationDialectText.text, executionContext) map { _ =>
      ExecutionLog.log(s"dw.amf.AMFValidatorPlugin#init: validation dialect registered")
      this
    }
  }

  override def dependencies() = Seq(SYamlSyntaxPlugin)


  // All the profiles are collected here, plugins can generate their own profiles

  override def loadValidationProfile(
                                      validationProfilePath: String,
                                      env: Environment = Environment(),
                                      errorHandler: ErrorHandler,
                                      exec: BaseExecutionEnvironment = platform.defaultExecutionEnvironment): Future[ProfileName] = {

    Future.successful(Raml10Profile)

  }

  override def shaclValidation(
                                model: BaseUnit,
                                validations: EffectiveValidations,
                                customFunctions: CustomShaclFunctions,
                                options: ValidationOptions)(implicit executionContext: ExecutionContext): Future[ValidationReport] = {

    Future.successful(new CustomValidationReport())
  }

  private def profileForUnit(unit: BaseUnit, given: ProfileName): ProfileName = {
    given match {
      case OasProfile =>
        getSource(unit) match {
          case Some(Oas30) => Oas30Profile
          case _ => Oas20Profile
        }
      case RamlProfile =>
        getSource(unit) match {
          case Some(Raml08) => Raml08Profile
          case _ => Raml10Profile
        }
      case _ => given
    }

  }

  private def getSource(unit: BaseUnit): Option[Vendor] = unit match {
    case d: Document => d.encodes.annotations.find(classOf[SourceVendor]).map(_.vendor)
    case m: Module => m.annotations.find(classOf[SourceVendor]).map(_.vendor)
    case f: Fragment => f.encodes.annotations.find(classOf[SourceVendor]).map(_.vendor)
    case _ => None
  }

  override def validate(
                         model: BaseUnit,
                         given: ProfileName,
                         messageStyle: MessageStyle,
                         env: Environment,
                         resolved: Boolean = false,
                         exec: BaseExecutionEnvironment = platform.defaultExecutionEnvironment): Future[AMFValidationReport] = {

    val profileName = profileForUnit(model, given)
    Future.successful(AMFValidationReport(true, model.id, profileName, Seq()))
  }

  /**
    * Returns a native RDF model with the dw.amf.SHACL shapes graph
    */
  override def shaclModel(
                           validations: Seq[ValidationSpecification],
                           functionUrls: String,
                           messageStyle: MessageStyle): RdfModel =
    throw new RuntimeException("Not Implemented")

  /**
    * Generates a JSON-LD graph with the dw.amf.SHACL shapes for the requested profile name
    *
    * @return JSON-LD graph
    */
  override def emitShapesGraph(profileName: ProfileName): String = {
    ""
  }
}

object ValidationMutex {}
