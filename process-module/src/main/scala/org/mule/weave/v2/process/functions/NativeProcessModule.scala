package org.mule.weave.v2.process.functions

import org.mule.weave.v2.core.functions.SecureBinaryFunctionValue
import org.mule.weave.v2.model.EvaluationContext
import org.mule.weave.v2.model.service.WeaveRuntimePrivilege
import org.mule.weave.v2.model.types.ArrayType
import org.mule.weave.v2.model.types.BinaryType
import org.mule.weave.v2.model.types.ObjectType
import org.mule.weave.v2.model.types.StringType
import org.mule.weave.v2.model.values.BinaryValue
import org.mule.weave.v2.model.values.BinaryValue.getBytesFromInputStream
import org.mule.weave.v2.model.values.FunctionValue
import org.mule.weave.v2.model.values.ObjectValue
import org.mule.weave.v2.model.values.ObjectValueBuilder
import org.mule.weave.v2.model.values.Value
import org.mule.weave.v2.module.native.NativeValueProvider
import org.mule.weave.v2.process.functions.ExecProcessFunction.GRACEFUL_RESULT
import org.mule.weave.v2.process.functions.ExecProcessFunction.KILLED_RESULT
import org.mule.weave.v2.util.ObjectValueUtils.select
import org.mule.weave.v2.util.ObjectValueUtils.selectNumber
import org.mule.weave.v2.util.ObjectValueUtils.selectString
import org.mule.weave.v2.util.ObjectValueUtils.selectStringMap

import java.io.File
import java.io.IOException
import java.util.concurrent.TimeUnit

class NativeProcessModule extends NativeValueProvider {

  val functions: Map[String, FunctionValue] = {
    toMap(
      Seq(
        new ExecProcessFunction()))
  }

  override def name() = "Process"

  override def getNativeFunction(name: String): Option[FunctionValue] = functions.get(name)
}

class ExecProcessFunction extends SecureBinaryFunctionValue {

  override val L = new ArrayType(StringType)
  override val R = ObjectType

  override val requiredPrivilege: WeaveRuntimePrivilege = new WeaveRuntimePrivilege("Process::exec")

  def buildResult(process: Process, status: String)(implicit ctx: EvaluationContext): ObjectValue = {
    val result = new ObjectValueBuilder()
    result.addPair("status", status)
    if (!process.isAlive) {
      result.addPair("exitCode", process.exitValue())
    }
    val stdOut =
      try {
        getBytesFromInputStream(process.getInputStream, ctx.serviceManager.memoryService)
      } catch {
        case _: IOException => Array[Byte](0)
      }

    val stdErr =
      try {
        getBytesFromInputStream(process.getErrorStream, ctx.serviceManager.memoryService)
      } catch {
        case _: IOException => Array[Byte](0)
      }

    result.addPair("stdOut", BinaryValue(stdOut))
    result.addPair("stdErr", BinaryValue(stdErr))
    result.build
  }

  override protected def onSecureExecution(leftValue: L.V, rightValue: R.V)(implicit ctx: EvaluationContext): Value[_] = {
    val args: ArrayType.T = leftValue.evaluate
    val config = rightValue.materialize.evaluate
    val maybeWorkingDirectory: Option[String] = selectString(config, "workingDirectory")
    val maybeEnvVars = selectStringMap(config, "envVars")
    val maybeTimeOut = selectNumber(config, "timeout")
    val maybeStdIn = select(config, "stdIn")

    val commandArgs: Array[String] = args.toSeq().map((v) => StringType.coerce(v).evaluate.toString).toArray
    val envVarsString: Array[String] = maybeEnvVars.map((envVars) => envVars.map((entry) => entry._1 + "=" + entry._2).toArray).orNull
    val wd: File = maybeWorkingDirectory.map((fp) => new File(fp)).orNull
    var process: Process = null
    try {
      process = Runtime.getRuntime.exec(commandArgs, envVarsString, wd)
    } catch {
      case e: IOException => {
        val error = new ObjectValueBuilder()
        error.addPair("status", "ERROR")
        error.addPair("message", e.getMessage)
        return error.build
      }
      case _: IndexOutOfBoundsException | _: NullPointerException => {
        val error = new ObjectValueBuilder()
        error.addPair("status", "ERROR")
        error.addPair("message", "Invalid argument `cmd` should not be empty or have null values.")
        return error.build
      }
    }

    //
    if (maybeStdIn.isDefined) {
      val standarInput = BinaryType.coerce(maybeStdIn.get)
      val bytes = BinaryValue.getBytes(standarInput, close = true)
      process.getOutputStream.write(bytes)
    }
    if (maybeTimeOut.isDefined) {
      if (process.waitFor(maybeTimeOut.get.longValue(), TimeUnit.MILLISECONDS)) {
        buildResult(process, GRACEFUL_RESULT)
      } else {
        process.destroyForcibly()
        buildResult(process, KILLED_RESULT)
      }
    } else {
      process.waitFor()
      buildResult(process, GRACEFUL_RESULT)
    }
  }

}

object ExecProcessFunction {
  val GRACEFUL_RESULT = "GRACEFUL"
  val KILLED_RESULT = "KILLED"
}
