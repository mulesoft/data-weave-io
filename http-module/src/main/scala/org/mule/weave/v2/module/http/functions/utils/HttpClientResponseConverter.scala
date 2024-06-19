package org.mule.weave.v2.module.http.functions.utils

import org.mule.weave.v2.core.io.SeekableStream
import org.mule.weave.v2.model.EvaluationContext
import org.mule.weave.v2.model.structure.KeyValuePair
import org.mule.weave.v2.model.values.BinaryValue
import org.mule.weave.v2.model.values.KeyValue
import org.mule.weave.v2.model.values.NumberValue
import org.mule.weave.v2.model.values.ObjectValue
import org.mule.weave.v2.model.values.ObjectValueBuilder
import org.mule.weave.v2.model.values.StringValue
import org.mule.weave.v2.model.values.Value
import org.mule.weave.v2.model.values.math.Number
import org.mule.weave.v2.module.http.functions.utils.HttpClientResponseConverter.BODY
import org.mule.weave.v2.module.http.functions.utils.HttpClientResponseConverter.CONTENT_TYPE
import org.mule.weave.v2.module.http.functions.utils.HttpClientResponseConverter.COOKIES
import org.mule.weave.v2.module.http.functions.utils.HttpClientResponseConverter.HEADERS
import org.mule.weave.v2.module.http.functions.utils.HttpClientResponseConverter.STATUS
import org.mule.weave.v2.module.http.functions.utils.HttpClientResponseConverter.STATUS_TEXT
import org.mule.weave.v2.module.http.service.HttpClientHeaders
import org.mule.weave.v2.module.http.service.HttpClientResponse
import org.mule.weave.v2.module.reader.SourceProvider

import java.net.HttpCookie
import scala.collection.JavaConverters._

class HttpClientResponseConverter(response: HttpClientResponse, stopWatch: StopWatch) {

  def convert()(implicit ctx: EvaluationContext): ObjectValue = {

    val builder = new ObjectValueBuilder()

    // status
    builder.addPair(STATUS, NumberValue(Number(response.getStatus)))

    // statusText?
    response.getStatusText.ifPresent(st => {
      builder.addPair(STATUS_TEXT, StringValue(st))
    })

    // headers
    builder.addPair(HEADERS, asHeadersValue(response.getHeaders))

    // body?
    response.getBody.ifPresent(body => {
      val sourceProvider = SourceProvider(SeekableStream(body))
      builder.addPair(BODY, BinaryValue(sourceProvider.asInputStream))
    })

    // cookies
    builder.addPair(COOKIES, asCookieValue(response.getCookies))

    // contentType?
    response.getContentType.ifPresent(contentType => {
      builder.addPair(CONTENT_TYPE, StringValue(contentType))
    })

    // Schema
    stopWatch.stop()
    val schema = MetadataConverter(response.getMetadata, stopWatch.getTotal).convert()
    builder.withSchema(schema)
  }

  private def asHeadersValue(headers: HttpClientHeaders): Value[_] = {
    val entries = if (headers != null) {
      val names = headers.getHeaderNames
      if (names != null) {
        names.asScala.flatMap(name => {
          headers.getHeaderValues(name).asScala.map(value => {
            KeyValuePair(KeyValue(name), StringValue(value))
          })
        })
      } else {
        Seq.empty[KeyValuePair]
      }
    } else {
      Seq.empty[KeyValuePair]
    }
    ObjectValue(entries.toArray)
  }

  private def asCookieValue(cookies: java.util.List[HttpCookie]): Value[_] = {
    val entries = if (cookies != null) {
      cookies.asScala.map(cookie => {
        val converter = new HttpClientResponseCookieConverter(cookie)
        KeyValuePair(KeyValue(cookie.getName), converter.convert())
      })
    } else {
      Seq.empty[KeyValuePair]
    }
    ObjectValue(entries.toArray)
  }
}

object HttpClientResponseConverter {
  private val STATUS = "status"
  private val STATUS_TEXT = "statusText"
  private val HEADERS = "headers"
  private val BODY = "body"
  private val COOKIES = "cookies"
  private val CONTENT_TYPE = "contentType"

  def apply(response: HttpClientResponse, stopWatch: StopWatch): HttpClientResponseConverter =
    new HttpClientResponseConverter(response, stopWatch)
}
