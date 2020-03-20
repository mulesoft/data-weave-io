package org.mule.weave.v2.module.http.client

import java.io.ByteArrayOutputStream
import java.io.InputStream
import java.lang.reflect.Field
import java.net.ConnectException
import java.net.HttpURLConnection
import java.net.InetAddress
import java.net.InetSocketAddress
import java.net.MalformedURLException
import java.net.Proxy
import java.net.Socket
import java.net.SocketTimeoutException
import java.net.URI
import java.net.URISyntaxException
import java.net.URL
import java.net.URLDecoder
import java.net.URLEncoder
import java.net.UnknownHostException
import java.security.cert.X509Certificate
import java.util.zip.GZIPInputStream
import java.util.zip.InflaterInputStream
import javax.net.ssl.HandshakeCompletedEvent
import javax.net.ssl.HostnameVerifier
import javax.net.ssl.HttpsURLConnection
import javax.net.ssl.SSLContext
import javax.net.ssl.SSLHandshakeException
import javax.net.ssl.SSLSession
import javax.net.ssl.SSLSocketFactory
import javax.net.ssl.TrustManager
import javax.net.ssl.X509TrustManager

import com.sun.org.apache.xml.internal.security.utils.Base64
import org.mule.weave.v2.io.ByteArraySeekableStream
import org.mule.weave.v2.module.http.HttpHeader
import org.mule.weave.v2.module.http.service.HttpClientOptions
import org.mule.weave.v2.module.http.service.HttpClientRequest
import org.mule.weave.v2.module.http.service.HttpClientResponse
import org.mule.weave.v2.module.http.service.HttpClientResult
import org.mule.weave.v2.module.http.service.SSLOptions

import scala.collection.immutable.TreeMap
import scala.collection.mutable
import scala.util.Try
import scala.util.matching.Regex

class HttpRequest(options: HttpClientOptions) {
  System.setProperty("sun.net.http.allowRestrictedHeaders", "true")

  var timers: Seq[(String, Number)] = Seq()
  var startTimer: Long = System.nanoTime()
  var checkpointTimer: Long = startTimer

  def registerTimer(name: String): Unit = {
    val now = System.nanoTime()
    val time: Number = (now - checkpointTimer).toFloat / 1000000.0
    checkpointTimer = now
    timers :+= (name, time)
  }

  private def urlBuilder(req: HttpClientOptions): URL = {
    new URI(req.url).toURL
  }

  private def connectFunc(req: HttpClientOptions, conn: HttpURLConnection) = {
    if (req.body.isDefined && req.body.get != null) {
      conn.setDoOutput(true)
      conn.connect()
      registerTimer("connect")

      val in = req.body.get
      val out = conn.getOutputStream

      val ba = new Array[Byte](4096)

      def readOnce() {
        val len = in.read(ba)
        if (len > 0) out.write(ba, 0, len)
        if (len >= 0) readOnce()
      }

      readOnce()
    } else {
      conn.connect()
      registerTimer("connect")
    }

    registerTimer("send")
  }

  def getURLFile(url: URL): String = {
    var urlPath = url.getFile

    if (urlPath != null && urlPath.length != 0) {
      if (urlPath.charAt(0) == 63) {
        urlPath = "/" + urlPath
      }
    } else {
      urlPath = "/"
    }

    if (urlPath.indexOf(10) == -1)
      urlPath
    else
      throw new MalformedURLException("Illegal character in URL")
  }

  def doConnection(urlToFetch: Option[URL] = None): HttpClientResult = {

    val result = HttpClientResult(
      err = false,
      options = options)

    var targetUrl: URL = null

    try {
      targetUrl = urlToFetch.getOrElse(urlBuilder(options))

      val address: InetAddress = InetAddress.getByName(targetUrl.getHost)

      registerTimer("dns")

      options.proxyConfig.map(x => targetUrl.openConnection(x)).getOrElse(targetUrl.openConnection) match {
        case conn: HttpURLConnection =>
          method(options.method, conn)

          conn.setUseCaches(false)
          conn.setDefaultUseCaches(false)

          val req = HttpClientRequest(
            conn.getRequestMethod,
            targetUrl.toString,
            getURLFile(targetUrl))

          result.request = Some(req)

          val isSSL = targetUrl.toString.startsWith("https:")

          req.port =
            if (targetUrl.getPort == -1)
              if (isSSL)
                443
              else
                80
            else
              targetUrl.getPort

          val inetAddress: InetSocketAddress = new InetSocketAddress(address, req.port)

          req.ip = inetAddress.getAddress.getHostAddress

          options.headers.foreach {
            case (name, values) =>
              values.foreach { value => conn.setRequestProperty(name, value) }
          }

          if (!options.headers.contains(HttpHeader.ACCEPT_ENCODING_HEADER) && options.allowCompression) {
            conn.setRequestProperty(HttpHeader.ACCEPT_ENCODING_HEADER, "gzip,deflate")
          }

          var hostHeader = targetUrl.getHost

          if (!options.headers.contains(HttpHeader.HOST_HEADER)) {
            hostHeader = targetUrl.getHost
            if (req.port != 80) hostHeader = hostHeader + ":" + req.port
            conn.setRequestProperty(HttpHeader.HOST_HEADER, hostHeader)
          }

          if (!options.headers.contains(HttpHeader.CONNECTION_HEADER))
            conn.setRequestProperty(HttpHeader.CONNECTION_HEADER, "close")

          if (!options.headers.contains(HttpHeader.USER_AGENT_HEADER))
            conn.setRequestProperty(HttpHeader.USER_AGENT_HEADER, "DataWeave/2.0")

          if (!options.headers.contains(HttpHeader.ACCEPT_HEADER))
            conn.setRequestProperty(HttpHeader.ACCEPT_HEADER, "text/html, image/gif, image/jpeg, *; q=.2, */*; q=.2")

          configureSSL(conn, options.ssl)

          conn.setConnectTimeout(options.connectionTimeout.intValue())
          conn.setReadTimeout(options.readTimeout.intValue())
          conn.setInstanceFollowRedirects(false)

          val properties = conn.getRequestProperties

          val mutableHeaders: mutable.Map[String, Seq[String]] = mutable.Map()

          properties.forEach((name, values) => {
            if (values == null || values.size() == 0 || values.size() == 1 && values.get(0) == null) {
              req.path = name.substring(conn.getRequestMethod.length + 1, name.length - 9)
              req.httpVersion = name.substring(name.length - 8)
            } else {
              var newValue: Seq[String] = mutableHeaders.getOrElse(name, Seq())
              values.forEach(value => newValue :+= value)
              mutableHeaders.update(name, newValue)
            }
          })

          if (!mutableHeaders.contains(HttpHeader.HOST_HEADER)) {
            var newValue: Seq[String] = mutableHeaders.getOrElse(HttpHeader.HOST_HEADER, Seq())

            newValue :+= hostHeader

            mutableHeaders.update(HttpHeader.HOST_HEADER, newValue)
          }

          options.headers.foreach {
            case (name, values) =>
              if (!mutableHeaders.contains(name)) {
                val newValue: Seq[String] = mutableHeaders.getOrElse(name, values)

                mutableHeaders.update(name, newValue)
              }
          }

          req.headers = mutableHeaders.toMap
          req.payload = options.body

          try {
            connectFunc(options, conn)
            result.response = Some(toResponse(conn, conn.getInputStream))
            Try(conn.disconnect())
          } catch {
            case e: java.io.IOException if conn.getResponseCode > 0 =>
              result.response = Some(toResponse(conn, conn.getErrorStream))
              Try(conn.disconnect())
          }
      }
    } catch {
      case e: ConnectException =>
        result.err = true
        result.message = Some("Connection refused")
      case e: URISyntaxException =>
        result.err = true
        result.message = Some("Invalid URI, " + e.getMessage)
      case e: UnknownHostException =>
        result.err = true

        if (e.getMessage.contains(targetUrl.getHost))
          result.message = Some(s"Cannot resolve host: ${targetUrl.getHost}")
        else
          result.message = Some(s"Cannot resolve host: ${e.getMessage}")
      case e: SSLHandshakeException =>
        result.err = true
        result.message = Some(e.getMessage)
      case e: SocketTimeoutException =>
        result.err = true
        result.message = Some(e.getMessage)
      case e: Throwable =>
        result.err = true
        result.message = Some(e.toString)
    } finally {
      val now = System.nanoTime()
      val time: Number = (now - startTimer).toFloat / 1000000.0
      timers :+= ("total", time)

      result.timers = Some(timers.toMap)
    }

    result
  }

  private def toResponse(conn: HttpURLConnection, inputStream: InputStream): HttpClientResponse = {
    val responseCode: Int = conn.getResponseCode

    registerTimer("wait")

    val headers: Map[String, IndexedSeq[String]] = getResponseHeaders(conn)
    val statusText: Option[String] = getStatusText(conn)
    val encoding: Option[String] = headers.get("Content-Encoding").flatMap(_.headOption)
    // handle a WWW-Authenticate digest round-trip
    // check if digest header already exists to prevent infinite loops
    val AuthHeaderName = "Authorization"

    val redirection: Option[HttpClientResponse] =
      if (responseCode == 401 && !options.headers.exists(p => p._1 == AuthHeaderName && p._2.startsWith(DigestAuth.DigestPrefix))) {
        def toUri(url: URL): String = {
          url.getPath + Option(url.getQuery).map(q => "?" + q).getOrElse("")
        }

        for {
          (username, password) <- options.digestCreds
          authParams: WwwAuthenticate <- {
            headers.get("WWW-Authenticate").flatMap(_.headOption).flatMap(DigestAuth.getAuthDetails)
          }
          if authParams.authType.equalsIgnoreCase(DigestAuth.DigestPrefix)
          url = urlBuilder(options)
          digestResult <- DigestAuth.createHeaderValue(
            username,
            password,
            options.method,
            toUri(url),
            HttpConstants.readBytes(inputStream),
            authParams.params)
        } yield {
          val newOptions = options.copy(headers + digestResult)
          val httpResponse = new HttpRequest(newOptions)
          httpResponse.doConnection(Some(url)).response.get
        }
      } else None

    redirection.getOrElse {
      // HttpURLConnection won't redirect from https <-> http, so we handle manually here
      val redirectedResult =
        if (conn.getInstanceFollowRedirects && (responseCode == 301 || responseCode == 302)) {
          headers.get("Location").flatMap(_.headOption).map(location => {
            doConnection(Some(new URL(location))).response.get
          })
        } else None

      redirectedResult.getOrElse {
        val shouldDecompress = options.allowCompression && inputStream != null

        val theStream: InputStream = if (shouldDecompress && encoding.exists(_.equalsIgnoreCase("gzip"))) {
          new GZIPInputStream(inputStream)
        } else if (shouldDecompress && encoding.exists(_.equalsIgnoreCase("deflate"))) {
          new InflaterInputStream(inputStream)
        } else inputStream

        if (theStream != null) {
          val bytes = HttpConstants.readBytes(theStream)

          registerTimer("receive")

          theStream.close()

          val stream = new ByteArraySeekableStream(bytes)

          HttpClientResponse(responseCode, headers, Some(stream), statusText)
        } else {
          registerTimer("receive")

          HttpClientResponse(responseCode, headers, None, statusText)
        }
      }
    }
  }

  private def getResponseHeaders(conn: HttpURLConnection): Map[String, IndexedSeq[String]] = {
    // There can be multiple values for the same response header key (this is common with Set-Cookie)
    // http://stackoverflow.com/questions/4371328/are-duplicate-http-response-headers-acceptable

    // according to javadoc, there can be a headerField value where the HeaderFieldKey is null
    // at the 0th row in some implementations.  In that case it's the http status line
    new TreeMap[String, IndexedSeq[String]]()(Ordering.by(_.toLowerCase)) ++ {
      Stream
        .from(0)
        .map(i => i -> conn.getHeaderField(i))
        .takeWhile(_._2 != null)
        .map {
          case (i, value) =>
            Option(conn.getHeaderFieldKey(i)).getOrElse("-\n") -> value
        }
        .filter { x => x._1 != "-\n" }
        .groupBy(_._1)
        .mapValues(_.map(_._2).toIndexedSeq)
    }
  }

  private def getStatusText(conn: HttpURLConnection): Option[String] = {
    Stream
      .from(0)
      .map(i => i -> conn.getHeaderField(i))
      .takeWhile(_._2 != null)
      .map {
        case (i, value) =>
          Option(conn.getHeaderFieldKey(i)).getOrElse("-\n") -> value
      }
      .find { x => x._1 == "-\n" }
      .map { x => x._2 }
  }

  val officalHttpMethods = Set("GET", "POST", "HEAD", "OPTIONS", "PUT", "DELETE", "TRACE")

  private lazy val methodField: Field = {
    val m = classOf[HttpURLConnection].getDeclaredField("method")
    m.setAccessible(true)
    m
  }

  def method(methodOrig: String, c: HttpURLConnection): Unit = {
    val method = methodOrig.toUpperCase
    if (officalHttpMethods.contains(method)) {
      c.setRequestMethod(method)
    } else {
      // HttpURLConnection enforces a list of official http METHODs, but not everyone abides by the spec
      // this hack allows us set an unofficial http method
      c match {
        case cs: HttpsURLConnection =>
          cs.getClass.getDeclaredFields.find(_.getName == "delegate").foreach { del =>
            del.setAccessible(true)
            methodField.set(del.get(cs), method)
          }
        case c =>
          methodField.set(c, method)
      }
    }
  }

  def configureSSL(c: HttpURLConnection, ssl: SSLOptions): Unit = {
    c match {
      case httpsConn: HttpsURLConnection =>

        val sslFactory: SSLSocketFactory = {
          if (ssl.allowSelfSignedCertificate)
            allowUnsafeSSL(httpsConn)
          else
            SSLSocketFactory.getDefault.asInstanceOf[SSLSocketFactory]
        }
        // TODO sslSocketFactory

        httpsConn.setSSLSocketFactory(new TLSSniSocketFactory(sslFactory) {
          override def handshakeCompleted(a: HandshakeCompletedEvent): Unit = {
            registerTimer("ssl")
          }
        })
      case _ => // do nothing
    }
  }

  /** Ignore the cert chain */
  def allowUnsafeSSL(httpsConn: HttpsURLConnection): SSLSocketFactory = {
    val hv = new HostnameVerifier() {
      def verify(urlHostName: String, session: SSLSession) = true
    }

    httpsConn.setHostnameVerifier(hv)

    val trustAllCerts = Array[TrustManager](new X509TrustManager() {
      def getAcceptedIssuers: Array[X509Certificate] = null

      def checkClientTrusted(certs: Array[X509Certificate], authType: String) {}

      def checkServerTrusted(certs: Array[X509Certificate], authType: String) {}
    })

    val sc = SSLContext.getInstance("SSL")
    sc.init(null, trustAllCerts, new java.security.SecureRandom())

    sc.getSocketFactory
  }
}

/**
  * Mostly helper methods
  */
object HttpConstants {
  val CharsetRegex = new Regex("(?i)\\bcharset=\\s*\"?([^\\s;\"]*)")

  val setFixedLengthStreamingMode: (HttpURLConnection, Long) => Unit = {
    val connClass = classOf[HttpURLConnection]
    val (isLong, theMethod) = try {
      true -> connClass.getDeclaredMethod("setFixedLengthStreamingMode", java.lang.Long.TYPE)
    } catch {
      case e: NoSuchMethodException =>
        false -> connClass.getDeclaredMethod("setFixedLengthStreamingMode", java.lang.Integer.TYPE)
    }
    (conn, length) =>
      if (isLong) {
        theMethod.invoke(conn, length: java.lang.Long)
      } else {
        if (length > Int.MaxValue) {
          throw new RuntimeException("Failing attempt to upload file greater than 2GB on java version < 1.7")
        }
        theMethod.invoke(conn, length.toInt: java.lang.Integer)
      }
  }

  def urlEncode(name: String, charset: String): String = URLEncoder.encode(name, charset)

  def urlDecode(name: String, charset: String): String = URLDecoder.decode(name, charset)

  def base64(bytes: Array[Byte]): String = new String(Base64.encode(bytes))

  def base64(in: String): String = base64(in.getBytes(utf8))

  def basicAuthValue(user: String, password: String): String = {
    "Basic " + base64(user + ":" + password)
  }

  def toQs(params: Seq[(String, String)], charset: String): String = {
    params.map(p => urlEncode(p._1, charset) + "=" + urlEncode(p._2, charset)).mkString("&")
  }

  def appendQs(url: String, params: Seq[(String, String)], charset: String): String = {
    url + (if (params.isEmpty) "" else {
      (if (url.contains("?")) "&" else "?") + toQs(params, charset)
    })
  }

  def normalizeParams(params: Seq[(String, String)]) = {
    percentEncode(params).sortWith(_ < _).mkString("&")
  }

  def percentEncode(params: Seq[(String, String)]): Seq[String] = {
    params.map(p => percentEncode(p._1) + "=" + percentEncode(p._2))
  }

  def percentEncode(s: String): String = {
    if (s == null) "" else {
      HttpConstants.urlEncode(s, HttpConstants.utf8).replace("+", "%20").replace("*", "%2A").replace("%7E", "~")
    }
  }

  /**
    * [lifted from lift]
    * Read all data from a stream into an Array[Byte]
    */
  def readBytes(in: InputStream): Array[Byte] = {
    if (in == null) {
      Array[Byte]()
    } else {
      val bos = new ByteArrayOutputStream
      val ba = new Array[Byte](4096)

      def readOnce {
        val len = in.read(ba)
        if (len > 0) bos.write(ba, 0, len)
        if (len >= 0) readOnce
      }

      readOnce

      bos.toByteArray
    }
  }

  def proxy(host: String, port: Int, proxyType: Proxy.Type = Proxy.Type.HTTP): Proxy = {
    new Proxy(proxyType, new InetSocketAddress(host, port))
  }

  val utf8 = "UTF-8"

}