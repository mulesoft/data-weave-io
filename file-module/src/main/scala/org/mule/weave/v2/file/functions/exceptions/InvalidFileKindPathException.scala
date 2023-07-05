package org.mule.weave.v2.file.functions.exceptions

import org.mule.weave.v2.core.exception.ExecutionException
import org.mule.weave.v2.parser.location.Location

class InvalidFileKindPathException(val filePath: String, val expectedKind: String, val actualKind: String, val location: Location) extends ExecutionException {
  override def message: String = s"Expecting `${expectedKind}` but got `${actualKind}` : ${filePath}."
}
