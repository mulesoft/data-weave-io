package org.mule.weave.v2.file.functions.exceptions

import org.mule.weave.v2.core.exception.ExecutionException
import org.mule.weave.v2.parser.location.Location

class ZipException(val filePath: String, reason: String, val location: Location) extends ExecutionException {
  override def message: String = s"Exception while trying to zip ${filePath}. Reason: ${reason}"
}
