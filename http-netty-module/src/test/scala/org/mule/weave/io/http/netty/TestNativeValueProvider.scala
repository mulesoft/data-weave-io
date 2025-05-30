package org.mule.weave.io.http.netty

import org.mule.weave.v2.model.values.FunctionValue
import org.mule.weave.v2.model.values.NativeValueProvider

class TestNativeValueProvider extends NativeValueProvider {

  val functions: Map[String, FunctionValue] = Map(
    ("FailBinaryFunction", new FailBinaryFunction()))

  override def name(): String = "TEST"

  override def getNativeFunction(name: String): Option[FunctionValue] = functions.get(name)
}
