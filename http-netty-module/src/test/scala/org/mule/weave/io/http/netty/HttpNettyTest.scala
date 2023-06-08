package org.mule.weave.io.http.netty

import org.mule.weave.v2.helper.AbstractEngineTest

class HttpNettyTest extends AbstractEngineTest {

  override def ignoreTests(): Array[String] = {
    Array("GET_POST")
  }
}
