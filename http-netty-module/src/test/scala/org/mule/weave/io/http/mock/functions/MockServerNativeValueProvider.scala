package org.mule.weave.io.http.mock.functions

import org.mule.weave.v2.model.values.FunctionValue
import org.mule.weave.v2.model.values.NativeValueProvider

class MockServerNativeValueProvider extends NativeValueProvider {

  val functions: Map[String, FunctionValue] = Map(
    ("MockServerStartFunction", new MockServerStartFunctionValue()),
    ("MockServerStopFunction", new MockServerStopFunctionValue())
  )

  override def name(): String = "MockServer"

  override def getNativeFunction(name: String): Option[FunctionValue] = functions.get(name)
}
