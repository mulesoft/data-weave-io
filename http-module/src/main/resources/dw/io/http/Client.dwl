/**
* Http client module allows to make http calls
*/
%dw 2.0

import * from dw::core::Binaries
import * from dw::core::Objects
import * from dw::core::URL
import * from dw::io::http::BodyUtils
import * from dw::io::http::Types
import * from dw::io::http::utils::HttpHeaders
import * from dw::module::Multipart

fun customClientConfig(config: HttpClientConfig, prefix: String = "CUSTOM"): HttpClientConfig & {id: String} = { id: "$(prefix)-$(uuid())" } ++ config

var DEFAULT_HTTP_CLIENT_CONFIG = customClientConfig({}, "DEFAULT")

var DEFAULT_HTTP_REQUEST_CONFIG = {}

var DEFAULT_SERIALIZATION_CONFIG = {
  contentType: "application/json",
  readerProperties: {},
  writerProperties: {}
}

fun get<B <: HttpBody, H <: HttpHeaders>(url: String | UrlBuilder, headers: HttpHeaders | Null = null): HttpResponse<B, H> = do {
  var requestHeaders = if (headers == null) {}  else { headers: headers }
  var httpRequest = {
    method: "GET",
    url: url
  } mergeWith requestHeaders
  ---
  request(httpRequest)
}

fun post<B <: HttpBody, H <: HttpHeaders>(url: String | UrlBuilder, body: HttpBody | Null = null, headers: HttpHeaders | Null = null): HttpResponse<B, H> = do {
  var requestHeaders = if (headers == null) {} else { headers: headers }
  var requestBody = if (body == null) {} else { body: body }
  var httpRequest = {
    method: "POST",
    url: url
  } mergeWith
      requestHeaders
    mergeWith
      requestBody
  ---
  request(httpRequest)
}

fun postMultipart<B <: HttpBody, H <: HttpHeaders>(url: String | UrlBuilder, body: Multipart, headers: HttpHeaders | Null = null): HttpResponse<B, H> = do {
  // TODO: Review header, is it OK to add it?
  var httpHeaders = if (headers == null) { headers: { (CONTENT_TYPE_HEADER): "multipart/form-data" } } else { headers: headers }
  var newHeaders = if (httpHeaders.headers[CONTENT_TYPE_HEADER]?)
      httpHeaders
    else
      httpHeaders update {
        case .headers.CONTENT_TYPE_HEADER! -> "multipart/form-data"
      }
  var httpRequest = {
    method: "POST",
    url: url,
    body: body
  } mergeWith
    newHeaders
  ---
  request(httpRequest)
}

fun head<B <: HttpBody, H <: HttpHeaders>(url: String | UrlBuilder, headers: HttpHeaders | Null = null): HttpResponse<B, H> = do {
    var requestHeaders = if (headers == null) {} else { headers: headers }
    var httpRequest = {
        method: "HEAD",
        url: url
    } mergeWith
        requestHeaders
    ---
    request(httpRequest)
}

fun put<B <: HttpBody, H <: HttpHeaders>(url: String | UrlBuilder, body: HttpBody | Null = null, headers: HttpHeaders | Null = null): HttpResponse<B, H> = do {
  var requestHeaders = if (headers == null) {} else { headers: headers }
  var requestBody = if (body == null) {} else { body: body }
  var httpRequest = {
    method: "PUT",
    url: url
  } mergeWith
      requestHeaders
    mergeWith
      requestBody
  ---
  request(httpRequest)
}

fun delete<B <: HttpBody, H <: HttpHeaders>(url: String | UrlBuilder, body: HttpBody | Null = null, headers: HttpHeaders | Null = null): HttpResponse<B, H> = do {
  var requestHeaders = if (headers == null) {} else { headers: headers }
  var requestBody = if (body == null) {} else { body: body }
  var httpRequest = {
    method: "DELETE",
    url: url
  } mergeWith
     requestHeaders
    mergeWith
      requestBody
  ---
  request(httpRequest)
}

fun connect<B <: HttpBody, H <: HttpHeaders>(url: String | UrlBuilder, headers: HttpHeaders | Null = null): HttpResponse<B, H> = do {
  var requestHeaders = if (headers == null) {} else { headers: headers }
  var httpRequest = {
    method: "CONNECT",
    url: url
  } mergeWith
     requestHeaders
  ---
  request(httpRequest)
}

fun options<B <: HttpBody, H <: HttpHeaders>(url: String | UrlBuilder, headers: HttpHeaders | Null = null): HttpResponse<B, H> = do {
  var requestHeaders = if (headers == null) {} else { headers: headers }
  var httpRequest = {
    method: "OPTIONS",
    url: url
  } mergeWith
      requestHeaders
  ---
  request(httpRequest)
}

fun trace<B <: HttpBody, H <: HttpHeaders>(url: String | UrlBuilder, headers: HttpHeaders | Null = null): HttpResponse<B, H> = do {
  var requestHeaders = if (headers == null) {} else { headers: headers }
  var httpRequest = {
    method: "TRACE",
    url: url
  } mergeWith
      requestHeaders
  ---
  request(httpRequest)
}

fun patch<B <: HttpBody, H <: HttpHeaders>(url: String | UrlBuilder, body: HttpBody | Null = null, headers: HttpHeaders | Null = null): HttpResponse<B, H> = do {
  var requestHeaders = if (headers == null) {} else { headers: headers }
  var requestBody = if (body == null) {} else { body: body }
  var httpRequest = {
    method: "PATCH",
    url: url
  } mergeWith
      requestHeaders
    mergeWith
      requestBody
  ---
  request(httpRequest)
}

@RuntimePrivilege(requires = "http::Client")
fun httpRequest<H <: HttpHeaders>(
  request: HttpRequest<Binary>,
  requestConfig: HttpRequestConfig = DEFAULT_HTTP_REQUEST_CONFIG,
  clientConfig: HttpClientConfig & {id: String} = DEFAULT_HTTP_CLIENT_CONFIG): HttpResponse<Binary, H> = native("http::HttpRequestFunction")

fun request<B <: HttpBody, H <: HttpHeaders>(
  request: HttpRequest,
  requestConfig: HttpRequestConfig = DEFAULT_HTTP_REQUEST_CONFIG,
  serializationConfig: SerializationConfig = DEFAULT_SERIALIZATION_CONFIG,
  clientConfig: HttpClientConfig & {id: String} = DEFAULT_HTTP_CLIENT_CONFIG): HttpResponse<B, H> = do {
  var requestBody = request.body
  var requestWithBody = if (requestBody != null) do {
    var requestHeaders = request.headers default {}
    var binaryBody = toBinaryBody(requestBody, requestHeaders, serializationConfig)
    var headersWithContentType = requestHeaders
      mergeWith {
        (CONTENT_TYPE_HEADER): binaryBody.contentType,
      }
    ---
    {
      method: request.method,
      url: request.url,
      headers: headersWithContentType,
      body: binaryBody.body
    }
    } else {
      method: request.method,
      url: request.url,
      (headers: request.headers!) if (request.headers?)
    }

  var httpResponse = httpRequest(requestWithBody, requestConfig, clientConfig)
  var responseBody = httpResponse.body
  ---
  if (responseBody == null)
    httpResponse as HttpResponse<B, H>
  else do {
    var responseHeaders = normalizeHeaders(httpResponse.headers)
    var contentType = responseHeaders[CONTENT_TYPE_HEADER]
    var httpResponseWithBody = httpResponse mergeWith
      if (contentType != null) do {
        // TODO: Shoud use custom Mime function
        var mime = (contentType splitBy ";")[0]
        // TODO: Add test for lazyness (e.g a broken json response using just the raw). Alternative see HttpResponse2
        @Lazy
        var body = (safeReadBody(mime, responseBody, serializationConfig) as B) <~ { "mimeType": mime, "raw": responseBody }
        ---
        { body: body }
      } else do {
        @Lazy
        var body = responseBody <~ { "mimeType": null, "raw": responseBody }
        ---
        { body: body }
      }
    ---
    httpResponseWithBody as HttpResponse<B, H>
  }
}


//UTILITY FUNCTIONS
/**
* String interpolator function to build a URL
*
**/
fun url(parts: Array<String>, interpolation: Array<StringCoerceable>): String =
  parts[0] ++ (interpolation map (encodeURIComponent($ as String) ++ parts[($$ + 1)]) joinBy '')

/**
* Replace the templates of a url according to RFC6570
*/
fun resolveTemplateWith(uri: String, context: Object): String =
    uri replace /\{([^\/]+?)\}/ with ((groups, index) -> context[groups[1]] default index[0])

/**
* Utility function that adds the proper Authorization header based on the supported Auth type.
**/
fun createAuthorizationHeader(kind: OAuth | BasicAuth): {| Authorization: String |} = do {
    kind  match {
        case is OAuth -> { Authorization: "Bearer $($.token)"}
        case is BasicAuth -> do {
          var base = toBase64("$($.username):$($.password)" as Binary {encoding: "UTF-8"})
            ---
            { Authorization: "Basic $(base)"}
        }
    }
}