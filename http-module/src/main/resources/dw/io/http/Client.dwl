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
import * from dw::module::Multipart

/**
* Variable used to identify the default HTTP client configuration.
*/
var DEFAULT_HTTP_CLIENT_CONFIG = {
  connectionTimeout: 5000,
  compressionHeader: false,
  decompress: true,
  tls: {
    insecure: false
  }
}

/**
* Variable used to identify the default HTTP request configuration.
*/
var DEFAULT_HTTP_REQUEST_CONFIG = {
  followRedirects: false,
  readTimeout: 60000,
  requestTimeout: 60000,
  streamResponse: false,
  enableMetrics: false
}

/**
* Variable used to identify the default HTTP serialization configuration.
*/
var DEFAULT_SERIALIZATION_CONFIG = {
  contentType: "application/json",
  readerProperties: {},
  writerProperties: {}
}

/**
* Helper function to send a `GET` HTTP request.
*
* === Parameters
*
* [%header, cols="1,1,3"]
* |===
* | Name | Type | Description
* | url | `String &#124; UrlBuilder` | The desired HTTP request url.
* | headers | `HttpHeaders` | The HTTP request header to send.
* | requestConfig | `HttpRequestConfig` | The HTTP request configuration to use.
* | serializationConfig | `SerializationConfig` | The HTTP serialization configuration to use.
* | clientConfig | `HttpClientConfig` | The HTTP client configuration configuration to use.
* |===
*/
fun get<B <: HttpBody, H <: HttpHeaders>(url: String | UrlBuilder,
  headers: HttpHeaders = {},
  requestConfig: HttpRequestConfig = DEFAULT_HTTP_REQUEST_CONFIG,
  serializationConfig: SerializationConfig = DEFAULT_SERIALIZATION_CONFIG,
  clientConfig: HttpClientConfig = DEFAULT_HTTP_CLIENT_CONFIG): HttpResponse<B, H> = do {
  var httpRequest =  createHttpRequest("GET", url, headers)
  ---
  sendRequestAndReadResponse(httpRequest, requestConfig, serializationConfig, clientConfig)
}

/**
* Helper function to send a `POST` HTTP request.
*
* === Parameters
*
* [%header, cols="1,1,3"]
* |===
* | Name | Type | Description
* | url | `String &#124; UrlBuilder` | The desired HTTP request url.
* | headers | `HttpHeaders` | The HTTP request header to send.
* | body | `HttpBody &#124; Null` | The HTTP request body to send.
* | requestConfig | `HttpRequestConfig` | The HTTP request configuration to use.
* | serializationConfig | `SerializationConfig` | The HTTP serialization configuration to use.
* | clientConfig | `HttpClientConfig` | The HTTP client configuration configuration to use.
* |===
*/
fun post<B <: HttpBody, H <: HttpHeaders>(url: String | UrlBuilder,
  headers: HttpHeaders = {},
  body: HttpBody | Null = null,
  requestConfig: HttpRequestConfig = DEFAULT_HTTP_REQUEST_CONFIG,
  serializationConfig: SerializationConfig = DEFAULT_SERIALIZATION_CONFIG,
  clientConfig: HttpClientConfig = DEFAULT_HTTP_CLIENT_CONFIG): HttpResponse<B, H> = do {
  var httpRequest =  createHttpRequest("POST", url, headers, body)
  ---
  sendRequestAndReadResponse(httpRequest, requestConfig, serializationConfig, clientConfig)
}

/**
* Helper function to send a `multipart` HTTP request.
*
* === Parameters
*
* [%header, cols="1,1,3"]
* |===
* | Name | Type | Description
* | url | `String &#124; UrlBuilder` | The desired HTTP request url.
* | body | `Multipart` | The HTTP request body to send.
* | headers | `HttpHeaders` | The HTTP request header to send.
* | requestConfig | `HttpRequestConfig` | The HTTP request configuration to use.
* | serializationConfig | `SerializationConfig` | The HTTP serialization configuration to use.
* | clientConfig | `HttpClientConfig` | The HTTP client configuration configuration to use.
* |===
*/
fun postMultipart<B <: HttpBody, H <: HttpHeaders>(url: String | UrlBuilder,
  body: Multipart,
  headers: HttpHeaders = {},
  requestConfig: HttpRequestConfig = DEFAULT_HTTP_REQUEST_CONFIG,
  serializationConfig: SerializationConfig = DEFAULT_SERIALIZATION_CONFIG,
  clientConfig: HttpClientConfig = DEFAULT_HTTP_CLIENT_CONFIG): HttpResponse<B, H> = do {
  var contentTypeHeaders = allHeadersWith(headers, CONTENT_TYPE_HEADER)
  var newHeaders = if (isEmpty(contentTypeHeaders))
    // Set 'Content-Type' header
    headers update {
      case ."$(CONTENT_TYPE_HEADER)"! -> "multipart/form-data"
    }
  else
    headers
  var httpRequest =  createHttpRequest("POST", url, newHeaders, body)
  ---
  sendRequestAndReadResponse(httpRequest, requestConfig, serializationConfig, clientConfig)
}

/**
* Helper function to send a `HEAD` HTTP request.
*
* === Parameters
*
* [%header, cols="1,1,3"]
* |===
* | Name | Type | Description
* | url | `String &#124; UrlBuilder` | The desired HTTP request url.
* | headers | `HttpHeaders` | The HTTP request header to send.
* | requestConfig | `HttpRequestConfig` | The HTTP request configuration to use.
* | serializationConfig | `SerializationConfig` | The HTTP serialization configuration to use.
* | clientConfig | `HttpClientConfig` | The HTTP client configuration configuration to use.
* |===
*/
fun head<B <: HttpBody, H <: HttpHeaders>(url: String | UrlBuilder,
  headers: HttpHeaders = {},
  requestConfig: HttpRequestConfig = DEFAULT_HTTP_REQUEST_CONFIG,
  serializationConfig: SerializationConfig = DEFAULT_SERIALIZATION_CONFIG,
  clientConfig: HttpClientConfig = DEFAULT_HTTP_CLIENT_CONFIG): HttpResponse<B, H> = do {
 var httpRequest =  createHttpRequest("HEAD", url, headers)
 ---
 sendRequestAndReadResponse(httpRequest, requestConfig, serializationConfig, clientConfig)
}

/**
* Helper function to send a `PUT` HTTP request.
*
* === Parameters
*
* [%header, cols="1,1,3"]
* |===
* | Name | Type | Description
* | url | `String &#124; UrlBuilder` | The desired HTTP request url.
* | headers | `HttpHeaders` | The HTTP request header to send.
* | body | `HttpBody &#124; Null` | The HTTP request body to send.
* | requestConfig | `HttpRequestConfig` | The HTTP request configuration to use.
* | serializationConfig | `SerializationConfig` | The HTTP serialization configuration to use.
* | clientConfig | `HttpClientConfig` | The HTTP client configuration configuration to use.
* |===
*/
fun put<B <: HttpBody, H <: HttpHeaders>(url: String | UrlBuilder,
  headers: HttpHeaders = {},
  body: HttpBody | Null = null,
  requestConfig: HttpRequestConfig = DEFAULT_HTTP_REQUEST_CONFIG,
  serializationConfig: SerializationConfig = DEFAULT_SERIALIZATION_CONFIG,
  clientConfig: HttpClientConfig = DEFAULT_HTTP_CLIENT_CONFIG): HttpResponse<B, H> = do {
  var httpRequest =  createHttpRequest("PUT", url, headers, body)
  ---
  sendRequestAndReadResponse(httpRequest)
}

/**
* Helper function to send a `DELETE` HTTP request.
*
* === Parameters
*
* [%header, cols="1,1,3"]
* |===
* | Name | Type | Description
* | url | `String &#124; UrlBuilder` | The desired HTTP request url.
* | headers | `HttpHeaders` | The HTTP request header to send.
* | body | `HttpBody &#124; Null` | The HTTP request body to send.
* | requestConfig | `HttpRequestConfig` | The HTTP request configuration to use.
* | serializationConfig | `SerializationConfig` | The HTTP serialization configuration to use.
* | clientConfig | `HttpClientConfig` | The HTTP client configuration configuration to use.
* |===
*/
fun delete<B <: HttpBody, H <: HttpHeaders>(url: String | UrlBuilder,
  headers: HttpHeaders = {},
  body: HttpBody | Null = null,
  requestConfig: HttpRequestConfig = DEFAULT_HTTP_REQUEST_CONFIG,
  serializationConfig: SerializationConfig = DEFAULT_SERIALIZATION_CONFIG,
  clientConfig: HttpClientConfig = DEFAULT_HTTP_CLIENT_CONFIG): HttpResponse<B, H> = do {
  var httpRequest =  createHttpRequest("DELETE", url, headers, body)
  ---
  sendRequestAndReadResponse(httpRequest, requestConfig, serializationConfig, clientConfig)
}

/**
* Helper function to send a `CONNECT` HTTP request.
*
* === Parameters
*
* [%header, cols="1,1,3"]
* |===
* | Name | Type | Description
* | url | `String &#124; UrlBuilder` | The desired HTTP request url.
* | headers | `HttpHeaders` | The HTTP request header to send.
* | requestConfig | `HttpRequestConfig` | The HTTP request configuration to use.
* | serializationConfig | `SerializationConfig` | The HTTP serialization configuration to use.
* | clientConfig | `HttpClientConfig` | The HTTP client configuration configuration to use.
* |===
*/
fun connect<B <: HttpBody, H <: HttpHeaders>(url: String | UrlBuilder,
  headers: HttpHeaders = {},
  requestConfig: HttpRequestConfig = DEFAULT_HTTP_REQUEST_CONFIG,
  serializationConfig: SerializationConfig = DEFAULT_SERIALIZATION_CONFIG,
  clientConfig: HttpClientConfig = DEFAULT_HTTP_CLIENT_CONFIG): HttpResponse<B, H> = do {
  var httpRequest =  createHttpRequest("CONNECT", url, headers)
  ---
  sendRequestAndReadResponse(httpRequest, requestConfig, serializationConfig, clientConfig)
}

/**
* Helper function to send a `OPTIONS` HTTP request.
*
* === Parameters
*
* [%header, cols="1,1,3"]
* |===
* | Name | Type | Description
* | url | `String &#124; UrlBuilder` | The desired HTTP request url.
* | headers | `HttpHeaders` | The HTTP request header to send.
* | requestConfig | `HttpRequestConfig` | The HTTP request configuration to use.
* | serializationConfig | `SerializationConfig` | The HTTP serialization configuration to use.
* | clientConfig | `HttpClientConfig` | The HTTP client configuration configuration to use.
* |===
*/
fun options<B <: HttpBody, H <: HttpHeaders>(url: String | UrlBuilder,
  headers: HttpHeaders = {},
  requestConfig: HttpRequestConfig = DEFAULT_HTTP_REQUEST_CONFIG,
  serializationConfig: SerializationConfig = DEFAULT_SERIALIZATION_CONFIG,
  clientConfig: HttpClientConfig = DEFAULT_HTTP_CLIENT_CONFIG): HttpResponse<B, H> = do {
  var httpRequest =  createHttpRequest("OPTIONS", url, headers)
  ---
  sendRequestAndReadResponse(httpRequest, requestConfig, serializationConfig, clientConfig)
}

/**
* Helper function to send a `TRACE` HTTP request.
*
* === Parameters
*
* [%header, cols="1,1,3"]
* |===
* | Name | Type | Description
* | url | `String &#124; UrlBuilder` | The desired HTTP request url.
* | headers | `HttpHeaders` | The HTTP request header to send.
* | requestConfig | `HttpRequestConfig` | The HTTP request configuration to use.
* | serializationConfig | `SerializationConfig` | The HTTP serialization configuration to use.
* | clientConfig | `HttpClientConfig` | The HTTP client configuration configuration to use.
* |===
*/
fun trace<B <: HttpBody, H <: HttpHeaders>(url: String | UrlBuilder,
  headers: HttpHeaders = {},
  requestConfig: HttpRequestConfig = DEFAULT_HTTP_REQUEST_CONFIG,
  serializationConfig: SerializationConfig = DEFAULT_SERIALIZATION_CONFIG,
  clientConfig: HttpClientConfig = DEFAULT_HTTP_CLIENT_CONFIG): HttpResponse<B, H> = do {
  var httpRequest =  createHttpRequest("TRACE", url, headers)
  ---
  sendRequestAndReadResponse(httpRequest, requestConfig, serializationConfig, clientConfig)
}

/**
* Helper function to send a `PATCH` HTTP request.
*
* === Parameters
*
* [%header, cols="1,1,3"]
* |===
* | Name | Type | Description
* | url | `String &#124; UrlBuilder` | The desired HTTP request url.
* | headers | `HttpHeaders` | The HTTP request header to send.
* | body | `HttpBody &#124; Null` | The HTTP request body to send.
* | requestConfig | `HttpRequestConfig` | The HTTP request configuration to use.
* | serializationConfig | `SerializationConfig` | The HTTP serialization configuration to use.
* | clientConfig | `HttpClientConfig` | The HTTP client configuration configuration to use.
* |===
*/
fun patch<B <: HttpBody, H <: HttpHeaders>(url: String | UrlBuilder,
  headers: HttpHeaders = {},
  body: HttpBody | Null = null,
  requestConfig: HttpRequestConfig = DEFAULT_HTTP_REQUEST_CONFIG,
  serializationConfig: SerializationConfig = DEFAULT_SERIALIZATION_CONFIG,
  clientConfig: HttpClientConfig = DEFAULT_HTTP_CLIENT_CONFIG): HttpResponse<B, H> = do {
  var httpRequest = createHttpRequest("PATCH", url, headers, body)
  ---
  sendRequestAndReadResponse(httpRequest, requestConfig, serializationConfig, clientConfig)
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
* | cookies | `HttpRequestCookies` | The HTTP request cookies to send.
* | body | `HttpBody &#124; Null` |  The HTTP request body to send.
* |===
*/
fun createHttpRequest<T <: HttpBody>(method: HttpMethod, url: String | UrlBuilder, headers: HttpHeaders = {}, body: T | Null = null, cookies: HttpRequestCookies = {}): HttpRequest<T> =
  if (body != null) {
    method: method,
    url: url,
    headers: headers,
    cookies: cookies,
    body: body,
  } else {
    method: method,
    url: url,
    headers: headers,
    cookies: cookies
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
    var contentTypesHeaders = allHeadersWith(headers, CONTENT_TYPE_HEADER)
    var requestContentType = (contentTypesHeaders[0].value as String) default serializationConfig.contentType
    var writerProperties = serializationConfig.writerProperties default {}
    var binaryBody = writeToBinary(request.body, requestContentType, writerProperties)
    var contentTypeHeader = {
      name: contentTypesHeaders[0].name default CONTENT_TYPE_HEADER,
      value: dw::module::Mime::toString(binaryBody.mime)
    }
    var headersWithContentType = headers withHeader contentTypeHeader
    ---
    request update {
      case .headers! -> headersWithContentType
      case .body -> binaryBody.body
    }
  } else {
    method: request.method,
    url: request.url,
    (headers: request.headers!) if (request.headers?),
    (cookies: request.cookies!) if (request.cookies?)
  }

/**
* Sends an HTTP request by using the provided `binaryRequestEncoder` to convert the current request into an `HttpRequest<Binary,H&#62;` instance
* with the desired configurations (request/client configuration) and returns an HTTP response using the provided `binaryResponseDecoder` to convert
* the current HTTP response into an `HttpResponse<B, H&#62;>` instance.
*
* === Parameters
*
* [%header, cols="1,1,3"]
* |===
* | Name | Type | Description
* | request | `HttpRequest` | An HTTP request to send.
* | binaryRequestEncoder | `BinaryRequestEncoder` | The HTTP request encoder to use.
* | binaryResponseDecoder | `BinaryResponseDecoder` | The HTTP response decoder to use.
* | requestConfig | `HttpRequestConfig` | The HTTP request configuration to use.
* | clientConfig | `HttpClientConfig` | The HTTP client configuration configuration to use.
* |===
*/
fun sendRequest<B <: HttpBody, H <: HttpHeaders>(request: HttpRequest,
  binaryRequestEncoder: BinaryRequestEncoder,
  binaryResponseDecoder: BinaryResponseDecoder<B, H>,
  requestConfig: HttpRequestConfig = DEFAULT_HTTP_REQUEST_CONFIG,
  clientConfig: HttpClientConfig = DEFAULT_HTTP_CLIENT_CONFIG): HttpResponse<B, H> = do {
    var binaryRequest = binaryRequestEncoder.encode(request)
    var httpResponse = sendRequest(binaryRequest, requestConfig, clientConfig)
    var response = binaryResponseDecoder.decode(httpResponse)
    ---
    response
  }

/**
* Sends an HttpRequest<Binary,H&#62; instance with the desired configurations (request/client configuration)
* and returns an HTTP response using the provided `binaryResponseDecoder` to convert the current HTTP response
* into an `HttpResponse<B, H&#62;>` instance.
*
* === Parameters
*
* [%header, cols="1,1,3"]
* |===
* | Name | Type | Description
* | request | `HttpRequest<Binary,H&#62;` | An HTTP request to send.
* | binaryResponseDecoder | `BinaryResponseDecoder` | The HTTP response decoder to use.
* | requestConfig | `HttpRequestConfig` | The HTTP request configuration to use.
* | clientConfig | `HttpClientConfig` | The HTTP client configuration configuration to use.
* |===
*/
fun sendRequest<B <: HttpBody, H <: HttpHeaders>(request: HttpRequest<Binary>,
  binaryResponseDecoder: BinaryResponseDecoder<B, H>,
  requestConfig: HttpRequestConfig = DEFAULT_HTTP_REQUEST_CONFIG,
  clientConfig: HttpClientConfig = DEFAULT_HTTP_CLIENT_CONFIG): HttpResponse<B, H> = do {
    var httpResponse = sendRequest(request, requestConfig, clientConfig)
    var response = binaryResponseDecoder.decode(httpResponse)
    ---
    response
  }


/**
* Sends an HTTP request by using the provided `binaryRequestEncoder` to convert the current request into an `HttpRequest<Binary,H&#62;` instance
* with the desired configurations (request/client configuration) and returns an `HttpResponse<Binary, H&#62;>` instance.
*
* === Parameters
*
* [%header, cols="1,1,3"]
* |===
* | Name | Type | Description
* | request | `HttpRequest` | An HTTP request to send.
* | binaryRequestEncoder | `BinaryRequestEncoder` | The HTTP request encoder to use.
* | requestConfig | `HttpRequestConfig` | The HTTP request configuration to use.
* | clientConfig | `HttpClientConfig` | The HTTP client configuration configuration to use.
* |===
*/
fun sendRequest<H <: HttpHeaders>(request: HttpRequest,
  binaryRequestEncoder: BinaryRequestEncoder,
  requestConfig: HttpRequestConfig = DEFAULT_HTTP_REQUEST_CONFIG,
  clientConfig: HttpClientConfig = DEFAULT_HTTP_CLIENT_CONFIG): HttpResponse<Binary, H> = do {
    var binaryRequest = binaryRequestEncoder.encode(request)
    var httpResponse = sendRequest(binaryRequest, requestConfig, clientConfig)
    ---
    httpResponse
}

/**
* Sends an HTTP request with a `Binary` body instance with the desired configurations (request/client configuration)
* and returns an `HttpResponse<Binary, H&#62;>` instance.
*
* === Parameters
*
* [%header, cols="1,1,3"]
* |===
* | Name | Type | Description
* | request | `HttpRequest<Binary,H&#62;` | An HTTP request to send.
* | requestConfig | `HttpRequestConfig` | The HTTP request configuration to use.
* | clientConfig | `HttpClientConfig` | The HTTP client configuration configuration to use.
* |===
*/
@RuntimePrivilege(requires = "http::Client")
fun sendRequest<H <: HttpHeaders>(
  request: HttpRequest<Binary>,
  requestConfig: HttpRequestConfig = DEFAULT_HTTP_REQUEST_CONFIG,
  clientConfig: HttpClientConfig = DEFAULT_HTTP_CLIENT_CONFIG): HttpResponse<Binary, H> = do {
    fun nativeSendRequest(request: HttpRequest<Binary>, requestConfig: HttpRequestConfig, clientConfig: HttpClientConfig): HttpResponse<Binary, H> = native("http::HttpRequestFunction")
    ---
    nativeSendRequest(request, requestConfig, clientConfig)
}

/**
* Sends an HTTP request by writing the request body into a `Binary` instance and reading
* the HTTP response `Binary` body instance.
*
* === Parameters
*
* [%header, cols="1,1,3"]
* |===
* | Name | Type | Description
* | request | `HttpRequest` | An HTTP request to send.
* | requestConfig | `HttpRequestConfig` | The HTTP request configuration to use.
* | serializationConfig | `SerializationConfig` | The HTTP serialization configuration to use.
* | clientConfig | `HttpClientConfig` | The HTTP client configuration configuration to use.
* |===
*/
fun sendRequestAndReadResponse<B <: HttpBody, H <: HttpHeaders>(
  request: HttpRequest,
  requestConfig: HttpRequestConfig = DEFAULT_HTTP_REQUEST_CONFIG,
  serializationConfig: SerializationConfig = DEFAULT_SERIALIZATION_CONFIG,
  clientConfig: HttpClientConfig = DEFAULT_HTTP_CLIENT_CONFIG): HttpResponse<B, H> = do {
  var binaryRequestEncoder = { encode: (httpRequest) -> createBinaryHttpRequest(httpRequest, serializationConfig) }
  var binaryResponseDecoder = { decode: (httpResponse) -> readHttpResponseBody(httpResponse, serializationConfig) }
  ---
  sendRequest(request, binaryRequestEncoder, binaryResponseDecoder,requestConfig, clientConfig)
}

/**
* Helper function to read a `HttpResponse` with a `Binary` body instance.
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
fun readHttpResponseBody<B <: HttpBody, H <: HttpHeaders>(httpResponse: HttpResponse<Binary, H>, serializationConfig: SerializationConfig): HttpResponse<B, H> = do {
  var responseBody = httpResponse.body
  var schema = httpResponse.^
  ---
  if (responseBody == null)
    (httpResponse as HttpResponse<B, H>) <~ schema
  else do {
    var contentType = httpResponse.contentType
    var httpResponseWithBody = httpResponse update {
      case .body -> do {
        if (contentType != null) do {
          var readerProperties = serializationConfig.readerProperties default {}
          ---
          readBody(contentType, responseBody, readerProperties)
        }
        else
          responseBody <~ { "mimeType": null, "raw": responseBody }
      }
    }
    ---
    (httpResponseWithBody as HttpResponse<B, H>) <~ schema
  }
}

/**
* Helper function to read a `Binary` body instance.
*
* === Parameters
*
* [%header, cols="1,1,3"]
* |===
* | Name | Type | Description
* | mimeType | `String` | The MIME type to use.
* | body | `Binary` | The desired body parse.
* | readerProperties | `Object` | The reader configuration properties used for read the body.
* |===
*/
fun readBody<B <: HttpBody>(mimeType: String, body: Binary, readerProperties: Object = {}): B = native("http::ReadBodyFunction")

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
    kind match {
        case is OAuth -> { Authorization: "Bearer $($.token)"}
        case is BasicAuth -> do {
          var base = toBase64("$($.username):$($.password)" as Binary {encoding: "UTF-8"})
            ---
            { Authorization: "Basic $(base)"}
        }
    }
}
