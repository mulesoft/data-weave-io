/**
* Module with a set of `types` used to make HTTP calls
*
* To use this module, you must import it to your DataWeave code, for example,
* by adding the line `import * from dw::io::http::Types` to the header of your
* DataWeave script.
*/
%dw 2.0

/**
 * DataWeave type for representing a HTTP body.
 */
type HttpBody = Any

/**
 * DataWeave type for representing a HTTP request methods.
 */
type HttpMethod = "GET" | "HEAD" | "POST" | "PUT" | "DELETE" | "CONNECT" | "OPTIONS" | "TRACE" | "PATCH"

/**
 * DataWeave type for representing a HTTP headers.
 */
type HttpHeaders = {
  "Content-Type"?: String,
  Authorization?: String,
  ETag?: SimpleType,
   _ ?: SimpleType
}

/**
 * DataWeave type for representing a HTTP request query parameters.
 */
type QueryParams = {
  _ ?: String
}

/**
 * DataWeave type for representing a HTTP request cookies.
 */
type HttpRequestCookies = {
  _ ?: String
}

/**
 * DataWeave type for representing an HTTP client configuration.
 * Supports the following fields:
 *
 * * `connectionTimeout`: The maximum time in millisecond an `HttpClient` can wait when connecting to a remote host (default: 5000).
 * * `compressionHeader`: If `true` the `Accept-Encoding: gzip, deflate` HTTP header will be sent to each request (default: false).
 * * `decompress`: Defines whether responses should be decompressed automatically (default: true).
 * * `tls`: The TLS context configuration.
 * * * `insecure`: Defines whether the trust store should be insecure, meaning no certificate validations should be performed (default: false)
 */
type HttpClientConfig = {
  connectionTimeout?: Number,
  compressionHeader?: Boolean,
  decompress?: Boolean,
  tls?: {
    insecure?: Boolean
  }
}

/**
 * DataWeave type for representing an HTTP request configuration.
 * Supports the following fields:
 *
 * * `followRedirects`: Indicates if the HTTP request is to follow redirects. (default: false)
 * * `readTimeout`: The desired HTTP request read timeout in millisecond. (default: 60000)
 * * `requestTimeout`: The desired HTTP request timeout in millisecond. (default: 60000)
 */
type HttpRequestConfig = {
  followRedirects?: Boolean,
  readTimeout?: Number,
  requestTimeout?: Number,
}

/**
 * DataWeave type for representing an HTTP request configuration.
 * Supports the following fields:
 *
 * * `contentType`: The ContentType used for HTTP request serialization.
 * * `readerProperties`: The reader configuration properties used for read the HTTP response body.
 * * `writerProperties`: The writer configuration properties used for write the HTTP request body.
 */
type SerializationConfig = {
  contentType: String,
  readerProperties?: Object,
  writerProperties?: Object
}

/**
 * DataWeave type for representing an HTTP response.
 * Supports the following fields:
 *
 * * `status`: The HTTP response status.
 * * `headers`: The HTTP response headers.
 * * `cookies`: The HTTP response cookies.
 * * `contentType`: The HTTP response `Content-Type`.
 * * `statusText`: The HTTP response status message.
 * * `body`: The HTTP response body.
 */
type HttpResponse<BodyType <: HttpBody, HeadersType <: HttpHeaders> = {
  status: Number,
  headers: HeadersType,
  cookies: HttpResponseCookies,
  contentType?: String,
  statusText?: String,
  body?: BodyType
}

/**
 * DataWeave type for representing HTTP response cookies.
 */
type HttpResponseCookies = {
  _ ?: HttpResponseCookie
}

/**
 * DataWeave type for representing an HTTP response cookies.
 * Supports the following fields:
 *
 * * `name`: The name of the cookie.
 * * `value`: The value of the cookie.
 * * `maxAge`: The maximum age of the cookie, specified in seconds.
 * * `httpOnly`: `true` if this cookie contains the HttpOnly attribute.
 * * `secure`: `true` if sending this cookie should be restricted to a secure protocol, or `false` if the it can be sent using any protocol.
 * * `domain`: The domain name set for this cookie.
 * * `comment`: The comment describing the purpose of this cookie.
 * * `path`: The path on the server to which the browser returns this cookie.
 *
 */
type HttpResponseCookie = {
  name: String,
  value: String,
  maxAge: Number,
  httpOnly: Boolean,
  secure: Boolean,
  domain?: String,
  comment?: String,
  path?: String
}

/**
 * DataWeave type for representing an HTTP request.
 * Supports the following fields:
 *
 * * `method`: The HTTP request method.
 * * `url`: The HTTP request url.
 * * `headers`: The HTTP request header.
 * * `cookies`: The HTTP request cookies.
 * * `body`: The HTTP request body.
 */
type HttpRequest<T <: HttpBody> = {
  method: HttpMethod,
  url: String | UrlBuilder,
  headers?: HttpHeaders,
  cookies?: HttpRequestCookies,
  body?: T
}

/**
 * DataWeave type for representing an HTTP request URL.
 * Supports the following fields:
 *
 * * `url`: The HTTP request url.
 * * `queryParams`: The HTTP request query parameters.
 */
type UrlBuilder = {
  url: String,
  queryParams?: QueryParams
}

/**
 * DataWeave type for representing an OAuth.
 */
type OAuth = {token: String}

/**
 * DataWeave type for representing an basic Authorization.
 */
type BasicAuth = {username: String, password: String}


