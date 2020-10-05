%dw 2.0

import * from dw::io::http::Types
import * from dw::io::http::Server
import * from dw::core::Objects

fun isOptions(request: HttpServerRequest) =
  request.method == "OPTIONS"

//TODO improve with cors config for now use it hardcoded
fun CORS(allowOrigin: Array<String> = ["*"], allowMethods: Array<String> = ["POST", "GET", "OPTIONS"], allowHTTPHeaders: Array<String> = ["*"], maxAge: Number = 86400, exposeHeaders: Array<String> = [""]) =
  {
    onRequest: (
      (req) ->
        if (isOptions(req) and req.headers.Origin?)
          {
            response: {
              headers: {
                "Access-Control-Allow-Origin": allowOrigin joinBy ", ",
                "Access-Control-Allow-Methods": allowMethods joinBy ", ",
                "Access-Control-Allow-Headers": allowHTTPHeaders joinBy ", ",
                "Access-Control-Max-Age": maxAge,
                "Access-Control-Expose-Headers": exposeHeaders joinBy ", "
              }
            }
          }
        else
          {request: req}
      ),
    onResponse: (
      (req, resp) ->
        if (req.headers.Origin?)
          resp mergeWith { headers: resp.headers mergeWith {"Access-Control-Allow-Origin": allowOrigin joinBy ", "} }
        else
          resp
      )
  }
