package org.mule.weave.v2.module.http

import org.mule.weave.v2.helper.AbstractEngineTest

import java.io.File

class HttpModuleTest extends AbstractEngineTest {


  override def isTestToRun(file: File): Boolean = {
    file.getName.contains("readBody")
  }
}
