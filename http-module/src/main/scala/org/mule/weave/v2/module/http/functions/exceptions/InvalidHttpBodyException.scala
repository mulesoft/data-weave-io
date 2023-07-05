package org.mule.weave.v2.module.http.functions.exceptions

import org.mule.weave.v2.core.exception.ExecutionException
import org.mule.weave.v2.parser.location.Location

class InvalidHttpBodyException(val location: Location) extends ExecutionException {
  override def message: String = "Response needs te be serializable in a http response."
}
