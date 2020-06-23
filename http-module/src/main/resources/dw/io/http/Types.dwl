%dw 2.0

// COMMON =========================================================

type HttpResponseCookie = {
  name: String,
  value: String,
  domain?: String,
  comment?: String,
  path?: String,
  maxAge?: Number,
  httpOnly?: Boolean,
  secure?: Boolean
}

type HttpResponseCookies = {
  _ ?: HttpResponseCookie
}

type HttpBody = Any

type HttpHeaders = {
  _ ?: SimpleType
}

type HttpStrictHeaders = {
  _ ?: String
}


type QueryParams = {
  _ ?: String
}

// CLIENT =========================================================

type HttpClientRequiredOptions = {
  /** Full url for the request, including domain */
  url: String,
  /** HTTP Method */
  method: String
}

type HttpClientOptionalOptions = {
  headers?: HttpHeaders,
  body?: Any,

  /** Do we accept header redirections? */
  allowRedirect?: Boolean,

  /** Accept self signed server certificates */
  allowUnsafeSSL?: Boolean,

  readTimeout?: Number, // default 20000ms
  connnectionTimeout?: Number, // default 10000ms

  /** Should HTTP compression be used?
    * If true, Accept-Encoding: gzip,deflate will be sent with request.
    * If the server response with Content-Encoding: (gzip|deflate) the client will automatically handle decompression
    *
    * This is true by default
    */
  allowCompression?: Boolean
}

type HttpClientOptions = HttpClientRequiredOptions & HttpClientOptionalOptions

type HttpClientResponse<Body, Headers <: Dictionary<String>> = {
  /** Example: 200 */
  status: Number,
  /** Example: "OK" */
  statusText: String,
  /** Response headers **/
  headers: Headers,
  /** Response's raw body */
  payload?: Binary | String,
  /** If we can parse the body response */
  body?: Body,
  /** Get the parsed cookies from the "Set-Cookie" header **/
  cookies?: HttpResponseCookies,
  /** Content type header */
  contentType?: String,
  /** MIME type of the content type, without encoding */
  mime?: String
}

type HttpClientRequest = {
  httpVersion: String,
  url: String,
  path: String,
  method: String,
  ip: String,
  port: Number,
  headers?: HttpStrictHeaders,
  payload?: Binary | String
}

type HARTimers = {-|
  blocked?: Number, // Time spent in a queue waiting for a network connection.
  dns?: Number,     // DNS resolution time. The time required to resolve a host name.
  connect?: Number, // Time required to create TCP connection.
  send?: Number,    // Time required to send HTTP request to the server.
  wait?: Number,    // Waiting for a response from the server.
  receive?: Number, // Time required to read entire response from the server (or cache).
  ssl?: Number,     // Time required for SSL/TLS negotiation. If this field is defined then the time is also included in the connect field (to ensure backward compatibility with HAR 1.1).
  total: Number
|-}

type HttpClientResult = {
  err: Boolean,
  message?: String,
  options?: Object,
  request?: HttpClientRequest,
  /** Timing metrics, all values are accumulative except for ssl, it is included inside connect when available */
  timers?: HARTimers,
  response?: HttpClientResponse<Any, HttpStrictHeaders> //,
  // redirects?: Array<HttpClientResult>
}

// SERVER IMPLEMENTATION



type HttpServerResponse<BodyType <: HttpBody, HeaderType <: HttpHeaders> = {
  headers?: HeaderType,
  body?: BodyType,
  status?: Number
}

type HttpServerRequest<BodyType, HeaderType <: HttpHeaders, QueryParamsType <: QueryParams> = {
  headers: HttpHeaders,
  method: String,
  path: String,
  queryParams: QueryParamsType,
  body: BodyType
}

type HttpHandler<
                    RequestType,
                    RequestHeaderType <: HttpHeaders,
                    QueryParamsType <: QueryParams,
                    HttpServerResponseType <: HttpServerResponse<Any, HttpHeaders>
                 > =
                    (HttpServerRequest<RequestType, RequestHeaderType, QueryParamsType>) -> HttpServerResponseType

type HttpServerOptions = {
  port: Number,
  host: String,
  contentType?: String
}

type HttpServer = {|
  running: Boolean,
  port: Number,
  host: String,
  stop: () -> Boolean
|}

