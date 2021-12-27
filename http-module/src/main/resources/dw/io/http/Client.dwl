/**
* Http client module allows to make http calls
*/
%dw 2.0

import dw::core::Objects
import * from dw::io::http::Types
import * from dw::io::http::BodyUtils
import * from dw::core::Binaries
import * from dw::core::URL
import * from dw::module::Multipart
import * from dw::io::http::utils::HttpHeaders

type HttpClientResponse<BodyType <: HttpBody, HeadersType <: HttpHeaders> = {
    contentType: String,
    status: Number,
    statusText?: String,
    headers: HeadersType,
    body?: BodyType,
    cookies?: HttpCookies
}


type HttpRequest<T <: HttpBody> = {
    headers?: HttpHeaders,
    body?: T,
    //Config properties
    config?: {
        defaultContentType?: String,
        followRedirects?: Boolean,
        readerProperties?: Object,
        writerProperties?: Object,
        readTimeout?: Number,
        requestTimeout?: Number

    }
}

type UrlBuilder = {
   url: String,
   queryParams?: QueryParams
}

type OAuth = {token: String}

type BasicAuth = {username: String, password: String}

/**
* Does a `GET` call the the specified URL
*
* === Parameters
*
* [%header, cols="1,1,3"]
* |===
* | Name | Type | Description
* | url | String &#124; UrlBuilder | The url to be called
* | httpRequest | HttpRequest | The request configuration information
* |===
*
* === Example
*
* This example shows how the `GET` behaves under different inputs.
*
* ==== Source
*
* [source,DataWeave,linenums]
* ----
* %dw 2.0
* output application/json
* ---
* GET("http://google.com")
*
* ----
*
**/
fun GET(url: String | UrlBuilder, httpRequest: HttpRequest = {}): HttpClientResponse =
  request("GET", url, httpRequest)


/**
* Does a `POST` call the the specified URL
*
* === Parameters
*
* [%header, cols="1,1,3"]
* |===
* | Name | Type | Description
* | url | String &#124; UrlBuilder | The url to be called
* | httpRequest | HttpRequest | The request configuration information
* |===
*
* === Example
*
* This example shows how the `GET` behaves under different inputs.
*
* ==== Source
*
* [source,DataWeave,linenums]
* ----
* %dw 2.0
* output application/json
* ---
* POST("http://google.com")
*
* ----
*
**/
fun POST(url: String | UrlBuilder, httpRequest: HttpRequest = {}): HttpClientResponse =
  request("POST", url, httpRequest)

fun POSTMultipart(url: String | UrlBuilder, httpRequest: HttpRequest<Multipart> = {}): HttpClientResponse = do {
    var newRequest = if(httpRequest.headers[CONTENT_TYPE_HEADER]?)
                      httpRequest
                     else
                      httpRequest update {
                        case .headers.CONTENT_TYPE_HEADER! -> "multipart/form-data"
                      }
    ---
    request("POST", url, httpRequest)
}


/**
* Does a `HEAD` call the the specified URL
*
* === Parameters
*
* [%header, cols="1,1,3"]
* |===
* | Name | Type | Description
* | url | String &#124; UrlBuilder | The url to be called
* | httpRequest | HttpRequest | The request configuration information
* |===
*
* === Example
*
* This example shows how the `GET` behaves under different inputs.
*
* ==== Source
*
* [source,DataWeave,linenums]
* ----
* %dw 2.0
* output application/json
* ---
* HEAD("http://google.com")
*
* ----
*
**/
fun HEAD(url: String | UrlBuilder, httpRequest: HttpRequest = {}): HttpClientResponse =
  request("HEAD", url, httpRequest)



/**
* Does a `PUT` call the the specified URL
*
* === Parameters
*
* [%header, cols="1,1,3"]
* |===
* | Name | Type | Description
* | url | String &#124; UrlBuilder | The url to be called
* | httpRequest | HttpRequest | The request configuration information
* |===
*
* === Example
*
* This example shows how the `GET` behaves under different inputs.
*
* ==== Source
*
* [source,DataWeave,linenums]
* ----
* %dw 2.0
* output application/json
* ---
* PUT("http://google.com")
*
* ----
*
**/
fun PUT(url: String | UrlBuilder, httpRequest: HttpRequest = {}): HttpClientResponse =
  request("PUT", url, httpRequest)



/**
* Does a `DELETE` call the the specified URL
*
* === Parameters
*
* [%header, cols="1,1,3"]
* |===
* | Name | Type | Description
* | url | String &#124; UrlBuilder | The url to be called
* | httpRequest | HttpRequest | The request configuration information
* |===
*
* === Example
*
* This example shows how the `GET` behaves under different inputs.
*
* ==== Source
*
* [source,DataWeave,linenums]
* ----
* %dw 2.0
* output application/json
* ---
* DELETE("http://google.com")
*
* ----
*
**/
fun DELETE(url: String | UrlBuilder, httpRequest: HttpRequest = {}): HttpClientResponse =
  request("DELETE", url, httpRequest)



/**
* Does a `CONNECT` call the the specified URL
*
* === Parameters
*
* [%header, cols="1,1,3"]
* |===
* | Name | Type | Description
* | url | String &#124; UrlBuilder | The url to be called
* | httpRequest | HttpRequest | The request configuration information
* |===
*
* === Example
*
* This example shows how the `GET` behaves under different inputs.
*
* ==== Source
*
* [source,DataWeave,linenums]
* ----
* %dw 2.0
* output application/json
* ---
* CONNECT("http://google.com")
*
* ----
*
**/
fun CONNECT(url: String | UrlBuilder, httpRequest: HttpRequest = {}): HttpClientResponse =
  request("CONNECT", url, httpRequest)



/**
* Does a `OPTIONS` call the the specified URL
*
* === Parameters
*
* [%header, cols="1,1,3"]
* |===
* | Name | Type | Description
* | url | String &#124; UrlBuilder | The url to be called
* | httpRequest | HttpRequest | The request configuration information
* |===
*
* === Example
*
* This example shows how the `GET` behaves under different inputs.
*
* ==== Source
*
* [source,DataWeave,linenums]
* ----
* %dw 2.0
* output application/json
* ---
* OPTIONS("http://google.com")
*
* ----
*
**/
fun OPTIONS(url: String | UrlBuilder, httpRequest: HttpRequest = {}): HttpClientResponse =
  request("OPTIONS", url, httpRequest)



/**
* Does a `TRACE` call the the specified URL
*
* === Parameters
*
* [%header, cols="1,1,3"]
* |===
* | Name | Type | Description
* | url | String &#124; UrlBuilder | The url to be called
* | httpRequest | HttpRequest | The request configuration information
* |===
*
* === Example
*
* This example shows how the `GET` behaves under different inputs.
*
* ==== Source
*
* [source,DataWeave,linenums]
* ----
* %dw 2.0
* output application/json
* ---
* TRACE("http://google.com")
*
* ----
*
**/
fun TRACE(url: String | UrlBuilder, httpRequest: HttpRequest = {}): HttpClientResponse =
  request("TRACE", url, httpRequest)



/**
* Does a `PATCH` call the the specified URL
*
* === Parameters
*
* [%header, cols="1,1,3"]
* |===
* | Name | Type | Description
* | url | String &#124; UrlBuilder | The url to be called
* | httpRequest | HttpRequest | The request configuration information
* |===
*
* === Example
*
* This example shows how the `GET` behaves under different inputs.
*
* ==== Source
*
* [source,DataWeave,linenums]
* ----
* %dw 2.0
* output application/json
* ---
* PATCH("http://google.com")
*
* ----
*
**/
fun PATCH(url: String | UrlBuilder, httpRequest: HttpRequest = {}): HttpClientResponse =
  request("PATCH", url, httpRequest)


/**
* This is a generic http call
*
* === Parameters
*
* [%header, cols="1,1,3"]
* |===
* | Name | Type | Description
* | method | HttpMethod |  The method name i.e "GET"
* | url | String &#124; UrlBuilder |  The url to be called
* | httpRequest | HttpRequest | The request config
* |===
*
**/
@RuntimePrivilege(requires = "http.Client")
fun request(method: HttpMethod, url: String | UrlBuilder, httpRequest: HttpRequest = {}): HttpClientResponse = native("http::HttpRequestFunction")

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