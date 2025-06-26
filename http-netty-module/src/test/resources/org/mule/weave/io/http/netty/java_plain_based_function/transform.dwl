%dw 2.0

import * from dw::io::http::Client
import * from dw::io::http::Server
import * from dw::module::Multipart
import * from org::mule::weave::io::http::ContentDocument
output application/json

var serverConfig = { host: "localhost", port: dw::io::http::utils::Port::freePort(), normalizeResponseHeaders: false }
var LOCALHOST = '$(serverConfig.host):$(serverConfig.port)'
var server = api(serverConfig,
  {
    "/multipart": {
      "GET": (req: HttpServerRequest) -> do {
       {
         responseStatus: 200,
         headers: {
           "Content-Type": "multipart/form-data;boundary=---------------------------9051914041544843365972754266"
         },
         body: form([
           field('in0', in0, "application/csv", "in0.csv")
           ])
       }
      }
    }
  })
---
{
  a: do {
    var response = get('http://$LOCALHOST/multipart')
    var part = response.body.parts.in0
    ---
    create("in0", part.headers.'Content-Type', part.content.^raw)
  },
  z: server.stop()
}