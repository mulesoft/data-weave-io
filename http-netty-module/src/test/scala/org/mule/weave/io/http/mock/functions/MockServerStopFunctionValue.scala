package org.mule.weave.io.http.mock.functions

import org.mule.weave.v2.core.functions.UnaryFunctionValue
import org.mule.weave.v2.model.EvaluationContext
import org.mule.weave.v2.model.types.StringType
import org.mule.weave.v2.model.values.BooleanValue
import org.mule.weave.v2.model.values.Value

class MockServerStopFunctionValue extends UnaryFunctionValue {

  override val R = StringType

  override protected def doExecute(v: R.V)(implicit ctx: EvaluationContext): Value[_] = {
    val id = v.evaluate.toString
    val maybeServer = MockServerRegistry.get(id)
    if (maybeServer.isDefined) {
      val server = maybeServer.get
      server.stopServer()
    }
    BooleanValue.TRUE_BOOL
  }
}
