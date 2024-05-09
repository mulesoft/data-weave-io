/**
* HTTP client module allows to make HTTP calls
*
* To use this module, you must import it to your DataWeave code, for example,
* by adding the line `import * from dw::io::http::Client` to the header of your
* DataWeave script.
*/
%dw 2.0

import * from dw::core::Binaries
import * from dw::core::URL
import * from dw::io::http::BodyUtils
import * from dw::io::http::Types
import * from dw::io::http::utils::HttpHeaders
import * from dw::module::Mime
import * from dw::module::Multipart

/**
* Helper function to create an identifiable HTTP client configuration with the desired `prefix`.
* This function returns the given `HttpClientConfig` with and an `id`.
*
* === Parameters
*
* [%header, cols="1,1,3"]
* |===
* | Name | Type | Description
* | config | `HttpClientConfig` | The desired HTTP client configuration.
* | prefix | `String` | The `prefix` to be used for creating the HTTP client configuration's `id`.
* |===
*/
fun identifiableHttpClientConfig(config: HttpClientConfig, prefix: String = "CUSTOM"): IdentifiableHttpClientConfig = { id: "$(prefix)-$(uuid())" } ++ config

/**
* Variable used to identify the default HTTP client configuration.
*/
var DEFAULT_HTTP_CLIENT_CONFIG = identifiableHttpClientConfig({}, "DEFAULT")

/**
* Variable used to identify the default HTTP request configuration.
*/
var DEFAULT_HTTP_REQUEST_CONFIG = {}

/**
* Variable used to identify the default HTTP serialization configuration.
*/
var DEFAULT_SERIALIZATION_CONFIG = {
  contentType: "application/json",
  readerProperties: {},
  writerProperties: {}
}

var OCTET_STREAM_MIME_TYPE = { 'type': "application", subtype: "octet-stream", parameters: {} }
var X_BINARY_MIME_TYPE = { 'type': "application", subtype: "x-binary", parameters: {} }

fun get<B <: HttpBody, H <: HttpHeaders>(url: String | UrlBuilder, headers: HttpHeaders = {}): HttpResponse<B, H> = do {
  var httpRequest =  createHttpRequest("GET", url, headers)
  ---
  request(httpRequest)
}

fun post<B <: HttpBody, H <: HttpHeaders>(url: String | UrlBuilder, headers: HttpHeaders = {}, body: HttpBody | Null = null): HttpResponse<B, H> = do {
  var httpRequest =  createHttpRequest("POST", url, headers, body)
  ---
  request(httpRequest)
}

fun postMultipart<B <: HttpBody, H <: HttpHeaders>(url: String | UrlBuilder, body: Multipart, headers: HttpHeaders = {}): HttpResponse<B, H> = do {
  var normalizedHeaders = normalizeHeaders(headers)
  var newHeaders = if (normalizedHeaders[CONTENT_TYPE_HEADER]?)
    headers
  else
    // Update 'Content-Type' header (using normalized headers to avoid Content-Type header duplication)
    normalizedHeaders update {
      case ."$(CONTENT_TYPE_HEADER)"! -> "multipart/form-data"
    }
  var httpRequest =  createHttpRequest("POST", url, newHeaders, body)
  ---
  request(httpRequest)
}

fun head<B <: HttpBody, H <: HttpHeaders>(url: String | UrlBuilder, headers: HttpHeaders = {}): HttpResponse<B, H> = do {
 var httpRequest =  createHttpRequest("HEAD", url, headers)
 ---
 request(httpRequest)
}

fun put<B <: HttpBody, H <: HttpHeaders>(url: String | UrlBuilder, headers: HttpHeaders = {}, body: HttpBody | Null = null): HttpResponse<B, H> = do {
  var httpRequest =  createHttpRequest("PUT", url, headers, body)
  ---
  request(httpRequest)
}

fun delete<B <: HttpBody, H <: HttpHeaders>(url: String | UrlBuilder, headers: HttpHeaders = {}, body: HttpBody | Null = null): HttpResponse<B, H> = do {
  var httpRequest =  createHttpRequest("DELETE", url, headers, body)
  ---
  request(httpRequest)
}

fun connect<B <: HttpBody, H <: HttpHeaders>(url: String | UrlBuilder, headers: HttpHeaders = {}): HttpResponse<B, H> = do {
  var httpRequest =  createHttpRequest("CONNECT", url, headers)
  ---
  request(httpRequest)
}

fun options<B <: HttpBody, H <: HttpHeaders>(url: String | UrlBuilder, headers: HttpHeaders = {}): HttpResponse<B, H> = do {
  var httpRequest =  createHttpRequest("OPTIONS", url, headers)
  ---
  request(httpRequest)
}

fun trace<B <: HttpBody, H <: HttpHeaders>(url: String | UrlBuilder, headers: HttpHeaders = {}): HttpResponse<B, H> = do {
  var httpRequest =  createHttpRequest("TRACE", url, headers)
  ---
  request(httpRequest)
}

fun patch<B <: HttpBody, H <: HttpHeaders>(url: String | UrlBuilder, headers: HttpHeaders = {}, body: HttpBody | Null = null): HttpResponse<B, H> = do {
  var httpRequest = createHttpRequest("PATCH", url, headers, body)
  ---
  request(httpRequest)
}

/**
* Helper function to create an `HttpRequest` instance.
*
* === Parameters
*
* [%header, cols="1,1,3"]
* |===
* | Name | Type | Description
* | method | `HttpMethod` | The desired HTTP request method.
* | url | `String &#124; UrlBuilder` | The desired HTTP request url.
* | headers | `HttpHeaders` | The HTTP request header to send.
* | body | `HttpBody &#124; Null` |  The HTTP request body to send.
* |===
*/
fun createHttpRequest<T <: HttpBody>(method: HttpMethod, url: String | UrlBuilder, headers: HttpHeaders = {}, body: T | Null = null): HttpRequest<T> =
  if (body != null) {
    method: method,
    url: url,
    headers: headers,
    body: body
  } else {
    method: method,
    url: url,
    headers: headers
  }

@RuntimePrivilege(requires = "http::Client")
fun httpRequest<H <: HttpHeaders>(
  request: HttpRequest<Binary>,
  requestConfig: HttpRequestConfig = DEFAULT_HTTP_REQUEST_CONFIG,
  clientConfig: IdentifiableHttpClientConfig = DEFAULT_HTTP_CLIENT_CONFIG): HttpResponse<Binary, H> = do {
    fun httpRequestNative(request: HttpRequest<Binary>, requestConfig: HttpRequestConfig, clientConfig: IdentifiableHttpClientConfig): HttpResponse<Binary, H> = native("http::HttpRequestFunction")
    ---
    httpRequestNative(request, requestConfig, clientConfig)
  }

/**
* Helper function to create a `HttpRequest` instances with `Binary` request body.
*
* === Parameters
*
* [%header, cols="1,1,3"]
* |===
* | Name | Type | Description
* | request | `HttpRequest` | The desired HTTP request to convert to a `HttpRequest` instances with `Binary` request body.
* | serializationConfig | `SerializationConfig` | The HTTP serialization configuration to use.
* |===
*/
fun createBinaryHttpRequest(request: HttpRequest, serializationConfig: SerializationConfig): HttpRequest<Binary> =
  if (request.body != null) do {
    var headers = request.headers default {}
    var normalizedHeaders = normalizeHeaders(headers)
    var requestContentType = normalizedHeaders[CONTENT_TYPE_HEADER] default serializationConfig.contentType
    var writerProperties = serializationConfig.writerProperties default {}
    var binaryBody = writeToBinary(request.body, requestContentType, writerProperties)
    // Update 'Content-Type' header (using normalized headers to avoid Content-Type header duplication)
    var headersWithContentType = normalizedHeaders
      update {
        case ."$(CONTENT_TYPE_HEADER)"! -> dw::module::Mime::toString(binaryBody.mime)
      }
    ---
    createHttpRequest(request.method, request.url, headersWithContentType, binaryBody.body)
  } else {
    method: request.method,
    url: request.url,
    (headers: request.headers!) if (request.headers?)
  }

fun request<B <: HttpBody, H <: HttpHeaders>(
  request: HttpRequest,
  requestConfig: HttpRequestConfig = DEFAULT_HTTP_REQUEST_CONFIG,
  serializationConfig: SerializationConfig = DEFAULT_SERIALIZATION_CONFIG,
  clientConfig: IdentifiableHttpClientConfig = DEFAULT_HTTP_CLIENT_CONFIG): HttpResponse<B, H> = do {
  var binaryRequest = createBinaryHttpRequest(request, serializationConfig)
  var httpResponse = httpRequest(binaryRequest, requestConfig, clientConfig)
  ---
  parseHttpResponse(httpResponse, serializationConfig)
}


/**
* Helper function to parse a `HttpResponse` with a `Binary` body instance.
*
* === Parameters
*
* [%header, cols="1,1,3"]
* |===
* | Name | Type | Description
* | httpResponse | `HttpResponse<Binary,H&#62;` | The desired `HttpResponse` to parse.
* | serializationConfig | `SerializationConfig` | The HTTP serialization configuration to use.
* |===
*/
fun parseHttpResponse<B <: HttpBody, H <: HttpHeaders>(httpResponse: HttpResponse<Binary, H>, serializationConfig: SerializationConfig): HttpResponse<B, H> = do {
  var responseBody = httpResponse.body
  ---
  if (responseBody == null)
    httpResponse as HttpResponse<B, H>
  else do {
    var httpResponseWithBody = httpResponse update {
      case .body -> do {
          parseHttpResponseBody(responseBody, serializationConfig)
        }
      }
    ---
    httpResponseWithBody //as HttpResponse<B, H>
  }
}

/**
* Helper function to parse a `HttpResponse` body `Binary` instance.
*
* === Parameters
*
* [%header, cols="1,1,3"]
* |===
* | Name | Type | Description
* | binaryBody | `Binary` | The desired body parse.
* | serializationConfig | `SerializationConfig` | The HTTP serialization configuration to use.
* |===
*/
fun parseHttpResponseBody<B <: HttpBody>(binaryBody: Binary, serializationConfig: SerializationConfig): B = do {
  var mimeType = binaryBody.^mimeType
  var body = if (mimeType != null) do {
    // TODO: W-15523320: Allow reading request body laziness
    var mimeResult = fromString(mimeType as String)
    @Lazy
    var parsedBody =
      if (mimeResult.success) do {
        var mime = mimeResult.result!
        var isBinaryMimeType = isHandledBy(OCTET_STREAM_MIME_TYPE, mime) or isHandledBy(X_BINARY_MIME_TYPE, mime)
        ---
        if (isBinaryMimeType)
          binaryBody
        else
          // Preserve mimeType and raw schemas
          readFromBinary(mime, binaryBody, serializationConfig.readerProperties default {})
      }
      else
        binaryBody
    ---
    parsedBody
  } else do {
    binaryBody
  }
  ---
  // Attach schemas
  (body as B) <~ { "mimeType": binaryBody.^mimeType, "raw": binaryBody.^raw }
}

/**
* String interpolator function to build a URL
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