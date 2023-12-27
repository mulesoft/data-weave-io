package org.mule.weave.io.file

import java.io.File
import java.io.FileWriter
import java.nio.file.Files

import org.mule.weave.v2.helper.AbstractEngineTest

class FileSystemTest extends AbstractEngineTest {

  {
    val tmpDir = Files.createTempDirectory("FileSystemTest")
    val ioTestDir = tmpDir.resolve("dw_io_test")
    ioTestDir.toFile.mkdirs()
    createFileWithContent(ioTestDir.resolve("test1.txt").toFile, "Test")
    createFileWithContent(ioTestDir.resolve("test2.json").toFile, "{}")
    createFileWithContent(ioTestDir.resolve("test3.unknown").toFile, "---")
    val inputStream = getClass.getClassLoader.getResourceAsStream("zips/test.zip")
    val zipDirectory = ioTestDir.resolve("zips").toFile
    zipDirectory.mkdirs()
    Files.copy(inputStream, new File(zipDirectory, "test.zip").toPath)

    val tmpDirAbsolutePath = tmpDir.toAbsolutePath
    println(s"Using temp folder $tmpDirAbsolutePath")
    System.setProperty("java.io.tmpdir", tmpDirAbsolutePath.toString)
  }

  private def createFileWithContent(test: File, content: String): Unit = {
    val writer = new FileWriter(test)
    writer.write(content)
    writer.close()
  }
}
