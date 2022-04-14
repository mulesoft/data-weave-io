package org.mule.weave.v2.module.http.functions

import org.mule.weave.v2.model.service.WeaveRuntimePrivilege

object HttpWeaveRuntimePrivilege {

  val HTTP_CLIENT: WeaveRuntimePrivilege = WeaveRuntimePrivilege("http.Client")

  val HTTP_SERVER: WeaveRuntimePrivilege = WeaveRuntimePrivilege("http.Server")
}
