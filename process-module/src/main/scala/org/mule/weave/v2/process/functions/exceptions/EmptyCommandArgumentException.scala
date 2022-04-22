package org.mule.weave.v2.process.functions.exceptions

import org.mule.weave.v2.exception.ExecutionException
import org.mule.weave.v2.parser.location.Location

class EmptyCommandArgumentException(val location: Location) extends ExecutionException {

  override def message: String = "Argument `cmd` can not be empty"
}
