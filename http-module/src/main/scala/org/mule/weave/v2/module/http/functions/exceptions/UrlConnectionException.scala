package org.mule.weave.v2.module.http.functions.exceptions

import org.mule.weave.v2.core.exception.ExecutionException
import org.mule.weave.v2.parser.location.Location

class UrlConnectionException(url: String, reason: String, val location: Location) extends ExecutionException {
  override def message: String = s"Unable to connect to ${url}. Caused by ${reason}."
}
