package org.mule.weave.v2.file.functions.exceptions

import org.mule.weave.v2.exception.ExecutionException
import org.mule.weave.v2.parser.location.Location

class UnableToWriteFileException(val filePath: String, reason: String, val location: Location) extends ExecutionException {
  override def message: String = s"Unable to write `${filePath}`, reason : ${reason}."
}
