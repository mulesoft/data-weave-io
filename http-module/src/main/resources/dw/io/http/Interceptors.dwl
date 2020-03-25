%dw 2.0

import * from dw::io::http::Types
import * from dw::io::http::Server
import * from dw::core::Objects

fun isOptions(request: HttpServerRequest) =
  request.method == "OPTIONS"

//TODO improve with cors config for now use it hardcoded
fun CORS() =
  {
    onRequest: (
      (req) ->
        if (isOptions(req) and req.headers.Origin?)
          {
            response: {
              headers: {
                "Access-Control-Allow-Origin": "*",
                "Access-Control-Allow-Methods": "POST, GET, OPTIONS, DELETE, PUT",
                "Access-Control-Allow-Headers": "Origin, X-Requested-With, Content-Type, Accept, Accept-Encoding, Accept-Language, Host, Referer, User-Agent",
                "Access-Control-Max-Age": 60 * 60 * 24 * 20 // cache pre-flight response for 20 days
              }
            }
          }
        else
          {request: req}
      ),
    onResponse: (
      (req, resp) ->
        if (req.headers.Origin?)
          resp mergeWith { headers: resp.headers mergeWith {"Access-Control-Allow-Origin": "*"} }
        else
          resp
      )
  }
