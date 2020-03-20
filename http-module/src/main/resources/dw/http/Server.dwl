/**
* This module contains all the functions required for declaring and using an HTTP Server
*/
%dw 2.0

import * from dw::core::Objects
import * from dw::http::BodyUtils
import * from dw::http::Types

/**
* Type for an API Definition
*/
type APIDefinition = {
    _ ?: {
        GET?: HttpHandler,
        POST?: HttpHandler,
        PUT?: HttpHandler,
        DELETE?: HttpHandler,
        PATCH?: HttpHandler,
        OPTIONS?: HttpHandler,
        HEAD?: HttpHandler,
        TRACE?: HttpHandler
    }
}

/**
* The type for an Interceptor
*/
type InterceptorCapable = { interceptors?:  Array<HttpInterceptor> }

/**
* The type for a Api Config
*/
type ApiConfig = HttpServerOptions & InterceptorCapable


type InterceptedHttpRequest = {
    /**
    * The chain of interceptor will be cut only if response is present
    */
    response?: HttpServerResponse,
    request?: HttpServerRequest
}

/**
* Interceptors of the incoming http response
*/
type HttpInterceptor = {onRequest?: (HttpServerRequest) -> InterceptedHttpRequest, onResponse?: (HttpServerRequest, HttpServerResponse) -> HttpServerResponse  }

/**
* Starts an http server at with the specified config.
* The handler with attend all the requests.
* Returns true if the server was initialized correctly
*
* === Parameters
*
* [%header, cols="1,3"]
* |===
* | Name   | Description
* | configuration | The server configuration
* | handler | The request handler
* |===
*
* === Example
*
* This example shows how the `server` behaves under different inputs.
*
* ==== Source
*
* [source,DataWeave,linenums]
* ----
* %dw 2.0
* output application/json
* ---
* import server from dw::http::Server
* ---
* server({port: 8081, host:"localhost"}, (request) -> {body: "Hello World"} )
* ----
*
**/
fun server(configuration: HttpServerOptions, handler: HttpHandler): HttpServer = native("http::HttpServerFunction")


/**
* The entry point for defining an HTTP Api. This method will receive the configuration and the api definition.
*
* === Parameters
*
* [%header, cols="1,3"]
* |===
* | Name   | Description
* | config | The configuration of an api
* | apiDefinition | The object with the api definition
* |===
*
* === Example
*
* This example shows how the `api` behaves under different inputs.
*
* ==== Source
*
* [source,DataWeave,linenums]
* ----
* %dw 2.0
* import api from dw::http::Server
* output application/json
* ---
* api(
*     {port: 8081, host: "localhost"}, {
*       "/test": {
*         GET: (request) -> {
*           body: {
*             name: "Mariano"
*           }
*         }
*     }
*   )
* ----
*
**/
fun api(config: ApiConfig = {port: 8081, host:"localhost"}, apiDefinition: APIDefinition): HttpServer = do {

fun handleRequestInterceptors(req: HttpServerRequest, interceptors: Array<HttpInterceptor>): InterceptedHttpRequest =
  interceptors match {
    case [] -> { request: req }
    case [head ~ tail] ->
      if (head.onRequest?)
        using(result = head.onRequest(req))
          if (result.response?)
            result
          else
            handleRequestInterceptors(result.request!, tail)
      else
        handleRequestInterceptors(req, tail)
  }

fun handleResponseInterceptors(req: HttpServerRequest, resp: HttpServerResponse, interceptors: Array<HttpInterceptor>): HttpServerResponse =
  interceptors match {
    case [] -> resp
    case [head ~ tail] ->
      if (head.onResponse?)
        handleResponseInterceptors(req, head.onResponse(req, resp), tail)
      else
        handleResponseInterceptors(req, resp, tail)
  }
 ---
  server(config, (request) -> do {
    var matchingHandler = apiDefinition[?(request.path matches ($$ as String))][0]
    var methodHandler = matchingHandler[(request.method)]
    var interseptedRequest = handleRequestInterceptors(request, config.interceptors default [])
    ---
    if (interseptedRequest.response?)
      interseptedRequest.response!
    else if (methodHandler == null)
      {
        body: "$(request.path) Not Found",
        status: 404
      }
    else do {
      var response = methodHandler(interseptedRequest.request!)
      var headers = normalizeHeaders(response.headers default {})
      ---
      handleResponseInterceptors(
        interseptedRequest.request!,
        {
            //If there is body and not Content-Type header is defined use the one form the config
          headers: if(response.body? and (not headers."Content-Type"?))
                     headers ++ {"Content-Type": config.contentType default "application/json"}
                   else
                     headers,
          (body: response.body) if response.body?,
          status: response.status default 200
        },
        config.interceptors default []
      )
    }
  })
}

/**
* Helper method to serve a static resource from the given
*
* === Parameters
*
* [%header, cols="1,3"]
* |===
* | Name   | Description
* | path | The path of the resource to lookup
* |===
*
* === Example
*
* This example shows how the `staticResponse` behaves under different inputs.
*
* ==== Source
*
* [source,DataWeave,linenums]
* ----
* %dw 2.0
* import api from dw::http::Server
* output application/json
* ---
* api(
*     {port: 8081, host: "localhost"}, {
*       "/test": {
*         GET: (request) -> staticResponse("index.html")
*     }
*   )
* ----
**/
fun staticResponse(path: String): HttpServerResponse = native("http::ServeResourceFunction")