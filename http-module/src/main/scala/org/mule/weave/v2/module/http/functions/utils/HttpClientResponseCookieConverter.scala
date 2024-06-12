package org.mule.weave.v2.module.http.functions.utils

import org.mule.weave.v2.model.values.BooleanValue
import org.mule.weave.v2.model.values.NumberValue
import org.mule.weave.v2.model.values.ObjectValue
import org.mule.weave.v2.model.values.ObjectValueBuilder
import org.mule.weave.v2.model.values.StringValue
import org.mule.weave.v2.module.http.functions.utils.HttpClientResponseCookieConverter.COMMENT
import org.mule.weave.v2.module.http.functions.utils.HttpClientResponseCookieConverter.DOMAIN
import org.mule.weave.v2.module.http.functions.utils.HttpClientResponseCookieConverter.HTTP_ONLY
import org.mule.weave.v2.module.http.functions.utils.HttpClientResponseCookieConverter.MAX_AGE
import org.mule.weave.v2.module.http.functions.utils.HttpClientResponseCookieConverter.NAME
import org.mule.weave.v2.module.http.functions.utils.HttpClientResponseCookieConverter.PATH
import org.mule.weave.v2.module.http.functions.utils.HttpClientResponseCookieConverter.SECURE
import org.mule.weave.v2.module.http.functions.utils.HttpClientResponseCookieConverter.VALUE

import java.net.HttpCookie

class HttpClientResponseCookieConverter(cookie: HttpCookie) {

  def convert(): ObjectValue = {
    val builder = new ObjectValueBuilder()

    if (cookie.getName != null) {
      builder.addPair(NAME, StringValue(cookie.getName))
    }
    if (cookie.getValue != null) {
      builder.addPair(VALUE, StringValue(cookie.getValue))
    }
    builder.addPair(MAX_AGE, NumberValue(cookie.getMaxAge.intValue()))
    builder.addPair(HTTP_ONLY, BooleanValue(cookie.isHttpOnly))
    builder.addPair(SECURE, BooleanValue(cookie.getSecure))
    if (cookie.getDomain != null) {
      builder.addPair(DOMAIN, StringValue(cookie.getDomain))
    }
    if (cookie.getComment != null) {
      builder.addPair(COMMENT, StringValue(cookie.getComment))
    }
    if (cookie.getPath != null) {
      builder.addPair(PATH, StringValue(cookie.getPath))
    }
    builder.build
  }
}

object HttpClientResponseCookieConverter {
  private val NAME = "name"
  private val VALUE = "value"
  private val MAX_AGE = "maxAge"
  private val HTTP_ONLY = "httpOnly"
  private val SECURE = "secure"
  private val DOMAIN = "domain"
  private val COMMENT = "comment"
  private val PATH = "path"

  def apply(cookie: HttpCookie): HttpClientResponseCookieConverter =
    new HttpClientResponseCookieConverter(cookie)
}
