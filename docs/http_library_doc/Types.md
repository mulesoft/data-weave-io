#### _dw::io::http::Types_
__________________________________________

Module with a set of `types` used to make HTTP calls

To use this module, you must import it to your DataWeave code, for example,
by adding the line `import * from dw::io::http::Types` to the header of your
DataWeave script.

# Index




### Types
| Name | Description|
|------|------------|
|[BasicAuth](#basicauth ) | DataWeave type for representing an basic Authorization.|
|[HttpBody](#httpbody ) | DataWeave type for representing a HTTP body.|
|[HttpClientConfig](#httpclientconfig ) | DataWeave type for representing an HTTP client configuration.<br>Supports the following fields:|
|[HttpHeaders](#httpheaders ) | DataWeave type for representing a HTTP headers.|
|[HttpMethod](#httpmethod ) | DataWeave type for representing a HTTP request methods.|
|[HttpRequest](#httprequest ) | DataWeave type for representing an HTTP request.<br>Supports the following fields:|
|[HttpRequestConfig](#httprequestconfig ) | DataWeave type for representing an HTTP request configuration.<br>Supports the following fields:|
|[HttpRequestCookies](#httprequestcookies ) | DataWeave type for representing a HTTP request cookies.|
|[HttpResponse](#httpresponse ) | DataWeave type for representing an HTTP response.<br>Supports the following fields:|
|[HttpResponseCookie](#httpresponsecookie ) | DataWeave type for representing an HTTP response cookies.<br>Supports the following fields:|
|[HttpResponseCookies](#httpresponsecookies ) | DataWeave type for representing HTTP response cookies.|
|[OAuth](#oauth ) | DataWeave type for representing an OAuth.|
|[QueryParams](#queryparams ) | DataWeave type for representing a HTTP request query parameters.|
|[SerializationConfig](#serializationconfig ) | DataWeave type for representing an HTTP request configuration.<br>Supports the following fields:|
|[UrlBuilder](#urlbuilder ) | DataWeave type for representing an HTTP request URL.<br>Supports the following fields:|






__________________________________________





__________________________________________

# Types

### **BasicAuth**


DataWeave type for representing an basic Authorization.

#### Definition

```dataweave
{ username: String, password: String }
```


### **HttpBody**


DataWeave type for representing a HTTP body.

#### Definition

```dataweave
Any
```


### **HttpClientConfig**


DataWeave type for representing an HTTP client configuration.
Supports the following fields:

- `connectionTimeout`: The maximum time in millisecond an `HttpClient` can wait when connecting to a remote host (default: 5000).
- `compressionHeader`: If `true` the `Accept-Encoding: gzip, deflate` HTTP header will be sent to each request (default: false).
- `decompress`: Defines whether responses should be decompressed automatically (default: true).
- `tls`: The TLS context configuration.
- * `insecure`: Defines whether the trust store should be insecure, meaning no certificate validations should be performed (default: false)

#### Definition

```dataweave
{ connectionTimeout?: Number, compressionHeader?: Boolean, decompress?: Boolean, tls?: { insecure?: Boolean } }
```


### **HttpHeaders**


DataWeave type for representing a HTTP headers.

#### Definition

```dataweave
{ _*?: SimpleType }
```


### **HttpMethod**


DataWeave type for representing a HTTP request methods.

#### Definition

```dataweave
"GET" | "HEAD" | "POST" | "PUT" | "DELETE" | "CONNECT" | "OPTIONS" | "TRACE" | "PATCH"
```


### **HttpRequest**


DataWeave type for representing an HTTP request.
Supports the following fields:

- `method`: The HTTP request method.
- `url`: The HTTP request url.
- `headers`: The HTTP request header.
- `cookies`: The HTTP request cookies.
- `body`: The HTTP request body.

#### Definition

```dataweave
{ method: HttpMethod, url: String | UrlBuilder, headers?: HttpHeaders, cookies?: HttpRequestCookies, body?: T }
```


### **HttpRequestConfig**


DataWeave type for representing an HTTP request configuration.
Supports the following fields:

- `followRedirects`: Indicates if the HTTP request is to follow redirects. (default: false)
- `readTimeout`: The desired HTTP request read timeout in millisecond. (default: 60000)
- `requestTimeout`: The desired HTTP request timeout in millisecond. (default: 60000)

#### Definition

```dataweave
{ followRedirects?: Boolean, readTimeout?: Number, requestTimeout?: Number }
```


### **HttpRequestCookies**


DataWeave type for representing a HTTP request cookies.

#### Definition

```dataweave
{ _?: String }
```


### **HttpResponse**


DataWeave type for representing an HTTP response.
Supports the following fields:

- `status`: The HTTP response status.
- `headers`: The HTTP response headers.
- `cookies`: The HTTP response cookies.
- `contentType`: The HTTP response `Content-Type`.
- `statusText`: The HTTP response status message.
- `body`: The HTTP response body.

#### Definition

```dataweave
{ status: Number, headers: HeadersType, cookies: HttpResponseCookies, contentType?: String, statusText?: String, body?: BodyType }
```


### **HttpResponseCookie**


DataWeave type for representing an HTTP response cookies.
Supports the following fields:

- `name`: The name of the cookie.
- `value`: The value of the cookie.
- `maxAge`: The maximum age of the cookie, specified in seconds.
- `httpOnly`: `true` if this cookie contains the HttpOnly attribute.
- `secure`: `true` if sending this cookie should be restricted to a secure protocol, or `false` if the it can be sent using any protocol.
- `domain`: The domain name set for this cookie.
- `comment`: The comment describing the purpose of this cookie.
- `path`: The path on the server to which the browser returns this cookie.

#### Definition

```dataweave
{ name: String, value: String, maxAge: Number, httpOnly: Boolean, secure: Boolean, domain?: String, comment?: String, path?: String }
```


### **HttpResponseCookies**


DataWeave type for representing HTTP response cookies.

#### Definition

```dataweave
{ _?: HttpResponseCookie }
```


### **OAuth**


DataWeave type for representing an OAuth.

#### Definition

```dataweave
{ token: String }
```


### **QueryParams**


DataWeave type for representing a HTTP request query parameters.

#### Definition

```dataweave
{ _*?: String }
```


### **SerializationConfig**


DataWeave type for representing an HTTP request configuration.
Supports the following fields:

- `contentType`: The ContentType used for HTTP request serialization.
- `readerProperties`: The reader configuration properties used for read the HTTP response body.
- `writerProperties`: The writer configuration properties used for write the HTTP request body.

#### Definition

```dataweave
{ contentType: String, readerProperties?: Object, writerProperties?: Object }
```


### **UrlBuilder**


DataWeave type for representing an HTTP request URL.
Supports the following fields:

- `url`: The HTTP request url.
- `queryParams`: The HTTP request query parameters.

#### Definition

```dataweave
{ url: String, queryParams?: QueryParams }
```




