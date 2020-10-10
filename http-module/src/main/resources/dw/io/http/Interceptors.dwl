%dw 2.0

import * from dw::io::http::Types
import * from dw::io::http::Server
import * from dw::core::Objects

fun isOptions(request: HttpServerRequest) =
  request.method == "OPTIONS"

fun CORS(allowOrigin: Array<String> | "SameOrigin" = ["*"],
         allowMethods: Array<String> = ["POST", "GET", "OPTIONS"],
         allowHTTPHeaders: Array<String> = ["*"],
         exposeHeaders: Array<String> = [""],
         maxAge: Number = -1,
         allowCredentials: Boolean = false) =
  {
    onRequest: (
      (req) ->
        if (isOptions(req) and req.headers.Origin?)
          {
            response: {
              headers: {
                "Access-Control-Allow-Origin": allowOrigin match {
                    case is Array<String> -> allowOrigin joinBy ", "
                    case is "SameOrigin" -> req.headers.Origin as String
                },
                "Access-Control-Allow-Methods": allowMethods joinBy ", ",
                "Access-Control-Allow-Headers": allowHTTPHeaders joinBy ", ",
                "Access-Control-Max-Age": maxAge,
                "Access-Control-Expose-Headers": exposeHeaders joinBy ", "
              } ++ if (allowCredentials) {"Access-Control-Allow-Credentials": true} else {}
            }
          }
        else
          {request: req}
      ),
    onResponse: (
      (req, resp) ->
        if (req.headers.Origin?)
          resp mergeWith {
            headers: resp.headers mergeWith {
                "Access-Control-Allow-Origin": allowOrigin match {
                   case is Array<String> -> allowOrigin joinBy ", "
                   case is "SameOrigin" -> req.headers.Origin as String
                },
            } ++ if (allowCredentials) {"Access-Control-Allow-Credentials": true} else {}
          }
        else
          resp
      )
  }

//CORS implementation for more specific use cases. Can determine the headers based on the HttpServerRequest.
fun CORS(allowOrigin: (HttpServerRequest) -> Array<String>,
         allowMethods: (HttpServerRequest) -> Array<String>,
         allowHTTPHeaders: (HttpServerRequest) -> Array<String>,
         exposeHeaders: (HttpServerRequest) -> Array<String>,
         maxAge: Number = -1,
         allowCredentials: Boolean = false) =
  {
    onRequest: (
      (req) ->
        if (isOptions(req) and req.headers.Origin?)
          {
            response: {
              headers: {
                "Access-Control-Allow-Origin": allowOrigin(req) joinBy ", ",
                "Access-Control-Allow-Methods": allowMethods(req) joinBy ", ",
                "Access-Control-Allow-Headers": allowHTTPHeaders(req) joinBy ", ",
                "Access-Control-Max-Age": maxAge,
                "Access-Control-Expose-Headers": exposeHeaders(req) joinBy ", "
              } ++ if (allowCredentials) {"Access-Control-Allow-Credentials": true} else {}
            }
          }
        else
          {request: req}
      ),
    onResponse: (
      (req, resp) ->
        if (req.headers.Origin?)
          resp mergeWith {
            headers: resp.headers mergeWith {
                "Access-Control-Allow-Origin": allowOrigin(req) joinBy ", "
            } ++ if (allowCredentials) {"Access-Control-Allow-Credentials": true} else {}
          }
        else
          resp
      )
  }