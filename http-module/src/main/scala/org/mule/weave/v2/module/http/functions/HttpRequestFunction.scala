package org.mule.weave.v2.module.http.functions

import java.net.HttpCookie

import org.mule.weave.v2.core.functions.UnaryFunctionValue
import org.mule.weave.v2.model.EvaluationContext
import org.mule.weave.v2.model.capabilities.UnknownLocationCapable
import org.mule.weave.v2.model.structure.KeyValuePair
import org.mule.weave.v2.model.structure.ObjectSeq
import org.mule.weave.v2.model.types._
import org.mule.weave.v2.model.values._
import org.mule.weave.v2.module.http.HttpHeader
import org.mule.weave.v2.module.http.service.HttpClientOptions
import org.mule.weave.v2.module.http.service.HttpClientRequest
import org.mule.weave.v2.module.http.service.HttpClientResponse
import org.mule.weave.v2.module.http.service.HttpClientResult
import org.mule.weave.v2.module.http.service.HttpClientService

import scala.collection.JavaConverters._
import scala.collection.mutable

class HttpRequestFunction extends UnaryFunctionValue {

  override val R = ObjectType

  def doExecute(v: R.V)(implicit ctx: EvaluationContext): Value[_] = {

    val httpClientService = ctx.serviceManager.lookupCustomService(classOf[HttpClientService]).get

    val materializedRequestObject: Value[ObjectSeq] = ObjectType.coerce(v).materialize

    var options: HttpClientOptions = null

    try {
      val requestObject: ObjectSeq = materializedRequestObject.evaluate
      val method = try {
        StringType.coerce(requestObject.keyValueOf(KeyValue("method")).getOrElse(KeyValuePair(KeyValue("method"), StringValue("GET")))._2).evaluate.toUpperCase()
      } catch {
        case e: Throwable => throw new Exception(s"request.method must be a String", e)
      }

      val url = try {
        StringType.coerce(requestObject.keyValueOf(KeyValue("url")).get._2).evaluate
      } catch {
        case e: Throwable => throw new Exception(s"request.url must be a String", e)
      }

      options = HttpClientOptions(url, method)

      val payloadContent: Option[KeyValuePair] = requestObject.keyValueOf(KeyValue("body"))

      val hasPayload = payloadContent.isDefined

      if (hasPayload) {
        val stream = if (BinaryType.accepts(payloadContent.get._2)) {
          BinaryType.coerce(payloadContent.get._2).evaluate
        } else {
          throw new Exception("Cannot coerce request.payload to Binary")
        }
        options.body = Some(stream)
      }

      options.readTimeout = getRequestTimeout(requestObject, "readTimeout", 30000)
      options.connectionTimeout = getRequestTimeout(requestObject, "connectionTimeout", 10000)

      options.allowCompression = {
        val compress = requestObject.keyValueOf(KeyValue("allowCompression")).getOrElse(KeyValuePair(KeyValue("allowCompression"), BooleanValue(value = true)))

        val value: Value[Boolean] = try {
          BooleanType.coerce(compress._2)
        } catch {
          case e: Throwable => throw new Exception("request.allowCompression must be a Boolean", e)
        }

        value.evaluate
      }

      options.allowRedirect = {
        BooleanType
          .coerce(
            requestObject
              .keyValueOf(KeyValue("allowRedirect"))
              .getOrElse(KeyValuePair(KeyValue("allowRedirect"), BooleanValue(value = false)))
              ._2)
          .evaluate
      }

      options.ssl.allowSelfSignedCertificate = BooleanType
        .coerce(
          requestObject
            .keyValueOf(KeyValue("allowUnsafeSSL"))
            .getOrElse(KeyValuePair(KeyValue("allowUnsafeSSL"), BooleanValue(value = false)))
            ._2)
        .evaluate

      options.headers = {
        val maybeRequestHeaders = requestObject.keyValueOf(KeyValue("headers"))

        val requestHeaders: mutable.Map[String, Seq[String]] = mutable.Map()

        if (maybeRequestHeaders.isDefined) {
          val maybeRequestHeadersObject = try {
            ObjectType.coerce(maybeRequestHeaders.get._2).materialize
          } catch {
            case e: Throwable => throw new Exception("request.headers must be an Object", e)
          }

          maybeRequestHeadersObject.evaluate
            .toIterator()
            .foreach(kvpv => {
              val key = kvpv._1.evaluate
              val keyString = key.name

              val values = maybeRequestHeadersObject.evaluate.allKeyValuesOf(kvpv._1)

              if (values.isDefined) {
                values.get
                  .toSeq()
                  .foreach((value) => {
                    val headerValue = try {
                      StringType.coerce(value._2).evaluate
                    } catch {
                      case e: Throwable => throw new Exception(s"Invalid header value `$keyString`. It must be a String", e)
                    }
                    val newValue: Seq[String] = requestHeaders.getOrElse(keyString, Seq()) :+ headerValue
                    requestHeaders.update(keyString, newValue)
                  })
              }
            })
        }

        { // Request cookies
          val maybeRequestCookies = requestObject.keyValueOf(KeyValue("cookies"))

          if (maybeRequestCookies.isDefined) {
            val maybeRequestCookiesObject = try {
              ObjectType.coerce(maybeRequestCookies.get._2).materialize
            } catch {
              case e: Throwable => throw new Exception("request.cookies must be an Object", e)
            }

            maybeRequestCookiesObject.evaluate
              .toIterator()
              .foreach(kvpv => {
                val key = kvpv._1.evaluate
                val keyString = key.name

                val values = maybeRequestCookiesObject.evaluate.allKeyValuesOf(kvpv._1)

                if (values.isDefined) {
                  values.get
                    .toSeq()
                    .foreach((value) => {
                      val headerValue = StringType.coerce(value._2).evaluate

                      val cookie = keyString + "=" + headerValue
                      val newValue: Seq[String] = requestHeaders.getOrElse(HttpHeader.COOKIE_HEADER, Seq()) :+ cookie

                      requestHeaders.update(HttpHeader.COOKIE_HEADER, newValue)
                    })
                }
                values
              })
          }
        }

        requestHeaders.toMap
      }

      val result: HttpClientResult = httpClientService.request(options)

      resultToWeave(result, materializedRequestObject)
    } catch {
      case e: Throwable =>
        var resultKeys: Seq[KeyValuePair] = Seq()
        resultKeys :+= KeyValuePair(KeyValue("err"), BooleanValue(value = true))
        resultKeys :+= KeyValuePair(KeyValue("message"), StringValue(Option(e.getMessage).getOrElse(e.getClass.getSimpleName)))
        ObjectValue(resultKeys, UnknownLocationCapable)
    }
  }

  def responseToWeave($: HttpClientResponse)(implicit ctx: EvaluationContext): Value[_] = {
    var responseKeys: Seq[KeyValuePair] = Seq()

    responseKeys :+= KeyValuePair(KeyValue("status"), NumberValue($.status.intValue()))

    responseKeys :+= KeyValuePair(KeyValue("statusText"), StringValue($.statusText.getOrElse("").replaceAll("HTTP/1.1 " + $.status.toString + " ", "")))

    responseKeys :+= KeyValuePair(KeyValue("headers"), headersToWeave($.headers))

    if ($.headers.contains("Set-Cookie")) {
      responseKeys :+= KeyValuePair(KeyValue("cookies"), parseCookies($.header("Set-Cookie")))
    }

    if ($.payload.isDefined) {
      ctx.registerCloseable($.payload.get)
      responseKeys :+= KeyValuePair(KeyValue("payload"), BinaryValue($.payload.get))
    }

    ObjectValue(responseKeys, UnknownLocationCapable)
  }

  def headersToWeave(headers: Map[String, Seq[String]])(implicit ctx: EvaluationContext): ObjectValue = {
    var headersSeq: Seq[KeyValuePair] = Seq()

    headers.foreach { tuple =>
      for (value <- tuple._2) {
        headersSeq :+= KeyValuePair(KeyValue(tuple._1), StringValue(value))
      }
    }

    ObjectValue(headersSeq, UnknownLocationCapable)
  }

  def parseCookies(cookieHeader: Option[String])(implicit ctx: EvaluationContext): ObjectValue = {
    val cookiesSeq: Seq[KeyValuePair] = cookieHeader.map { value =>
      val cookies: Seq[HttpCookie] = HttpCookie.parse(value).asScala
      cookies.map { cookie =>
        var cookieObjectSeq: Seq[KeyValuePair] = Seq()
        if (cookie.getName != null)
          cookieObjectSeq :+= KeyValuePair(KeyValue("name"), StringValue(cookie.getName))
        if (cookie.getValue != null)
          cookieObjectSeq :+= KeyValuePair(KeyValue("value"), StringValue(cookie.getValue))
        if (cookie.getDomain != null)
          cookieObjectSeq :+= KeyValuePair(KeyValue("domain"), StringValue(cookie.getDomain))
        if (cookie.getComment != null)
          cookieObjectSeq :+= KeyValuePair(KeyValue("comment"), StringValue(cookie.getComment))
        if (cookie.getPath != null)
          cookieObjectSeq :+= KeyValuePair(KeyValue("path"), StringValue(cookie.getPath))
        if (cookie.getMaxAge != -1)
          cookieObjectSeq :+= KeyValuePair(KeyValue("maxAge"), NumberValue(cookie.getMaxAge.intValue()))
        if (cookie.isHttpOnly)
          cookieObjectSeq :+= KeyValuePair(KeyValue("httpOnly"), BooleanValue(cookie.isHttpOnly))
        if (cookie.isHttpOnly)
          cookieObjectSeq :+= KeyValuePair(KeyValue("secure"), BooleanValue(!cookie.isHttpOnly))
        KeyValuePair(KeyValue(cookie.getName), ObjectValue(cookieObjectSeq, UnknownLocationCapable))
      }
    }.getOrElse(Seq())
    ObjectValue(cookiesSeq, UnknownLocationCapable)
  }

  def requestToWeave($: HttpClientRequest)(implicit ctx: EvaluationContext): Value[_] = {
    var requestKeys: Seq[KeyValuePair] = Seq()

    requestKeys :+= KeyValuePair(KeyValue("httpVersion"), StringValue($.httpVersion))
    requestKeys :+= KeyValuePair(KeyValue("method"), StringValue($.method))
    requestKeys :+= KeyValuePair(KeyValue("url"), StringValue($.url))
    requestKeys :+= KeyValuePair(KeyValue("path"), StringValue($.path))
    requestKeys :+= KeyValuePair(KeyValue("ip"), StringValue($.ip))
    requestKeys :+= KeyValuePair(KeyValue("port"), NumberValue($.port.intValue()))
    requestKeys :+= KeyValuePair(KeyValue("headers"), headersToWeave($.headers))

    if ($.payload.isDefined) {
      requestKeys :+= KeyValuePair(KeyValue("payload"), BinaryValue($.payload.get))
    }

    ObjectValue(requestKeys, UnknownLocationCapable)
  }

  def timersToWeave(timers: Map[String, Number])(implicit ctx: EvaluationContext): Value[_] = {
    var timerKeys: Seq[KeyValuePair] = Seq()

    timers.foreach { x =>
      timerKeys :+= KeyValuePair(KeyValue(x._1), NumberValue(x._2.floatValue()))
    }

    if (timers.get("total").isEmpty)
      timerKeys :+= KeyValuePair(KeyValue("total"), NumberValue(0))

    ObjectValue(timerKeys, UnknownLocationCapable)
  }

  def resultToWeave($: HttpClientResult, options: Value[_])(implicit ctx: EvaluationContext): Value[_] = {
    var resultKeys: Seq[KeyValuePair] = Seq()
    try {
      resultKeys :+= KeyValuePair(KeyValue("options"), options)

      if ($.request.isDefined) {
        resultKeys :+= KeyValuePair(KeyValue("request"), requestToWeave($.request.get))
      }

      if ($.response.isDefined) {
        resultKeys :+= KeyValuePair(KeyValue("response"), responseToWeave($.response.get))
      }

      if ($.timers.isDefined)
        resultKeys :+= KeyValuePair(KeyValue("timers"), timersToWeave($.timers.getOrElse(Map())))

      if ($.message.isDefined) {
        resultKeys :+= KeyValuePair(KeyValue("message"), StringValue($.message.get, UnknownLocationCapable))
      }

      resultKeys :+= KeyValuePair(KeyValue("err"), BooleanValue($.err, UnknownLocationCapable))

    } catch {
      case e: Throwable =>
        e.printStackTrace()
        resultKeys :+= KeyValuePair(KeyValue("err"), BooleanValue(value = true))
        resultKeys :+= KeyValuePair(KeyValue("message"), StringValue(e.toString, UnknownLocationCapable))
    }

    ObjectValue(resultKeys, UnknownLocationCapable)
  }

  def getRequestTimeout(obj: ObjectSeq, key: String, default: Number)(implicit ctx: EvaluationContext): Int = {
    val timeoutKVP: KeyValuePair = obj.keyValueOf(KeyValue(key)).getOrElse(KeyValuePair(KeyValue(key), NumberValue(30000)))

    val value = try {
      NumberType.coerce(timeoutKVP._2)
    } catch {
      case e: Throwable => throw new Exception(s"request.$key must be a Number > 0", e)
    }

    val timeout = value.evaluate.toInt

    if (timeout <= 0) {
      throw new Exception(s"request.$key must be > 0")
    }

    timeout
  }

}
