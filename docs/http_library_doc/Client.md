#### _dw::io::http::Client_
__________________________________________

Http client module allows to make http calls

# Index

### Functions
| Name | Description|
|------|------------|
| [CONNECT](#connect-index ) | |
| [DELETE](#delete-index ) | |
| [GET](#get-index ) | |
| [HEAD](#head-index ) | |
| [OPTIONS](#options-index ) | |
| [PATCH](#patch-index ) | |
| [POST](#post-index ) | |
| [POSTMultipart](#postmultipart-index ) | |
| [PUT](#put-index ) | |
| [TRACE](#trace-index ) | |
| [createAuthorizationHeader](#createauthorizationheader-index ) | Utility function that adds the proper Authorization header based on the supported Auth type.|
| [request](#request-index ) | |
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


__________________________________________


## **DELETE** [↑↑](#index )

### _DELETE(url: String | UrlBuilder, httpRequest: HttpRequest = {}): HttpClientResponse_


__________________________________________


## **GET** [↑↑](#index )

### _GET(url: String | UrlBuilder, httpRequest: HttpRequest = {}): HttpClientResponse_


__________________________________________


## **HEAD** [↑↑](#index )

### _HEAD(url: String | UrlBuilder, httpRequest: HttpRequest = {}): HttpClientResponse_


__________________________________________


## **OPTIONS** [↑↑](#index )

### _OPTIONS(url: String | UrlBuilder, httpRequest: HttpRequest = {}): HttpClientResponse_


__________________________________________


## **PATCH** [↑↑](#index )

### _PATCH(url: String | UrlBuilder, httpRequest: HttpRequest = {}): HttpClientResponse_


__________________________________________


## **POST** [↑↑](#index )

### _POST(url: String | UrlBuilder, httpRequest: HttpRequest = {}): HttpClientResponse_


__________________________________________


## **POSTMultipart** [↑↑](#index )

### _POSTMultipart(url: String | UrlBuilder, httpRequest: HttpRequest<Multipart> = {}): HttpClientResponse_


__________________________________________


## **PUT** [↑↑](#index )

### _PUT(url: String | UrlBuilder, httpRequest: HttpRequest = {}): HttpClientResponse_


__________________________________________


## **TRACE** [↑↑](#index )

### _TRACE(url: String | UrlBuilder, httpRequest: HttpRequest = {}): HttpClientResponse_


__________________________________________


## **createAuthorizationHeader** [↑↑](#index )

### _createAuthorizationHeader(kind: OAuth | BasicAuth): {| Authorization: String |}_

Utility function that adds the proper Authorization header based on the supported Auth type.
__________________________________________


## **request** [↑↑](#index )

### _request(method: HttpMethod, url: String | UrlBuilder, httpRequest: HttpRequest = {}): HttpClientResponse_


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




