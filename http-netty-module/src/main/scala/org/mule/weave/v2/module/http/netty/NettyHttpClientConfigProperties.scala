package org.mule.weave.v2.module.http.netty

import org.mule.weave.v2.model.service.RuntimeSettings

object NettyHttpClientConfigProperties {

  // prefix value = "com.mulesoft.dw.netty.client"
  private val NETTY_CLIENT_SETTINGS_PREFIX = RuntimeSettings.prop("netty.client")

  private val HONOR_PROXY_PROPERTIES_SETTING = s"$NETTY_CLIENT_SETTINGS_PREFIX.honor_proxy_properties"

  lazy val HONOR_PROXY_PROPERTIES: Boolean = System.getProperty(HONOR_PROXY_PROPERTIES_SETTING, "true").toBoolean
}
