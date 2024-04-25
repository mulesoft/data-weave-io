%dw 2.0

type HttpBody = Any

type HttpMethod = "GET" | "HEAD" | "POST" | "PUT" | "DELETE" | "CONNECT" | "OPTIONS" | "TRACE" | "PATCH"

type HttpHeaders = {
  "Content-Type"?: String,
  Authorization?: String,
  ETag?: SimpleType,
  Cookie*?: String,
   _ ?: SimpleType
}

type HttpCookies = {
  _ ?: SimpleType
}

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
type HttpRequest<T <: HttpBody> = {
  method: HttpMethod,
  url: String | UrlBuilder,
  headers?: HttpHeaders,
  body?: T
}

type UrlBuilder = {
  url: String,
  queryParams?: QueryParams
}

type OAuth = {token: String}

type BasicAuth = {username: String, password: String}

// SERVER IMPLEMENTATION


