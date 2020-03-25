package org.mule.weave.v2.module.test.raml

import java.io.File

import org.mule.weave.v2.codegen.CodeGenerator
import org.mule.weave.v2.helper.SimpleTextBasedTest
import org.mule.weave.v2.interpreted.RuntimeModuleNodeCompiler
import org.mule.weave.v2.io.FileHelper
import org.mule.weave.v2.model.EvaluationContext
import org.mule.weave.v2.module.raml.RamlModuleLoader
import org.mule.weave.v2.parser.ModuleParser
import org.mule.weave.v2.parser.ast.variables.NameIdentifier
import org.mule.weave.v2.parser.exception.LocatableException
import org.mule.weave.v2.parser.phase.ParsingContext
import org.mule.weave.v2.utils.AstEmitter

class RamlTest extends SimpleTextBasedTest {

  val moduleNodeLoader = RuntimeModuleNodeCompiler()

  override def runTest(testFile: File): String = {
    implicit val ctx: EvaluationContext = EvaluationContext()

    try {
      val baseName = FileHelper.baseName(testFile)
      val context: ParsingContext = createTestParsingContext(testFile)
      val moduleLoader = new RamlModuleLoader()
      val identifier = NameIdentifier(s"org::mule::weave::v2::module::test::raml::${baseName}", Some("raml"))
      val result = moduleLoader.loadModule(identifier, context)
      result match {
        case Some(parsingResult) => {
          val module = parsingResult.getResult().astNode
          val cannonicalPhase = ModuleParser.canonicalPhasePhases().call(result.get.getResult(), context)

          val scopePhase = ModuleParser.scopePhasePhases().call(cannonicalPhase.getResult(), context)
          assert(scopePhase.noErrors(), s"Scope check didn't pass ${scopePhase.errorMessages().map(_._2.message).mkString("\n - ")}")

          val typePhase = ModuleParser.typeCheckPhasePhases().call(scopePhase.getResult(), context)
          assert(typePhase.noErrors(), s"Typeck check didn't pass ${scopePhase.errorMessages().map(_._2.message).mkString("\n - ")}")

          val str = CodeGenerator.generate(module)
          println(str)
          AstEmitter(printLocation = false, printComments = false).print(module)
        }
        case None => fail(s"Unable to generated module for ${identifier}")
      }
    } catch {
      case e: LocatableException =>
        fail(e.getMessage + " at:\n" + e.location.locationString, e)
      case e: Exception =>
        fail(e)
    }
  }

  override def testExtension: String = ".raml"

  override def expectedExtension: String = ".ast"
}

