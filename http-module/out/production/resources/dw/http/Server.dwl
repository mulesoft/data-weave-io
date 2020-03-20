%dw 2.0

import * from dw::core::Objects
import * from dw::http::BodyUtils
import * from dw::http::Types

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

type InterceptorCapable = { interceptors?:  Array<HttpInterceptor> }

type ApiConfig = HttpServerOptions & InterceptorCapable

type InterceptedHttpRequest = {
    /**
    * The chain of interceptor will be cut only if response is present
    */
    response?: HttpServerResponse,
    request?: HttpServerRequest
}

type HttpInterceptor = {onRequest?: (HttpServerRequest) -> InterceptedHttpRequest, onResponse?: (HttpServerRequest, HttpServerResponse) -> HttpServerResponse  }

/**
* Starts an http server at with the specified config.
* The handler with attend all the requests.
* Returns true if the server was initialized correctly
*/
fun server(configuration: HttpServerOptions, handler: HttpHandler): HttpServer = native("http::HttpServerFunction")

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



/**
* Initialize an api with the specified APIDefinition
*/
fun api(config: ApiConfig = {port: 8081, host:"localhost"}, apiDefinition: APIDefinition): HttpServer =
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

/**
* Returns a static resoure respone using the specified path. Use this method to serve static content
*/
fun serveResource(path: String): HttpServerResponse = native("http::ServeResourceFunction")