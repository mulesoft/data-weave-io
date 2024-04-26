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
  Cookie*?: String,
   _ ?: SimpleType
}

/**
 * DataWeave type for representing a HTTP cookies.
 */
type HttpCookies = {
  _ ?: SimpleType
}

/**
 * DataWeave type for representing a HTTP request query parameters.
 */
type QueryParams = {
  _ ?: String
}

/**
 * DataWeave type for representing an HTTP client configuration.
 * Supports the following fields:
 *
 * * `connectionTimeout`: The maximum time in millisecond an `HttpClient` can wait when connecting to a remote host.
 */
type HttpClientConfig = {
    connectionTimeout?: Number
}

/**
 * DataWeave type for representing an HTTP request configuration.
 * Supports the following fields:
 *
 * * `followRedirects`: Indicates if the HTTP request is to follow redirects.
 * * `readTimeout`: The desired HTTP request read timeout in millisecond.
 * * `requestTimeout`: The desired HTTP request timeout in millisecond.
 */
type HttpRequestConfig = {
  followRedirects?: Boolean,
  readTimeout?: Number,
  requestTimeout?: Number
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
 * DataWeave type for representing an HTTP respose.
 * Supports the following fields:
 *
 * * `contentType`: The HTTP response `Content-Type`.
 * * `status`: The HTTP response status.
 * * `statusText`: The HTTP response status message.
 * * `headers`: The HTTP response headers.
 * * `body`: The HTTP response body.
 * * `cookies`: The HTTP response cookies.
 */
type HttpResponse<BodyType <: HttpBody, HeadersType <: HttpHeaders> = {
  contentType?: String,
  status: Number,
  statusText?: String,
  headers: HeadersType,
  body?: BodyType,
  cookies: HttpCookies
}
/*
type HttpResponse1<HeadersType <: HttpHeaders> = {
  contentType?: String,
  status: Number,
  statusText?: String,
  headers: HeadersType,
  body?: Binary,
  cookies: HttpCookies
}

type HttpResponse2<BodyType <: HttpBody, HeadersType <: HttpHeaders> = HttpResponse1<HeadersType> & {
  payload: () -> BodyType
}
*/

/**
 * DataWeave type for representing an HTTP request.
 * Supports the following fields:
 *
 * * `method`: The HTTP request method.
 * * `url`: The HTTP request url.
 * * `headers`: The HTTP request header.
 * * `body`: The HTTP request body.
 */
type HttpRequest<T <: HttpBody> = {
  method: HttpMethod,
  url: String | UrlBuilder,
  headers?: HttpHeaders,
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


