package org.mule.weave.v2.module.http.functions

import org.mule.weave.v2.core.model.values.FunctionValue
import org.mule.weave.v2.core.module.native.NativeValueProvider

class HttpNativeValueProvider extends NativeValueProvider {

  val functions: Map[String, FunctionValue] = Map(
    ("HttpServerFunction", new HttpServerFunction()),
    ("HttpRequestFunction", new HttpRequestFunction()))

  override def name(): String = "http"

  override def getNativeFunction(name: String): Option[FunctionValue] = functions.get(name)
}
