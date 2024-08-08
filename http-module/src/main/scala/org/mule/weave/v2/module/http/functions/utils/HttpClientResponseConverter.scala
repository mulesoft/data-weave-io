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

import java.io.InputStream
import java.lang.Long.parseLong
import java.net.HttpCookie
import scala.collection.JavaConverters._

class HttpClientResponseConverter(response: HttpClientResponse, stopWatch: StopWatch) {
  private val CONTENT_LENGTH_HEADER = "Content-Length"
  private val NO_CONTENT_STATUS_CODE = 204
  private val NOT_MODIFIED_STATUS_CODE = 304
  private val RESET_CONTENT_STATUS_CODE = 205

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
      addBody(body, response.getStatus, response.getHeaders, builder)
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

  private def addBody(body: InputStream, statusCode: Int, headers: HttpClientHeaders, builder: ObjectValueBuilder)(implicit ctx: EvaluationContext): Unit = {
    var addBodyField = true
    val maybeContentLength = extractContentLength(headers)
    if (maybeContentLength.isDefined) {
      // Validate content-length header
      val contentLength = maybeContentLength.get
      if (contentLength <= 0) {
        addBodyField = false
        ctx.serviceManager.loggingService.logDebug(s"Ignoring HTTP response body field because $CONTENT_LENGTH_HEADER header value is $contentLength")
      }
    } else {
      // Validate status code
      if (NO_CONTENT_STATUS_CODE == statusCode || NOT_MODIFIED_STATUS_CODE == statusCode || RESET_CONTENT_STATUS_CODE == statusCode) {
        addBodyField = false
        ctx.serviceManager.loggingService.logDebug(s"Ignoring HTTP response body field because the status: $statusCode does not support body")
      }
    }

    if (addBodyField) {
      val sourceProvider = SourceProvider(SeekableStream(body))
      builder.addPair(BODY, BinaryValue(sourceProvider.asInputStream))
    }
  }

  private def extractContentLength(headers: HttpClientHeaders): Option[Long] = {
    if (headers != null) {
      val contentLengthHeaderValues = headers.getHeaderValue(CONTENT_LENGTH_HEADER)
      if (contentLengthHeaderValues.isPresent) {
        var contentLength: Option[Long] = None
        try {
          contentLength = Some(parseLong(contentLengthHeaderValues.get()))
        } catch {
          case _: NumberFormatException =>
          // Nothing to do
        }
        contentLength
      } else {
        None
      }
    } else {
      None
    }
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
