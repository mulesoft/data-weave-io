package org.mule.weave.v2.file.functions

import java.io.File
import java.io.FileInputStream
import java.io.FileOutputStream
import java.io.IOException
import java.nio.file.Files
import java.nio.file.StandardCopyOption
import java.util.zip.ZipEntry
import java.util.zip.ZipInputStream
import java.util.zip.ZipOutputStream

import org.mule.weave.v2.core.functions.BinaryFunctionValue
import org.mule.weave.v2.core.functions.EmptyFunctionValue
import org.mule.weave.v2.core.functions.UnaryFunctionValue
import org.mule.weave.v2.file.functions.exceptions.InvalidFileKindPathException
import org.mule.weave.v2.file.functions.exceptions.ZipException
import org.mule.weave.v2.io.SeekableStream
import org.mule.weave.v2.model.EvaluationContext
import org.mule.weave.v2.model.types.ArrayType
import org.mule.weave.v2.model.types.BinaryType
import org.mule.weave.v2.model.types.StringType
import org.mule.weave.v2.model.values.ArrayValue
import org.mule.weave.v2.model.values.BooleanValue
import org.mule.weave.v2.model.values.FunctionValue
import org.mule.weave.v2.model.values.NullValue
import org.mule.weave.v2.model.values.NumberValue
import org.mule.weave.v2.model.values.StringValue
import org.mule.weave.v2.model.values.Value
import org.mule.weave.v2.module.native.NativeValueProvider

class NativeFileModule extends NativeValueProvider {

  val functions: Map[String, FunctionValue] = {
    toMap(
      Seq(
        new LSFunction(),
        new FileTypeOfFunction(),
        new NameOfFunction(),
        new TmpPathFunction(),
        new PathFunction(),
        new ToUrlFunction(),
        new MakeDirFunction(),
        new WriteFunction(),
        new WorkingDirectoryPathFunction(),
        new HomePathFunction(),
        new ZipFunction(),
        new UnzipFunction(),
        new RemoveFunction()))
  }

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
    } else if (file.isDirectory) {
      StringValue(FSKind.FOLDER_KIND)
    } else {
      StringValue(FSKind.FILE_KIND)
    }
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

class HomePathFunction extends EmptyFunctionValue {
  override protected def doExecute()(implicit ctx: EvaluationContext): Value[_] = {
    StringValue(System.getProperty("user.home"))
  }
}

class WorkingDirectoryPathFunction extends EmptyFunctionValue {
  override protected def doExecute()(implicit ctx: EvaluationContext): Value[_] = {
    StringValue(System.getProperty("user.dir"))
  }
}

class ZipFunction extends BinaryFunctionValue {
  override val L = ArrayType
  override val R = StringType

  override protected def doExecute(leftValue: L.V, rightValue: R.V)(implicit ctx: EvaluationContext): Value[_] = {
    val zipPath = rightValue.evaluate
    try {
      val zipFileToCreate = new File(zipPath)
      val parentFile = zipFileToCreate.getParentFile
      if (parentFile.exists() && parentFile.isFile) {
        throw new InvalidFileKindPathException(parentFile.getAbsolutePath, FSKind.FOLDER_KIND, FSKind.FILE_KIND, location())
      } else {
        parentFile.mkdirs()
        if (zipFileToCreate.isDirectory) {
          throw new InvalidFileKindPathException(zipPath, FSKind.FILE_KIND, FSKind.FOLDER_KIND, location())
        }
        val fos = new FileOutputStream(zipPath)
        val zipOut = new ZipOutputStream(fos)
        val iterator = leftValue.evaluate.toIterator()
        while (iterator.hasNext) {
          val sourceFile = StringType.coerce(iterator.next()).evaluate
          val fileToZip = new File(sourceFile)
          zipFile(fileToZip, fileToZip.getName, zipOut)
          zipOut.close()
        }
      }
      StringValue(zipFileToCreate.getAbsolutePath)
    } catch {
      case io: IOException => {
        throw new ZipException(zipPath, io.getMessage, location())
      }
    }
  }

  @throws[IOException]
  private def zipFile(fileToZip: File, fileName: String, zipOut: ZipOutputStream): Unit = {
    if (fileToZip.isHidden) return
    if (fileToZip.isDirectory) {
      if (fileName.endsWith("/")) {
        zipOut.putNextEntry(new ZipEntry(fileName))
        zipOut.closeEntry()
      } else {
        zipOut.putNextEntry(new ZipEntry(fileName + "/"))
        zipOut.closeEntry()
      }
      val children = fileToZip.listFiles
      for (childFile <- children) {
        zipFile(childFile, fileName + "/" + childFile.getName, zipOut)
      }
      return
    }
    val fis = new FileInputStream(fileToZip)
    val zipEntry = new ZipEntry(fileName)
    zipOut.putNextEntry(zipEntry)
    val bytes = new Array[Byte](1024)
    var length = fis.read(bytes)
    while (length >= 0) {
      zipOut.write(bytes, 0, length)
      length = fis.read(bytes)
    }
    fis.close()
  }
}

class RemoveFunction extends UnaryFunctionValue {
  override val R = StringType

  override protected def doExecute(v: R.V)(implicit ctx: EvaluationContext): Value[_] = {
    val filePath = v.evaluate
    BooleanValue(deleteDirectory(new File(filePath)))
  }

  def deleteDirectory(directoryToBeDeleted: File): Boolean = {
    val allContents = directoryToBeDeleted.listFiles
    if (allContents != null) {
      for (file <- allContents) {
        deleteDirectory(file)
      }
    }
    directoryToBeDeleted.delete
  }

}

class UnzipFunction extends BinaryFunctionValue {
  override val L = StringType
  override val R = StringType

  override protected def doExecute(leftValue: L.V, rightValue: R.V)(implicit ctx: EvaluationContext): Value[_] = {
    val fileZip = leftValue.evaluate
    val destDirPath = rightValue.evaluate
    unzip(fileZip, destDirPath)
    rightValue
  }

  private def unzip(zipFilePath: String, destDir: String): Unit = {
    val dir = new File(destDir)
    if (dir.isFile) {
      throw new InvalidFileKindPathException(dir.getAbsolutePath, FSKind.FOLDER_KIND, FSKind.FILE_KIND, location())
    } else if (!dir.exists) {
      // create output directory if it doesn't exist
      dir.mkdirs
    }
    var fis: FileInputStream = null
    var zis: ZipInputStream = null;
    //buffer for read and write data to file
    try {
      fis = new FileInputStream(zipFilePath)
      zis = new ZipInputStream(fis)
      var ze = zis.getNextEntry
      while (ze != null) {
        val fileName = ze.getName
        val newFile = new File(destDir + File.separator + fileName)
        //create directories for sub directories in zip
        val parentFile = newFile.getParentFile
        parentFile.mkdirs
        if (!ze.isDirectory) {
          Files.copy(zis, newFile.toPath)
        }
        zis.closeEntry()
        ze = zis.getNextEntry
      }

    } catch {
      case e: IOException =>
        throw new ZipException(zipFilePath, e.getMessage, location())
    } finally {
      if (zis != null) {
        zis.closeEntry()
        zis.close()
      }
      if (fis != null) {
        fis.close()
      }
    }
  }
}

object FSKind {
  val FOLDER_KIND = "Folder"
  val FILE_KIND = "File"
}