#### _dw::io::http::Server_
__________________________________________

This module contains all the functions required for declaring and using an HTTP Server

# Index

### Functions
| Name | Description|
|------|------------|
| [api](#api ) | The entry point for defining an HTTP Api. This method will receive the configuration and the api definition.|
| [resourceResponse](#resourceresponse ) | Helper method to serve a static resource from the given classpath path.|
| [server](#server ) | Starts an http server at with the specified config.<br>The handler with attend all the requests.<br>Returns true if the server was initialized correctly|




### Types
| Name | Description|
|------|------------|
|[APIDefinition](#apidefinition ) | Type for an API Definition|
|[ApiConfig](#apiconfig ) | The type for a Api Config|
|[HttpHandler](#httphandler ) | |
|[HttpInterceptor](#httpinterceptor ) | Interceptors of the incoming http response|
|[HttpServer](#httpserver ) | |
|[HttpServerOptions](#httpserveroptions ) | |
|[HttpServerRequest](#httpserverrequest ) | |
|[HttpServerResponse](#httpserverresponse ) | |
|[InterceptedHttpRequest](#interceptedhttprequest ) | |
|[InterceptorCapable](#interceptorcapable ) | The type for an Interceptor|






__________________________________________


# Functions

## **api**

### _api&#40;config: ApiConfig = {
  port: 8081,
  host: "localhost"
}, apiDefinition: APIDefinition&#41;: HttpServer_

The entry point for defining an HTTP Api. This method will receive the configuration and the api definition.

##### Parameters

| Name   | Description|
|--------|------------|
| config | The configuration of an api|
| apiDefinition | The object with the api definition|


##### Example

This example shows how the `api` behaves under different inputs.

###### Source

```dataweave
%dw 2.0
import api from dw::io::http::Server
output application/json
---
api(
    {port: 8081, host: "localhost"}, {
      "/test": {
        GET: (request) -> {
          body: {
            name: "Mariano"
          }
        }
    }
  )
```
__________________________________________


## **resourceResponse**

### _resourceResponse&#40;path: String&#41;: HttpServerResponse_

Helper method to serve a static resource from the given classpath path.

##### Parameters

| Name   | Description|
|--------|------------|
| path | The path of the resource to lookup|


##### Example

This example shows how the `staticResponse` behaves under different inputs.

###### Source

```dataweave
%dw 2.0
import api from dw::io::http::Server
output application/json
---
api(
    {port: 8081, host: "localhost"}, {
      "/test": {
        GET: (request) -> resourceResponse("index.html")
    }
  )
```
__________________________________________


## **server**

### _server<HttpHandlerType <: HttpHandler&#62;&#40;configuration: HttpServerOptions, handler: HttpHandlerType&#41;: HttpServer_

Starts an http server at with the specified config.
The handler with attend all the requests.
Returns true if the server was initialized correctly

##### Parameters

| Name   | Description|
|--------|------------|
| configuration | The server configuration|
| handler | The request handler|


##### Example

This example shows how the `server` behaves under different inputs.

###### Source

```dataweave
%dw 2.0
output application/json
---
import server from dw::io::http::Server
---
server({port: 8081, host:"localhost"}, (request) -> {body: "Hello World"} )
```
__________________________________________




__________________________________________

# Types

### **APIDefinition**


Type for an API Definition

#### Definition

```dataweave
{ _?: { GET?: HttpHandler<Any, HttpHeaders, QueryParams, HttpServerResponse<Any, HttpHeaders>>, POST?: HttpHandler<Any, HttpHeaders, QueryParams, HttpServerResponse<Any, HttpHeaders>>, PUT?: HttpHandler<Any, HttpHeaders, QueryParams, HttpServerResponse<Any, HttpHeaders>>, DELETE?: HttpHandler<Any, HttpHeaders, QueryParams, HttpServerResponse<Any, HttpHeaders>>, PATCH?: HttpHandler<Any, HttpHeaders, QueryParams, HttpServerResponse<Any, HttpHeaders>>, OPTIONS?: HttpHandler<Any, HttpHeaders, QueryParams, HttpServerResponse<Any, HttpHeaders>>, HEAD?: HttpHandler<Any, HttpHeaders, QueryParams, HttpServerResponse<Any, HttpHeaders>>, TRACE?: HttpHandler<Any, HttpHeaders, QueryParams, HttpServerResponse<Any, HttpHeaders>> } }
```


### **ApiConfig**


The type for a Api Config

#### Definition

```dataweave
HttpServerOptions & InterceptorCapable
```


### **HttpHandler**




#### Definition

```dataweave
(HttpServerRequest<RequestType, RequestHeaderType, QueryParamsType>) -> HttpServerResponseType
```


### **HttpInterceptor**


Interceptors of the incoming http response

#### Definition

```dataweave
{ onRequest?: (HttpServerRequest) -> InterceptedHttpRequest, onResponse?: (HttpServerRequest, HttpServerResponse) -> HttpServerResponse }
```


### **HttpServer**




#### Definition

```dataweave
{| running: Boolean, port: Number, host: String, stop: () -> Boolean |}
```


### **HttpServerOptions**




#### Definition

```dataweave
{ port: Number, host: String, contentType?: String, normalizeResponseHeaders?: Boolean }
```


### **HttpServerRequest**




#### Definition

```dataweave
{ headers: HttpHeaders, method: String, path: String, queryParams: QueryParamsType, body: BodyType }
```


### **HttpServerResponse**




#### Definition

```dataweave
{ headers?: HeaderType, body?: BodyType, status?: Number }
```


### **InterceptedHttpRequest**




#### Definition

```dataweave
{ /**
- The chain of interceptor will be cut only if response is present
  -/
response?: HttpServerResponse, request?: HttpServerRequest }
```


### **InterceptorCapable**


The type for an Interceptor

#### Definition

```dataweave
{ interceptors?: Array<HttpInterceptor> }
```




