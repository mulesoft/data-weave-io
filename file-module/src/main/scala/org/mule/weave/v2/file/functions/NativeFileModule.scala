package org.mule.weave.v2.file.functions

import java.io.File

import org.mule.weave.v2.core.functions.BinaryFunctionValue
import org.mule.weave.v2.core.functions.EmptyFunctionValue
import org.mule.weave.v2.core.functions.UnaryFunctionValue
import org.mule.weave.v2.model.EvaluationContext
import org.mule.weave.v2.model.types.StringType
import org.mule.weave.v2.model.values.ArrayValue
import org.mule.weave.v2.model.values.FunctionValue
import org.mule.weave.v2.model.values.NullValue
import org.mule.weave.v2.model.values.StringValue
import org.mule.weave.v2.model.values.Value
import org.mule.weave.v2.module.DataFormatManager
import org.mule.weave.v2.module.native.NativeValueProvider

class NativeFileModule extends NativeValueProvider {

  val functions: Map[String, FunctionValue] = Map(
    "LSFunction" -> new LSFunction(),
    "FileTypeOfFunction" -> new FileTypeOfFunction(),
    "NameOfFunction" -> new NameOfFunction(),
    "TmpPathFunction" -> new TmpPathFunction(),
    "PathFunction" -> new PathFunction(),
    "MimeTypeOfFunction" -> new MimeTypeOfFunction())

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
    if (file.isDirectory)
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

class MimeTypeOfFunction extends UnaryFunctionValue {

  def getExtension(output: File): String = {
    val i = output.getName lastIndexOf '.'
    if (i > 0) {
      output.getName.drop(i)
    } else {
      null
    }
  }

  override val R = StringType

  override def doExecute(path: R.V)(implicit ctx: EvaluationContext): Value[_] = {
    val pathString = StringType.coerce(path).evaluate
    val file = new File(pathString)
    val extension = getExtension(file)
    if (extension != null) {
      val maybeFormat = DataFormatManager.byExtension(extension)
      maybeFormat
        .map(_.defaultMimeType.toString)
        .orElse(MimeTypes.getMimeType(extension))
        .map((value) => {
          StringValue(value)
        })
        .getOrElse(NullValue)
    } else {
      NullValue
    }
  }
}
