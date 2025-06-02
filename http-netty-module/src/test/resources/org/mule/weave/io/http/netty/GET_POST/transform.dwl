%dw 2.0

import * from dw::io::http::Client
import * from dw::io::http::Server
import * from dw::io::http::utils::Port

var serverConfig = { host: "localhost", port: dw::io::http::utils::Port::freePort()}
var LOCALHOST = '$(serverConfig.host):$(serverConfig.port)'

// Create a 1024 byte body
var body = (1 to 1024) map "x" joinBy ""

var server = api(serverConfig,
  {
    "/chunked-response": {
      "GET": (req) -> {
        responseStatus: 200,
        headers: {
          "Content-Type": "application/octet-stream",
          "Transfer-Encoding": "chunked"
        },
        body: body
      }
    }
  })

fun then<A, V>(result: A, assertions: (result: A) -> V): V = assertions(result)
---
{
  // Test chunked streaming
  d: get('http://$LOCALHOST/chunked-response') then {
       body: $.body is Binary,
       bodySize: sizeOf($.body!) == 1024,
       contentType: $.contentType
  }
}

