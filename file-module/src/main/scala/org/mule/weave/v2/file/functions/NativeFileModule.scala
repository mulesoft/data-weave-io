package org.mule.weave.v2.file.functions

import org.mule.weave.v2.core.functions.{ BinaryFunctionValue, EmptyFunctionValue, SecureBinaryFunctionValue, SecureEmptyFunctionValue, SecureUnaryFunctionValue, UnaryFunctionValue }
import org.mule.weave.v2.file.functions.exceptions.InvalidFileKindPathException
import org.mule.weave.v2.file.functions.exceptions.UnableToWriteFileException
import org.mule.weave.v2.file.functions.exceptions.ZipException
import org.mule.weave.v2.model.EvaluationContext
import org.mule.weave.v2.model.service.WeaveRuntimePrivilege
import org.mule.weave.v2.model.types.ArrayType
import org.mule.weave.v2.model.types.BinaryType
import org.mule.weave.v2.model.types.StringType
import org.mule.weave.v2.model.values.ArrayValue
import org.mule.weave.v2.model.values.BooleanValue
import org.mule.weave.v2.model.values.FunctionValue
import org.mule.weave.v2.model.values.NativeValueProvider
import org.mule.weave.v2.model.values.NullValue
import org.mule.weave.v2.model.values.NumberValue
import org.mule.weave.v2.model.values.StringValue
import org.mule.weave.v2.model.values.Value

import java.io.File
import java.io.FileInputStream
import java.io.FileOutputStream
import java.io.IOException
import java.nio.file.Files
import java.nio.file.StandardCopyOption
import java.util.zip.ZipEntry
import java.util.zip.ZipInputStream
import java.util.zip.ZipOutputStream

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
        new CopyToFunction(),
        new WorkingDirectoryPathFunction(),
        new HomePathFunction(),
        new ZipFunction(),
        new UnzipFunction(),
        new RemoveFunction(),
        new ParentOfFunction(),
        new SeparatorFunction()))
  }

  override def name() = "file"

  override def getNativeFunction(name: String): Option[FunctionValue] = functions.get(name)
}

class LSFunction extends SecureUnaryFunctionValue {
  override val R = StringType

  override val requiredPrivilege: WeaveRuntimePrivilege = FileWeaveRuntimePrivilege.FS_READ

  override protected def onSecureExecution(path: R.V)(implicit ctx: EvaluationContext): Value[_] = {
    val pathString = path.evaluate.toString
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

class FileTypeOfFunction extends SecureUnaryFunctionValue {

  override val R = StringType

  override val requiredPrivilege: WeaveRuntimePrivilege = FileWeaveRuntimePrivilege.FS_READ

  override protected def onSecureExecution(path: R.V)(implicit ctx: EvaluationContext): Value[_] = {
    val pathString = path.evaluate.toString
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
    val pathString = path.evaluate.toString
    val file = new File(pathString)
    StringValue(file.getName)
  }
}

class ParentOfFunction extends UnaryFunctionValue {
  override val R = StringType

  override protected def doExecute(path: Value[R.T])(implicit ctx: EvaluationContext): Value[_] = {
    val pathString = path.evaluate.toString
    val file = new File(pathString)
    Option(file.getParent).map(StringValue(_)).getOrElse(NullValue)
  }
}

class SeparatorFunction extends EmptyFunctionValue {

  override protected def doExecute()(implicit ctx: EvaluationContext): Value[_] = StringValue(File.separator)
}

class ToUrlFunction extends UnaryFunctionValue {
  override val R = StringType

  override def doExecute(path: R.V)(implicit ctx: EvaluationContext): Value[_] = {
    val pathString = path.evaluate.toString
    val file = new File(pathString)
    StringValue(file.toURI.toURL.toExternalForm)
  }
}

class MakeDirFunction extends SecureUnaryFunctionValue {
  override val R = StringType

  override val requiredPrivilege: WeaveRuntimePrivilege = FileWeaveRuntimePrivilege.FS_WRITE

  override protected def onSecureExecution(path: R.V)(implicit ctx: EvaluationContext): Value[_] = {
    val pathString = path.evaluate.toString
    val file = new File(pathString)
    if (file.mkdirs()) {
      StringValue(pathString, this)
    } else {
      NullValue
    }
  }
}

class CopyToFunction extends SecureBinaryFunctionValue {
  override val L = BinaryType
  override val R = StringType

  override val requiredPrivilege: WeaveRuntimePrivilege = FileWeaveRuntimePrivilege.FS_WRITE

  override protected def onSecureExecution(leftValue: Value[L.T], rightValue: Value[R.T])(implicit ctx: EvaluationContext): Value[_] = {
    val path: String = rightValue.evaluate.toString
    val file = new File(path)
    val parentFile = file.getParentFile
    if (parentFile != null && !parentFile.exists()) {
      parentFile.mkdirs()
    }
    try {
      val amount = Files.copy(leftValue.evaluate.spinOff(), file.toPath, StandardCopyOption.REPLACE_EXISTING)
      NumberValue(amount)
    } catch {
      case e: Exception => {
        throw new UnableToWriteFileException(path, e.getMessage, location())
      }
    }
  }
}

class PathFunction extends BinaryFunctionValue {
  override val L = StringType
  override val R = StringType

  override def doExecute(path: L.V, subPath: R.V)(implicit ctx: EvaluationContext): Value[_] = {
    val pathString = path.evaluate.toString
    val subPathString = subPath.evaluate.toString
    val file = new File(pathString, subPathString)
    StringValue(file.getAbsolutePath)
  }
}

class TmpPathFunction extends SecureEmptyFunctionValue {
  override val requiredPrivilege: WeaveRuntimePrivilege = FileWeaveRuntimePrivilege.FS_READ

  override protected def onSecureExecution()(implicit ctx: EvaluationContext): Value[_] = {
    StringValue(System.getProperty("java.io.tmpdir"))
  }
}

class HomePathFunction extends SecureEmptyFunctionValue {
  override val requiredPrivilege: WeaveRuntimePrivilege = FileWeaveRuntimePrivilege.FS_READ

  override protected def onSecureExecution()(implicit ctx: EvaluationContext): Value[_] = {
    StringValue(System.getProperty("user.home"))
  }
}

class WorkingDirectoryPathFunction extends SecureEmptyFunctionValue {
  override val requiredPrivilege: WeaveRuntimePrivilege = FileWeaveRuntimePrivilege.FS_READ

  override protected def onSecureExecution()(implicit ctx: EvaluationContext): Value[_] = {
    StringValue(System.getProperty("user.dir"))
  }
}

class ZipFunction extends SecureBinaryFunctionValue {
  override val L = ArrayType
  override val R = StringType

  override val requiredPrivilege: WeaveRuntimePrivilege = FileWeaveRuntimePrivilege.FS_WRITE

  override protected def onSecureExecution(leftValue: L.V, rightValue: R.V)(implicit ctx: EvaluationContext): Value[_] = {
    val zipPath = rightValue.evaluate.toString
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
          val sourceFile = StringType.coerce(iterator.next()).evaluate.toString
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

class RemoveFunction extends SecureUnaryFunctionValue {
  override val R = StringType

  override val requiredPrivilege: WeaveRuntimePrivilege = FileWeaveRuntimePrivilege.FS_WRITE

  override protected def onSecureExecution(v: R.V)(implicit ctx: EvaluationContext): Value[_] = {
    val filePath = v.evaluate.toString
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

class UnzipFunction extends SecureBinaryFunctionValue {
  override val L = StringType
  override val R = StringType

  override val requiredPrivilege: WeaveRuntimePrivilege = FileWeaveRuntimePrivilege.FS_WRITE

  override protected def onSecureExecution(leftValue: L.V, rightValue: R.V)(implicit ctx: EvaluationContext): Value[_] = {
    val fileZip = leftValue.evaluate.toString
    val destDirPath = rightValue.evaluate.toString
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