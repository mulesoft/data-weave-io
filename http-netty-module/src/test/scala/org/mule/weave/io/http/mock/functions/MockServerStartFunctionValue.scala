package org.mule.weave.io.http.mock.functions

import org.mule.weave.io.http.mock.MockServer
import org.mule.weave.v2.core.functions.BinaryFunctionValue
import org.mule.weave.v2.model.EvaluationContext
import org.mule.weave.v2.model.types.ObjectType
import org.mule.weave.v2.model.types.StringType
import org.mule.weave.v2.model.values.BooleanValue
import org.mule.weave.v2.model.values.Value

class MockServerStartFunctionValue extends BinaryFunctionValue {
  override val L = ObjectType
  override val R = StringType

  override protected def doExecute(leftValue: L.V, rightValue: R.V)(implicit ctx: EvaluationContext): Value[_] = {
    val serverConfigObject = leftValue.evaluate.materialize()
    val id = rightValue.evaluate.toString
    val config = MockServerConfigConverter.convert(serverConfigObject)
    val server = new MockServer(config)
    val success = try {
      server.startServer()
      MockServerRegistry.register(id, server)
      BooleanValue.TRUE_BOOL
    } catch {
      case _: Exception =>
        BooleanValue.FALSE_BOOL
    }
    success
  }
}
