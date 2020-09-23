package org.mule.weave.io.file

import java.io.File
import java.io.FileWriter
import java.nio.file.Files

import org.mule.weave.v2.helper.AbstractEngineTest

class FileSystemTest extends AbstractEngineTest {

  {
    //
    val tmpDir = new File(System.getProperty("java.io.tmpdir"), "dw_io_test")
    deleteDirectory(tmpDir)
    tmpDir.mkdirs()
    createFileWithContent(new File(tmpDir, "test1.txt"), "Test")
    createFileWithContent(new File(tmpDir, "test2.json"), "{}")
    createFileWithContent(new File(tmpDir, "test3.unknown"), "---")
    val inputStream = getClass.getClassLoader.getResourceAsStream("zips/test.zip")
    val zipDirectory = new File(tmpDir, "zips")
    zipDirectory.mkdirs()
    Files.copy(inputStream, new File(zipDirectory, "test.zip").toPath)
  }

   def deleteDirectory(directoryToBeDeleted: File):Unit = {
    val allContents = directoryToBeDeleted.listFiles
    if (allContents != null) for (file <- allContents) {
      deleteDirectory(file)
    }
    directoryToBeDeleted.delete
  }

  private def createFileWithContent(test: File, content: String): Unit = {
    val writer = new FileWriter(test)
    writer.write(content)
    writer.close()
  }
}
