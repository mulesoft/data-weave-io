package org.mule.weave.v2.module.http.functions.utils

import org.mule.weave.v2.core.util.ObjectValueUtils.select
import org.mule.weave.v2.core.util.ObjectValueUtils.selectObject
import org.mule.weave.v2.core.util.ObjectValueUtils.selectString
import org.mule.weave.v2.model.EvaluationContext
import org.mule.weave.v2.model.structure.ObjectSeq
import org.mule.weave.v2.model.types.BinaryType
import org.mule.weave.v2.model.types.NullType
import org.mule.weave.v2.model.types.ObjectType
import org.mule.weave.v2.model.types.StringType
import org.mule.weave.v2.module.http.HttpHeader
import org.mule.weave.v2.module.http.functions.HttpClientRequestConfig
import org.mule.weave.v2.module.http.functions.exceptions.DuplicatedCookieFieldException
import org.mule.weave.v2.module.http.functions.utils.HttpClientRequestConverter.BODY
import org.mule.weave.v2.module.http.functions.utils.HttpClientRequestConverter.COOKIES
import org.mule.weave.v2.module.http.functions.utils.HttpClientRequestConverter.HEADERS
import org.mule.weave.v2.module.http.functions.utils.HttpClientRequestConverter.METHOD
import org.mule.weave.v2.module.http.functions.utils.HttpClientRequestConverter.QUERY_PARAMS
import org.mule.weave.v2.module.http.functions.utils.HttpClientRequestConverter.URL
import org.mule.weave.v2.module.http.service.HttpClientHeaders
import org.mule.weave.v2.module.http.service.HttpClientQueryParams.HttpQueryParam
import org.mule.weave.v2.module.http.service.HttpClientRequest
import org.mule.weave.v2.parser.exception.WeaveRuntimeException
import org.mule.weave.v2.parser.location.LocationCapable

import java.io.InputStream

class HttpClientRequestConverter(
  request: ObjectSeq,
  requestConfig: HttpClientRequestConfig,
  location: LocationCapable) {

  def convert()(implicit ctx: EvaluationContext): HttpClientRequest = {
    val method = selectString(request, METHOD).getOrElse(throw new WeaveRuntimeException(s"Missing '$METHOD' value", location.location()))

    val builder = new HttpClientRequest.Builder()
      .setMethod(method)

    val url = extractUrl(request, location)
    builder.setUrl(url.url)

    // Configuring query params
    url.queryParams.foreach(queryParam => {
      builder.addQueryParam(queryParam)
    })

    // Configuring headers
    val httpClientHeaders = extractHeaders(request)
    httpClientHeaders.getHeaders.forEach(header => {
      builder.addHeader(header)
    })

    val maybeCookieHeader = httpClientHeaders.firstValueIgnoreCase(HttpHeader.COOKIE_HEADER)
    val cookiesValue = selectObject(request, COOKIES).getOrElse(ObjectSeq.empty).toSeq()

    // Cookie is allowed in a single place: Header or Cookie object
    if (maybeCookieHeader.isPresent && maybeCookieHeader.get.nonEmpty && cookiesValue.nonEmpty) {
      throw new DuplicatedCookieFieldException(location.location())
    } else {
      if (cookiesValue.nonEmpty) {
        val cookieValue = cookiesValue.map(kvp => {
          val name = kvp._1.evaluate.name
          val value = StringType.coerce(kvp._2).evaluate.toString
          s"$name=$value"
        }).mkString(";")
        builder.addHeader(HttpHeader.COOKIE_HEADER, cookieValue)
      }
    }

    // Configuring body
    val maybeBody = extractBody(request)
    if (maybeBody.isDefined) {
      val body = maybeBody.get
      builder.setBody(body)
    }

    builder.setFollowRedirect(requestConfig.followRedirects)

    if (requestConfig.readTimeout.isDefined) {
      builder.setReadTimeout(requestConfig.readTimeout.get)
    }

    if (requestConfig.requestTimeout.isDefined) {
      builder.setRequestTimeout(requestConfig.requestTimeout.get)
    }

    builder.setStreamResponse(requestConfig.streamResponse)

    builder.build()
  }

  private def extractUrl(request: ObjectSeq, location: LocationCapable)(implicit ctx: EvaluationContext): Url = {
    val urlValue = select(request, URL).getOrElse(throw new WeaveRuntimeException(s"Missing '$URL' value", location.location()))
    urlValue.evaluate match {
      case url: StringType.T =>
        Url(url.toString, Seq.empty[HttpQueryParam])

      case urlBuilder: ObjectType.T =>
        var queryParams = Seq.empty[HttpQueryParam]
        val urlObjectSeq = urlBuilder.materialize()
        val queryParamsValue = selectObject(urlObjectSeq, QUERY_PARAMS).getOrElse(ObjectSeq.empty)
        val url = selectString(urlObjectSeq, URL).getOrElse(throw new WeaveRuntimeException(s"Missing '$URL' value", location.location()))
        queryParamsValue.toSeq().foreach(kvp => {
          val name = kvp._1.evaluate.name
          val value = StringType.coerce(kvp._2).evaluate.toString
          queryParams = queryParams :+ new HttpQueryParam(name, value)
        })
        Url(url, queryParams)
    }
  }

  private def extractHeaders(request: ObjectSeq)(implicit ctx: EvaluationContext): HttpClientHeaders = {
    val builder = new HttpClientHeaders.Builder()
    val headersValue = selectObject(request, HEADERS).getOrElse(ObjectSeq.empty)
    headersValue.toSeq().foreach(kvp => {
      val name = kvp._1.evaluate.name
      val value = StringType.coerce(kvp._2).evaluate.toString
      builder.addHeader(name, value)
    })
    builder.build()
  }

  private def extractBody(request: ObjectSeq)(implicit ctx: EvaluationContext): Option[InputStream] = {
    val bodyValue = select(request, BODY)
    bodyValue.flatMap(body => {
      body match {
        case nullType if NullType.accepts(nullType) =>
          None
        case bt if BinaryType.accepts(bt) =>
          Some(BinaryType.coerce(body).evaluate.spinOff())
        case _ =>
          // TODO: Should fail ??
          None
      }
    })
  }
}

case class Url(url: String, queryParams: Seq[HttpQueryParam])

object HttpClientRequestConverter {
  private val METHOD = "method"
  private val URL = "url"
  private val QUERY_PARAMS = "queryParams"
  private val HEADERS = "headers"
  private val BODY = "body"
  private val COOKIES = "cookies"

  def apply(
    request: ObjectSeq,
    requestConfig: HttpClientRequestConfig,
    location: LocationCapable): HttpClientRequestConverter =
    new HttpClientRequestConverter(request, requestConfig, location)
}
