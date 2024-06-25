#### _dw::io::http::Client_
__________________________________________

HTTP client module allows to make HTTP calls

To use this module, you must import it to your DataWeave code, for example,
by adding the line `import * from dw::io::http::Client` to the header of your
DataWeave script.

# Index

### Functions
| Name | Description|
|------|------------|
| [connect](#connect ) | |
| [createAuthorizationHeader](#createauthorizationheader ) | Utility function that adds the proper Authorization header based on the supported Auth type.|
| [createBinaryHttpRequest](#createbinaryhttprequest ) | Helper function to create a `HttpRequest` instances with `Binary` request body.|
| [createHttpRequest](#createhttprequest ) | Helper function to create an `HttpRequest` instance.|
| [delete](#delete ) | |
| [get](#get ) | |
| [head](#head ) | |
| [options](#options ) | |
| [patch](#patch ) | |
| [post](#post ) | |
| [postMultipart](#postmultipart ) | |
| [put](#put ) | |
| [readBody](#readbody ) | Helper function to read a `Binary` body instance.|
| [readHttpResponseBody](#readhttpresponsebody ) | Helper function to read a `HttpResponse` with a `Binary` body instance.|
| [resolveTemplateWith](#resolvetemplatewith ) | Replace the templates of a url according to RFC6570|
| [sendRequest](#sendrequest ) | |
| [sendRequestAndReadResponse](#sendrequestandreadresponse ) | |
| [trace](#trace ) | |
| [url](#url ) | String interpolator function to build a URL|


### Variables
| Name | Description|
|------|------------|
| [DEFAULT_HTTP_CLIENT_CONFIG](#default_http_client_config ) | Variable used to identify the default HTTP client configuration.|
| [DEFAULT_HTTP_REQUEST_CONFIG](#default_http_request_config ) | Variable used to identify the default HTTP request configuration.|
| [DEFAULT_SERIALIZATION_CONFIG](#default_serialization_config ) | Variable used to identify the default HTTP serialization configuration.|
| [OCTET_STREAM_MIME_TYPE](#octet_stream_mime_type ) | |
| [X_BINARY_MIME_TYPE](#x_binary_mime_type ) | |







__________________________________________


# Functions

## **connect**

### _connect<B <: HttpBody, H <: HttpHeaders&#62;&#40;url: String &#124; UrlBuilder, headers: HttpHeaders = {}, requestConfig: HttpRequestConfig = DEFAULT_HTTP_REQUEST_CONFIG, serializationConfig: SerializationConfig = DEFAULT_SERIALIZATION_CONFIG, clientConfig: HttpClientConfig = DEFAULT_HTTP_CLIENT_CONFIG&#41;: HttpResponse<B, H&#62;_


__________________________________________


## **createAuthorizationHeader**

### _createAuthorizationHeader&#40;kind: OAuth &#124; BasicAuth&#41;: {&#124; Authorization: String &#124;}_

Utility function that adds the proper Authorization header based on the supported Auth type.
__________________________________________


## **createBinaryHttpRequest**

### _createBinaryHttpRequest&#40;request: HttpRequest, serializationConfig: SerializationConfig&#41;: HttpRequest<Binary&#62;_

Helper function to create a `HttpRequest` instances with `Binary` request body.

##### Parameters

| Name | Type | Description|
|------|------|------------|
| request | `HttpRequest` | The desired HTTP request to convert to a `HttpRequest` instances with `Binary` request body.|
| serializationConfig | `SerializationConfig` | The HTTP serialization configuration to use.|

__________________________________________


## **createHttpRequest**

### _createHttpRequest<T <: HttpBody&#62;&#40;method: HttpMethod, url: String &#124; UrlBuilder, headers: HttpHeaders = {}, body: T &#124; Null = null&#41;: HttpRequest<T&#62;_

Helper function to create an `HttpRequest` instance.

##### Parameters

| Name | Type | Description|
|------|------|------------|
| method | `HttpMethod` | The desired HTTP request method.|
| url | `String &#124; UrlBuilder` | The desired HTTP request url.|
| headers | `HttpHeaders` | The HTTP request header to send.|
| body | `HttpBody &#124; Null` |  The HTTP request body to send.|

__________________________________________


## **delete**

### _delete<B <: HttpBody, H <: HttpHeaders&#62;&#40;url: String &#124; UrlBuilder, headers: HttpHeaders = {}, body: HttpBody &#124; Null = null, requestConfig: HttpRequestConfig = DEFAULT_HTTP_REQUEST_CONFIG, serializationConfig: SerializationConfig = DEFAULT_SERIALIZATION_CONFIG, clientConfig: HttpClientConfig = DEFAULT_HTTP_CLIENT_CONFIG&#41;: HttpResponse<B, H&#62;_


__________________________________________


## **get**

### _get<B <: HttpBody, H <: HttpHeaders&#62;&#40;url: String &#124; UrlBuilder, headers: HttpHeaders = {}, requestConfig: HttpRequestConfig = DEFAULT_HTTP_REQUEST_CONFIG, serializationConfig: SerializationConfig = DEFAULT_SERIALIZATION_CONFIG, clientConfig: HttpClientConfig = DEFAULT_HTTP_CLIENT_CONFIG&#41;: HttpResponse<B, H&#62;_


__________________________________________


## **head**

### _head<B <: HttpBody, H <: HttpHeaders&#62;&#40;url: String &#124; UrlBuilder, headers: HttpHeaders = {}, requestConfig: HttpRequestConfig = DEFAULT_HTTP_REQUEST_CONFIG, serializationConfig: SerializationConfig = DEFAULT_SERIALIZATION_CONFIG, clientConfig: HttpClientConfig = DEFAULT_HTTP_CLIENT_CONFIG&#41;: HttpResponse<B, H&#62;_


__________________________________________


## **options**

### _options<B <: HttpBody, H <: HttpHeaders&#62;&#40;url: String &#124; UrlBuilder, headers: HttpHeaders = {}, requestConfig: HttpRequestConfig = DEFAULT_HTTP_REQUEST_CONFIG, serializationConfig: SerializationConfig = DEFAULT_SERIALIZATION_CONFIG, clientConfig: HttpClientConfig = DEFAULT_HTTP_CLIENT_CONFIG&#41;: HttpResponse<B, H&#62;_


__________________________________________


## **patch**

### _patch<B <: HttpBody, H <: HttpHeaders&#62;&#40;url: String &#124; UrlBuilder, headers: HttpHeaders = {}, body: HttpBody &#124; Null = null, requestConfig: HttpRequestConfig = DEFAULT_HTTP_REQUEST_CONFIG, serializationConfig: SerializationConfig = DEFAULT_SERIALIZATION_CONFIG, clientConfig: HttpClientConfig = DEFAULT_HTTP_CLIENT_CONFIG&#41;: HttpResponse<B, H&#62;_


__________________________________________


## **post**

### _post<B <: HttpBody, H <: HttpHeaders&#62;&#40;url: String &#124; UrlBuilder, headers: HttpHeaders = {}, body: HttpBody &#124; Null = null, requestConfig: HttpRequestConfig = DEFAULT_HTTP_REQUEST_CONFIG, serializationConfig: SerializationConfig = DEFAULT_SERIALIZATION_CONFIG, clientConfig: HttpClientConfig = DEFAULT_HTTP_CLIENT_CONFIG&#41;: HttpResponse<B, H&#62;_


__________________________________________


## **postMultipart**

### _postMultipart<B <: HttpBody, H <: HttpHeaders&#62;&#40;url: String &#124; UrlBuilder, body: Multipart, headers: HttpHeaders = {}, requestConfig: HttpRequestConfig = DEFAULT_HTTP_REQUEST_CONFIG, serializationConfig: SerializationConfig = DEFAULT_SERIALIZATION_CONFIG, clientConfig: HttpClientConfig = DEFAULT_HTTP_CLIENT_CONFIG&#41;: HttpResponse<B, H&#62;_


__________________________________________


## **put**

### _put<B <: HttpBody, H <: HttpHeaders&#62;&#40;url: String &#124; UrlBuilder, headers: HttpHeaders = {}, body: HttpBody &#124; Null = null, requestConfig: HttpRequestConfig = DEFAULT_HTTP_REQUEST_CONFIG, serializationConfig: SerializationConfig = DEFAULT_SERIALIZATION_CONFIG, clientConfig: HttpClientConfig = DEFAULT_HTTP_CLIENT_CONFIG&#41;: HttpResponse<B, H&#62;_


__________________________________________


## **readBody**

### _readBody<B <: HttpBody&#62;&#40;mimeType: String, body: Binary, serializationConfig: SerializationConfig&#41;: B_

Helper function to read a `Binary` body instance.

##### Parameters

| Name | Type | Description|
|------|------|------------|
| mimeType | `String` | The MIME type to use.|
| body | `Binary` | The desired body parse.|
| serializationConfig | `SerializationConfig` | The HTTP serialization configuration to use.|

__________________________________________


## **readHttpResponseBody**

### _readHttpResponseBody<B <: HttpBody, H <: HttpHeaders&#62;&#40;httpResponse: HttpResponse<Binary, H&#62;, serializationConfig: SerializationConfig&#41;: HttpResponse<B, H&#62;_

Helper function to read a `HttpResponse` with a `Binary` body instance.

##### Parameters

| Name | Type | Description|
|------|------|------------|
| httpResponse | `HttpResponse<Binary,H&#62;` | The desired `HttpResponse` to parse.|
| serializationConfig | `SerializationConfig` | The HTTP serialization configuration to use.|

__________________________________________


## **resolveTemplateWith**

### _resolveTemplateWith&#40;uri: String, context: Object&#41;: String_

Replace the templates of a url according to RFC6570
__________________________________________


## **sendRequest**

### _sendRequest<H <: HttpHeaders&#62;&#40;request: HttpRequest<Binary&#62;, requestConfig: HttpRequestConfig = DEFAULT_HTTP_REQUEST_CONFIG, clientConfig: HttpClientConfig = DEFAULT_HTTP_CLIENT_CONFIG&#41;: HttpResponse<Binary, H&#62;_


__________________________________________


## **sendRequestAndReadResponse**

### _sendRequestAndReadResponse<B <: HttpBody, H <: HttpHeaders&#62;&#40;request: HttpRequest, requestConfig: HttpRequestConfig = DEFAULT_HTTP_REQUEST_CONFIG, serializationConfig: SerializationConfig = DEFAULT_SERIALIZATION_CONFIG, clientConfig: HttpClientConfig = DEFAULT_HTTP_CLIENT_CONFIG&#41;: HttpResponse<B, H&#62;_


__________________________________________


## **trace**

### _trace<B <: HttpBody, H <: HttpHeaders&#62;&#40;url: String &#124; UrlBuilder, headers: HttpHeaders = {}, requestConfig: HttpRequestConfig = DEFAULT_HTTP_REQUEST_CONFIG, serializationConfig: SerializationConfig = DEFAULT_SERIALIZATION_CONFIG, clientConfig: HttpClientConfig = DEFAULT_HTTP_CLIENT_CONFIG&#41;: HttpResponse<B, H&#62;_


__________________________________________


## **url**

### _url&#40;parts: Array<String&#62;, interpolation: Array<StringCoerceable&#62;&#41;: String_

String interpolator function to build a URL
__________________________________________




# Variables

## **DEFAULT_HTTP_CLIENT_CONFIG**


Variable used to identify the default HTTP client configuration.

## **DEFAULT_HTTP_REQUEST_CONFIG**


Variable used to identify the default HTTP request configuration.

## **DEFAULT_SERIALIZATION_CONFIG**


Variable used to identify the default HTTP serialization configuration.

## **OCTET_STREAM_MIME_TYPE**




## **X_BINARY_MIME_TYPE**







