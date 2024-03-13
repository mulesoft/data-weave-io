package org.mule.weave.v2.module.http.service

import java.io.InputStream
import java.net.HttpCookie
import java.util.concurrent.CompletableFuture

trait HttpClientService {
  def request(config: HttpClientOptions): CompletableFuture[_ <: HttpClientResponse]
}

case class HttpClientOptions(
  /** Full url for the request, including domain */
  var url: String,

  /** HTTP Method */
  var method: String,

  var headers: Map[String, Seq[String]] = Map[String, Seq[String]](),
  var queryParams: Map[String, Seq[String]] = Map[String, Seq[String]](),
  var body: Option[InputStream] = None,

  /** Do we accept header redirections? */
  var allowRedirect: Boolean = false,

  var readTimeout: Option[Int] = None, // default 20000ms
  var requestTimeout: Option[Int] = None, // default 10000ms

  /**
    * Should HTTP compression be used?
    * If true, Accept-Encoding: gzip,deflate will be sent with request.
    * If the server response with Content-Encoding: (gzip|deflate) the client will automatically handle decompression
    *
    * This is true by default
    */
  var allowCompression: Boolean = true,
  var ssl: SSLOptions = SSLOptions(),
  var proxyConfig: Option[ProxyConfig] = None,
  var digestCreds: Option[(String, String)] = None)

case class ProxyConfig(host: String, port: Int)

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

trait HttpClientResponse {
  /** Example: 200 */
  def status: Int

  /** Response headers * */
  def headers: HttpClientHeaders

  def contentType: Option[String]

  /** Response's raw body */
  def body: Option[InputStream]

  def statusText: Option[String]

  /** Location header value sent for redirects. By default, this library will not follow redirects. */
  def location: Option[String] = {
    headers.headerValues("Location").headOption
  }

  /** Get the parsed cookies from the "Set-Cookie" header * */
  def cookies: Seq[HttpCookie] = {
    headers.headerValues("Set-Cookie").flatMap(x => {
      HttpCookie.parse(x).toArray(Array[HttpCookie]())
    })
  }
}

trait HttpClientHeaders {

  def headerNames: Array[String]

  def headerValues(name: String): Array[String]

  def headerValue(name: String): Option[String]
}

