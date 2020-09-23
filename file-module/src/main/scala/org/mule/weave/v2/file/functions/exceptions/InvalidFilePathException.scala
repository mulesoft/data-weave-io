package org.mule.weave.v2.file.functions.exceptions

import org.mule.weave.v2.exception.ExecutionException
import org.mule.weave.v2.parser.location.Location

class InvalidFilePathException(val filePath: String, val location: Location) extends ExecutionException {
  override def message: String = s"Invalid path ${filePath}."
}
