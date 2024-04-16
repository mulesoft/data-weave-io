%dw 2.0

// COMMON =========================================================
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

// CLIENT =========================================================

type HttpClientConfig = {
    connectionTimeout?: Number
}

type HttpRequestConfig = {
  followRedirects?: Boolean,
  readTimeout?: Number,
  requestTimeout?: Number
}

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


