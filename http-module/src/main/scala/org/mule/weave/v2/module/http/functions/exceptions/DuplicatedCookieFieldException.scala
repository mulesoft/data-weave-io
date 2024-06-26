package org.mule.weave.v2.module.http.functions.exceptions

import org.mule.weave.v2.core.exception.ExecutionException
import org.mule.weave.v2.parser.location.Location

class DuplicatedCookieFieldException(val location: Location) extends ExecutionException {

  override def message: String = {
    s"Duplicate cookie instance was found. Cookies can be specified as a 'Header' OR a 'Cookie' object for a single HTTP Request.\nIn this case, cookies were set:\n" +
      s"- as header instance.\n" +
      s"- as `cookie` object in the HTTP request object.\n" +
      s"Please review your HTTP request."
  }
}
