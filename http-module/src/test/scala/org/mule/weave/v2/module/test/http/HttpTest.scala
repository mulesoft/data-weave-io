package org.mule.weave.v2.module.test.http

import org.mule.weave.v2.helper.EngineHelper
import org.mule.weave.v2.helper.FolderBasedTest
import org.mule.weave.v2.helper.ParsingContextTestAware
import org.mule.weave.v2.interpreted.RuntimeModuleNodeCompiler
import org.mule.weave.v2.io.FileHelper
import org.mule.weave.v2.model.EvaluationContext
import org.mule.weave.v2.module.option.Settings
import org.mule.weave.v2.module.reader.Reader
import org.mule.weave.v2.module.writer.EmptyWriter
import org.mule.weave.v2.parser.exception.LocatableException
import org.mule.weave.v2.parser.phase.ParsingContext
import org.mule.weave.v2.runtime.WeaveCompiler
import org.mule.weave.v2.sdk.WeaveResourceFactory
import org.scalatest.FunSpec
import org.scalatest.Matchers

class HttpTest extends FunSpec with Matchers with FolderBasedTest with ParsingContextTestAware {

  val moduleNodeLoader = RuntimeModuleNodeCompiler()

  scenariosWithoutOutputFor(classOf[HttpTest]).foreach {
    case (scenario, inputs, transform) =>
      it(scenario) {

        implicit val ctx = EvaluationContext()
        val readers: Map[String, Reader] = EngineHelper.buildReaders(inputs)
        try {
          val context: ParsingContext = createTestParsingContext(transform)
          inputs.foreach((f) => {
            context.addImplicitInput(FileHelper.baseName(f), None)
          })
          val compilerResult = if (System.getProperty("noCheck") != null) {
            WeaveCompiler.compileWithNoCheck(WeaveResourceFactory.fromFile(transform), context)
          } else {
            WeaveCompiler.compile(WeaveResourceFactory.fromFile(transform), context, moduleNodeLoader)
          }
          compilerResult
            .getResult()
            .executable
            .writeWith(EmptyWriter, readers)
        } catch {
          case e: LocatableException =>
            fail(e.getMessage + " at:\n" + e.location.locationString, e)
          case e: Exception =>
            fail(e)
        }
      }
  }
}



