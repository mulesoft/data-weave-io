package org.mule.weave.v2.module.http.functions.exceptions

import org.mule.weave.v2.exception.ExecutionException
import org.mule.weave.v2.parser.location.Location

class InvalidUrlException(url: String, val location: Location) extends ExecutionException {
  override def message: String = s"The ${url} is not valid."
}
