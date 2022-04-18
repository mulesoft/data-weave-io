package org.mule.weave.v2.file.functions

import org.mule.weave.v2.model.service.WeaveRuntimePrivilege

object FileWeaveRuntimePrivilege {

  val FS_READ: WeaveRuntimePrivilege = WeaveRuntimePrivilege("fs::Read")

  val FS_WRITE: WeaveRuntimePrivilege = WeaveRuntimePrivilege("fs::Write")
}
