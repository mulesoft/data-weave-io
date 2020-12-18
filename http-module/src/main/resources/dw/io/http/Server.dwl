/**
* This module contains all the functions required for declaring and using an HTTP Server
*/
%dw 2.0

import * from dw::core::Objects
import * from dw::io::http::BodyUtils
import * from dw::io::http::Types
import * from dw::io::http::utils::HttpHeaders
import dw::io::file::FileSystem
import * from dw::Runtime


type HttpServerResponse<BodyType <: HttpBody, HeaderType <: HttpHeaders> = {
  headers?: HeaderType,
  body?: BodyType,
  status?: Number
}


type HttpServerRequest<BodyType, HeaderType <: HttpHeaders, QueryParamsType <: QueryParams> = {
  headers: HttpHeaders,
  method: String,
  path: String,
  queryParams: QueryParamsType,
  body: BodyType
}

type HttpHandler<
                    RequestType,
                    RequestHeaderType <: HttpHeaders,
                    QueryParamsType <: QueryParams,
                    HttpServerResponseType <: HttpServerResponse<Any, HttpHeaders>
                 > =
                    (HttpServerRequest<RequestType, RequestHeaderType, QueryParamsType>) -> HttpServerResponseType

type HttpServerOptions = {
  port: Number,
  host: String,
  contentType?: String
}

type HttpServer = {|
  running: Boolean,
  port: Number,
  host: String,
  stop: () -> Boolean
|}


/**
* Type for an API Definition
*/
type APIDefinition = {
    _ ?: {
        GET?: HttpHandler<Any,HttpHeaders,QueryParams,HttpServerResponse<Any, HttpHeaders>>,
        POST?: HttpHandler<Any,HttpHeaders,QueryParams,HttpServerResponse<Any, HttpHeaders>>,
        PUT?: HttpHandler<Any,HttpHeaders,QueryParams,HttpServerResponse<Any, HttpHeaders>>,
        DELETE?: HttpHandler<Any,HttpHeaders,QueryParams,HttpServerResponse<Any, HttpHeaders>>,
        PATCH?: HttpHandler<Any,HttpHeaders,QueryParams,HttpServerResponse<Any, HttpHeaders>>,
        OPTIONS?: HttpHandler<Any,HttpHeaders,QueryParams,HttpServerResponse<Any, HttpHeaders>>,
        HEAD?: HttpHandler<Any,HttpHeaders,QueryParams,HttpServerResponse<Any, HttpHeaders>>,
        TRACE?: HttpHandler<Any,HttpHeaders,QueryParams,HttpServerResponse<Any, HttpHeaders>>
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
* import server from dw::io::http::Server
* ---
* server({port: 8081, host:"localhost"}, (request) -> {body: "Hello World"} )
* ----
*
**/
@RuntimePrivilege(requires = "http.Server")
fun server<HttpHandlerType <: HttpHandler>(configuration: HttpServerOptions, handler: HttpHandlerType): HttpServer = native("http::HttpServerFunction")


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
* import api from dw::io::http::Server
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
          if (head.onRequest?) do {
            var result = head.onRequest(req)
            ---
            if (result.response?)
                result
            else
                handleRequestInterceptors(result.request!, tail)
          } else
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
        var interceptedRequest = handleRequestInterceptors(request, config.interceptors default [])
        ---
        if (interceptedRequest.response?)
          interceptedRequest.response!
        else if (methodHandler == null)
          {
            body: "$(request.path) Not Found",
            status: 404
          }
        else do {
          var response = methodHandler(interceptedRequest.request!)
          var headers = normalizeHeaders(response.headers)
          ---
          handleResponseInterceptors(
            interceptedRequest.request!,
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
      }
   )
}

/**
* Helper method to serve a static resource from the given classpath path.
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
* import api from dw::io::http::Server
* output application/json
* ---
* api(
*     {port: 8081, host: "localhost"}, {
*       "/test": {
*         GET: (request) -> resourceResponse("index.html")
*     }
*   )
* ----
**/
fun resourceResponse(path: String): HttpServerResponse = do {
    var content: TryResult<Binary> = try(() -> readUrl("classpath://" ++ path, "binary") as Binary)
    ---
    if(content.success)
        {
            headers: {
                (CONTENT_TYPE_HEADER): FileSystem::mimeTypeOf(path) default "application/octet-stream",
                (CONTENT_LENGTH_HEADER): sizeOf(content.result!),
            },
            body: content.result!,
            status: 200
        }
    else
        {
          body: content.error.message!,
          headers: {
            (CONTENT_TYPE_HEADER):  "text/plain",
          },
          status: 404
        }
}