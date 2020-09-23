%dw 2.0

import * from dw::io::http::Types
import * from dw::io::http::Server
import * from dw::core::Objects

fun isOptions(request: HttpServerRequest) =
  request.method == "OPTIONS"

//TODO improve with cors config for now use it hardcoded
fun CORS(allowOrigin: String = "*", allowMethods: String = "POST, GET, OPTIONS", allowHTTPHeaders: String = "*", maxAge: Number = 86400, exposeHeaders: String = "") =
  {
    onRequest: (
      (req) ->
        if (isOptions(req) and req.headers.Origin?)
          {
            response: {
              headers: {
                "Access-Control-Allow-Origin": allowOrigin,
                "Access-Control-Allow-Methods": allowMethods,
                "Access-Control-Allow-Headers": allowHTTPHeaders,
                "Access-Control-Max-Age": maxAge,
                "Access-Control-Expose-Headers": exposeHeaders
              }
            }
          }
        else
          {request: req}
      ),
    onResponse: (
      (req, resp) ->
        if (req.headers.Origin?)
          resp mergeWith { headers: resp.headers mergeWith {"Access-Control-Allow-Origin": allowOrigin} }
        else
          resp
      )
  }
