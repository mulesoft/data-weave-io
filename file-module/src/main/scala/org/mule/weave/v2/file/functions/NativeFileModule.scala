package org.mule.weave.v2.file.functions

import java.io.File
import java.nio.file.Files
import java.nio.file.StandardCopyOption

import org.mule.weave.v2.core.functions.BinaryFunctionValue
import org.mule.weave.v2.core.functions.EmptyFunctionValue
import org.mule.weave.v2.core.functions.UnaryFunctionValue
import org.mule.weave.v2.io.SeekableStream
import org.mule.weave.v2.model.EvaluationContext
import org.mule.weave.v2.model.types.BinaryType
import org.mule.weave.v2.model.types.StringType
import org.mule.weave.v2.model.values.ArrayValue
import org.mule.weave.v2.model.values.FunctionValue
import org.mule.weave.v2.model.values.NullValue
import org.mule.weave.v2.model.values.NumberValue
import org.mule.weave.v2.model.values.StringValue
import org.mule.weave.v2.model.values.Value
import org.mule.weave.v2.module.native.NativeValueProvider

class NativeFileModule extends NativeValueProvider {

  val functions: Map[String, FunctionValue] = toMap(
    Seq(
      new LSFunction(),
      new FileTypeOfFunction(),
      new NameOfFunction(),
      new TmpPathFunction(),
      new PathFunction(),
      new ToUrlFunction(),
      new MakeDirFunction(),
      new WriteFunction()))

  override def name() = "file"

  override def getNativeFunction(name: String): Option[FunctionValue] = functions.get(name)
}

class LSFunction extends UnaryFunctionValue {
  override val R = StringType

  override def doExecute(path: R.V)(implicit ctx: EvaluationContext): Value[_] = {
    val pathString = path.evaluate
    val files = new File(pathString).listFiles()
    if (files == null) {
      ArrayValue(Seq())
    } else {
      val children = files.map((path) => {
        StringValue(path.getAbsolutePath)
      })
      ArrayValue(children)
    }
  }
}

class FileTypeOfFunction extends UnaryFunctionValue {

  override val R = StringType

  override def doExecute(path: R.V)(implicit ctx: EvaluationContext): Value[_] = {
    val pathString = StringType.coerce(path).evaluate
    val file = new File(pathString)
    if (!file.exists()) {
      NullValue
    } else if (file.isDirectory)
      StringValue("Folder")
    else
      StringValue("File")

  }
}

class NameOfFunction extends UnaryFunctionValue {
  override val R = StringType

  override def doExecute(path: R.V)(implicit ctx: EvaluationContext): Value[_] = {
    val pathString = StringType.coerce(path).evaluate
    val file = new File(pathString)
    StringValue(file.getName)
  }
}

class ToUrlFunction extends UnaryFunctionValue {
  override val R = StringType

  override def doExecute(path: R.V)(implicit ctx: EvaluationContext): Value[_] = {
    val pathString = path.evaluate
    val file = new File(pathString)
    StringValue(file.toURI.toURL.toExternalForm)
  }
}

class MakeDirFunction extends UnaryFunctionValue {
  override val R = StringType

  override def doExecute(path: R.V)(implicit ctx: EvaluationContext): Value[_] = {
    val pathString = path.evaluate
    val file = new File(pathString)
    file.mkdirs()
    StringValue(pathString)
  }
}

class WriteFunction extends BinaryFunctionValue {
  override val L = StringType
  override val R = BinaryType

  override protected def doExecute(leftValue: Value[L.T], rightValue: Value[SeekableStream])(implicit ctx: EvaluationContext): Value[_] = {
    val path: String = leftValue.evaluate
    val amount = Files.copy(rightValue.evaluate.spinOff(), new File(path).toPath, StandardCopyOption.REPLACE_EXISTING)
    NumberValue(amount)
  }
}

class PathFunction extends BinaryFunctionValue {
  override val L = StringType
  override val R = StringType

  override def doExecute(path: L.V, subPath: R.V)(implicit ctx: EvaluationContext): Value[_] = {
    val pathString = StringType.coerce(path).evaluate
    val subPathString = StringType.coerce(subPath).evaluate
    val file = new File(pathString, subPathString)
    StringValue(file.getAbsolutePath)
  }
}

class TmpPathFunction extends EmptyFunctionValue {
  override protected def doExecute()(implicit ctx: EvaluationContext): Value[_] = {
    StringValue(System.getProperty("java.io.tmpdir"))
  }
}

