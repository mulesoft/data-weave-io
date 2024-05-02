package org.mule.weave.io.http.netty

import org.mule.weave.io.http.mock.MockServer
import org.mule.weave.io.http.mock.functions.MockServerRegistry.getAll
import org.mule.weave.v2.helper.AbstractEngineTest
import org.scalatest.BeforeAndAfterAll

class HttpNettyTest extends AbstractEngineTest with BeforeAndAfterAll {

  // TODO: Use custom http.bin server to avoid flaky tests
  override def ignoreTests(): Array[String] = {
    // TODO: Flaky test, see W-13564473
    // Array("GET_POST")
    Array()
  }

  override protected def afterAll(): Unit = {
    getAll.foreach(server => {
      stopSilently(server)
    })
  }

  private def stopSilently(server: MockServer): Unit = {
    try {
      server.stopServer()
    } catch {
      case _: Exception =>
      // Nothing to do
    }
  }
}
