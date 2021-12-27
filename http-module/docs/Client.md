#### _dw::io::http::Client_
__________________________________________

Http client module allows to make http calls

# Index

### Functions
| Name | Description|
|------|------------|
| [CONNECT](#connect-index ) | Does a `CONNECT` call the the specified URL|
| [DELETE](#delete-index ) | Does a `DELETE` call the the specified URL|
| [GET](#get-index ) | Does a `GET` call the the specified URL|
| [HEAD](#head-index ) | Does a `HEAD` call the the specified URL|
| [OPTIONS](#options-index ) | Does a `OPTIONS` call the the specified URL|
| [PATCH](#patch-index ) | Does a `PATCH` call the the specified URL|
| [POST](#post-index ) | Does a `POST` call the the specified URL|
| [POSTMultipart](#postmultipart-index ) | |
| [PUT](#put-index ) | Does a `PUT` call the the specified URL|
| [TRACE](#trace-index ) | Does a `TRACE` call the the specified URL|
| [createAuthorizationHeader](#createauthorizationheader-index ) | Utility function that adds the proper Authorization header based on the supported Auth type.|
| [request](#request-index ) | This is a generic http call|
| [resolveTemplateWith](#resolvetemplatewith-index ) | Replace the templates of a url according to RFC6570|
| [url](#url-index ) | String interpolator function to build a URL|




### Types
| Name | Description|
|------|------------|
|[BasicAuth](#basicauth-index ) | |
|[HttpClientResponse](#httpclientresponse-index ) | |
|[HttpRequest](#httprequest-index ) | |
|[OAuth](#oauth-index ) | |
|[UrlBuilder](#urlbuilder-index ) | |







__________________________________________


# Functions

## **CONNECT** [↑↑](#index )

### _CONNECT(url: String | UrlBuilder, httpRequest: HttpRequest = {}): HttpClientResponse_

Does a `CONNECT` call the the specified URL

##### Parameters

| Name | Type | Description|
|------|------|------------|
| url | String &#124; UrlBuilder | The url to be called|
| httpRequest | HttpRequest | The request configuration information|


##### Example

This example shows how the `GET` behaves under different inputs.

###### Source

```dataweave
%dw 2.0
output application/json
---
CONNECT("http://google.com")

```
__________________________________________


## **DELETE** [↑↑](#index )

### _DELETE(url: String | UrlBuilder, httpRequest: HttpRequest = {}): HttpClientResponse_

Does a `DELETE` call the the specified URL

##### Parameters

| Name | Type | Description|
|------|------|------------|
| url | String &#124; UrlBuilder | The url to be called|
| httpRequest | HttpRequest | The request configuration information|


##### Example

This example shows how the `GET` behaves under different inputs.

###### Source

```dataweave
%dw 2.0
output application/json
---
DELETE("http://google.com")

```
__________________________________________


## **GET** [↑↑](#index )

### _GET(url: String | UrlBuilder, httpRequest: HttpRequest = {}): HttpClientResponse_

Does a `GET` call the the specified URL

##### Parameters

| Name | Type | Description|
|------|------|------------|
| url | String &#124; UrlBuilder | The url to be called|
| httpRequest | HttpRequest | The request configuration information|


##### Example

This example shows how the `GET` behaves under different inputs.

###### Source

```dataweave
%dw 2.0
output application/json
---
GET("http://google.com")

```
__________________________________________


## **HEAD** [↑↑](#index )

### _HEAD(url: String | UrlBuilder, httpRequest: HttpRequest = {}): HttpClientResponse_

Does a `HEAD` call the the specified URL

##### Parameters

| Name | Type | Description|
|------|------|------------|
| url | String &#124; UrlBuilder | The url to be called|
| httpRequest | HttpRequest | The request configuration information|


##### Example

This example shows how the `GET` behaves under different inputs.

###### Source

```dataweave
%dw 2.0
output application/json
---
HEAD("http://google.com")

```
__________________________________________


## **OPTIONS** [↑↑](#index )

### _OPTIONS(url: String | UrlBuilder, httpRequest: HttpRequest = {}): HttpClientResponse_

Does a `OPTIONS` call the the specified URL

##### Parameters

| Name | Type | Description|
|------|------|------------|
| url | String &#124; UrlBuilder | The url to be called|
| httpRequest | HttpRequest | The request configuration information|


##### Example

This example shows how the `GET` behaves under different inputs.

###### Source

```dataweave
%dw 2.0
output application/json
---
OPTIONS("http://google.com")

```
__________________________________________


## **PATCH** [↑↑](#index )

### _PATCH(url: String | UrlBuilder, httpRequest: HttpRequest = {}): HttpClientResponse_

Does a `PATCH` call the the specified URL

##### Parameters

| Name | Type | Description|
|------|------|------------|
| url | String &#124; UrlBuilder | The url to be called|
| httpRequest | HttpRequest | The request configuration information|


##### Example

This example shows how the `GET` behaves under different inputs.

###### Source

```dataweave
%dw 2.0
output application/json
---
PATCH("http://google.com")

```
__________________________________________


## **POST** [↑↑](#index )

### _POST(url: String | UrlBuilder, httpRequest: HttpRequest = {}): HttpClientResponse_

Does a `POST` call the the specified URL

##### Parameters

| Name | Type | Description|
|------|------|------------|
| url | String &#124; UrlBuilder | The url to be called|
| httpRequest | HttpRequest | The request configuration information|


##### Example

This example shows how the `GET` behaves under different inputs.

###### Source

```dataweave
%dw 2.0
output application/json
---
POST("http://google.com")

```
__________________________________________


## **POSTMultipart** [↑↑](#index )

### _POSTMultipart(url: String | UrlBuilder, httpRequest: HttpRequest<Multipart> = {}): HttpClientResponse_


__________________________________________


## **PUT** [↑↑](#index )

### _PUT(url: String | UrlBuilder, httpRequest: HttpRequest = {}): HttpClientResponse_

Does a `PUT` call the the specified URL

##### Parameters

| Name | Type | Description|
|------|------|------------|
| url | String &#124; UrlBuilder | The url to be called|
| httpRequest | HttpRequest | The request configuration information|


##### Example

This example shows how the `GET` behaves under different inputs.

###### Source

```dataweave
%dw 2.0
output application/json
---
PUT("http://google.com")

```
__________________________________________


## **TRACE** [↑↑](#index )

### _TRACE(url: String | UrlBuilder, httpRequest: HttpRequest = {}): HttpClientResponse_

Does a `TRACE` call the the specified URL

##### Parameters

| Name | Type | Description|
|------|------|------------|
| url | String &#124; UrlBuilder | The url to be called|
| httpRequest | HttpRequest | The request configuration information|


##### Example

This example shows how the `GET` behaves under different inputs.

###### Source

```dataweave
%dw 2.0
output application/json
---
TRACE("http://google.com")

```
__________________________________________


## **createAuthorizationHeader** [↑↑](#index )

### _createAuthorizationHeader(kind: OAuth | BasicAuth): {| Authorization: String |}_

Utility function that adds the proper Authorization header based on the supported Auth type.
__________________________________________


## **request** [↑↑](#index )

### _request(method: HttpMethod, url: String | UrlBuilder, httpRequest: HttpRequest = {}): HttpClientResponse_

This is a generic http call

##### Parameters

| Name | Type | Description|
|------|------|------------|
| method | HttpMethod |  The method name i.e "GET"|
| url | String &#124; UrlBuilder |  The url to be called|
| httpRequest | HttpRequest | The request config|

__________________________________________


## **resolveTemplateWith** [↑↑](#index )

### _resolveTemplateWith(uri: String, context: Object): String_

Replace the templates of a url according to RFC6570
__________________________________________


## **url** [↑↑](#index )

### _url(parts: Array<String>, interpolation: Array<StringCoerceable>): String_

String interpolator function to build a URL
__________________________________________




__________________________________________

# Types

### **BasicAuth** [↑↑](#index )




#### Definition

```dataweave
{ username: String, password: String }
```


### **HttpClientResponse** [↑↑](#index )




#### Definition

```dataweave
{ contentType: String, status: Number, statusText?: String, headers: HeadersType, body?: BodyType, cookies?: HttpCookies }
```


### **HttpRequest** [↑↑](#index )




#### Definition

```dataweave
{ headers?: HttpHeaders, body?: T, // Config properties
config?: { defaultContentType?: String, followRedirects?: Boolean, readerProperties?: Object, writerProperties?: Object, readTimeout?: Number, requestTimeout?: Number } }
```


### **OAuth** [↑↑](#index )




#### Definition

```dataweave
{ token: String }
```


### **UrlBuilder** [↑↑](#index )




#### Definition

```dataweave
{ url: String, queryParams?: QueryParams }
```




