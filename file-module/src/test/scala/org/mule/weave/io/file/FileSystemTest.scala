package org.mule.weave.io.file

import java.io.File
import java.io.FileWriter

import org.mule.weave.v2.helper.AbstractEngineTest

class FileSystemTest extends AbstractEngineTest {

  {
    //
    val tmpDir = new File(System.getProperty("java.io.tmpdir"), "dw_io_test")
    tmpDir.mkdirs()
    createFileWithContent(new File(tmpDir, "test1.txt"), "Test")
    createFileWithContent(new File(tmpDir, "test2.json"), "{}")
    createFileWithContent(new File(tmpDir, "test3.unknown"), "---")

  }

  private def createFileWithContent(test: File, content: String): Unit = {
    val writer = new FileWriter(test)
    writer.write(content)
    writer.close()
  }
}
