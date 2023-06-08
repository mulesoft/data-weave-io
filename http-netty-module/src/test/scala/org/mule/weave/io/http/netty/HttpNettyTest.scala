package org.mule.weave.io.http.netty

import org.mule.weave.v2.helper.AbstractEngineTest

class HttpNettyTest extends AbstractEngineTest {

  override def ignoreTests(): Array[String] = {
    // TODO: Flaky test, see W-13564473
    Array("GET_POST")
  }
}
