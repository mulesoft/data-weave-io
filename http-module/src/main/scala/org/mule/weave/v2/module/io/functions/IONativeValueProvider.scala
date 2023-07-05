package org.mule.weave.v2.module.io.functions

import org.mule.weave.v2.core.model.values.FunctionValue
import org.mule.weave.v2.core.module.native.NativeValueProvider

class IONativeValueProvider extends NativeValueProvider {

  val functions: Map[String, FunctionValue] = Map(
    ("FreePortFunction", new FreePortFunctionValue()))

  override def name(): String = "IO"

  override def getNativeFunction(name: String): Option[FunctionValue] = functions.get(name)
}
