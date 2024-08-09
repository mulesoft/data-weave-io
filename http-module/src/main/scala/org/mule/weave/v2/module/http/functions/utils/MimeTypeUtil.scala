package org.mule.weave.v2.module.http.functions.utils

import org.mule.weave.v2.parser.module.MimeType

import scala.util.Try

object MimeTypeUtil {

  def fromSimpleString(mimeType: String): Option[MimeType] = {
    Try(MimeType.fromSimpleString(mimeType)).toOption
  }
}
