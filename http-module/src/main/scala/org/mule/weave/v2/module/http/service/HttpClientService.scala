package org.mule.weave.v2.module.http.service

import java.io.InputStream
import java.net.HttpCookie
import java.net.Proxy

trait HttpClientService {
  def request(config: HttpClientOptions): HttpClientResult
}

case class HttpClientOptions(
  /** Full url for the request, including domain */
  var url: String,

  /** HTTP Method */
  var method: String,

  var headers: Map[String, Seq[String]] = Map[String, Seq[String]](),
  var body: Option[InputStream] = None,

  /** Do we accept header redirections? */
  var allowRedirect: Boolean = false,

  var readTimeout: Number = 20000, // default 20000ms
  var connectionTimeout: Number = 10000, // default 10000ms

  /**
    * Should HTTP compression be used?
    * If true, Accept-Encoding: gzip,deflate will be sent with request.
    * If the server response with Content-Encoding: (gzip|deflate) the client will automatically handle decompression
    *
    * This is true by default
    */
  var allowCompression: Boolean = true,
  var ssl: SSLOptions = SSLOptions(),
  var proxyConfig: Option[Proxy] = None,
  var digestCreds: Option[(String, String)] = None)

case class SSLOptions(
  /** Accept self signed server certificates */
  var allowSelfSignedCertificate: Boolean = false,
  var clientCert: Option[String] = None,
  var password: Option[String] = None,
  var keyStoreType: Option[String] = None,
  var sslProtocol: String = "TLS")

case class HttpClientRequest(

  var method: String,
  var url: String,
  var path: String = "",
  var httpVersion: String = "HTTP/1.1",
  var ip: String = "0.0.0.0",
  var port: Int = -1,
  var headers: Map[String, Seq[String]] = Map(),
  var payload: Option[InputStream] = None)

case class HttpClientResponse(
    /** Example: 200 */
    status: Number,

    /** Response headers **/
    headers: Map[String, Seq[String]],

    /** Response's raw body */
    payload: Option[InputStream] = None,

    statusText: Option[String] = None) {
  /** Get the response header value for a key */
  def header(key: String): Option[String] = headers.get(key).flatMap(_.headOption)

  /** Get all the response header values for a repeated key */
  def headerSeq(key: String): Seq[String] = headers.getOrElse(key, Seq.empty)

  /** Location header value sent for redirects. By default, this library will not follow redirects. */
  def location: Option[String] = header("Location")

  /** Get the parsed cookies from the "Set-Cookie" header **/
  def cookies: Seq[HttpCookie] = {
    headerSeq("Set-Cookie").flatMap(x => HttpCookie.parse(x).toArray().asInstanceOf[Array[HttpCookie]])
  }
}

case class HttpClientResult(
  var err: Boolean,
  var options: HttpClientOptions,
  var message: Option[String] = None,
  var request: Option[HttpClientRequest] = None,

  /** Timing metrics, all values are accumulative except for ssl, it is included inside connect when available */
  var timers: Option[Map[String, Number]] = None,
  var response: Option[HttpClientResponse] = None,
  var redirects: Option[Seq[HttpClientResponse]] = None)
