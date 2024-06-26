%dw 2.0

import * from dw::Runtime
import * from dw::core::Objects
import * from dw::io::http::Client
import * from dw::io::http::Server
import * from dw::io::http::Types

output application/json

fun createClientConfig(decompress: Boolean): HttpClientConfig = do {
  DEFAULT_HTTP_CLIENT_CONFIG mergeWith
    {
      decompress: decompress
    }
}

var serverConfig = { host: "localhost", port: dw::io::http::utils::Port::freePort()}
var LOCALHOST = '$(serverConfig.host):$(serverConfig.port)'
var server = api(serverConfig,
  {
    "/decompress": {
      "GET": (req) -> {
        responseStatus: 200,
        headers: {
          "Content-Type": "text-plain",
          "Content-Encoding": "gzip"
        },
        body: in0
      }
    },
    "/no-decompress": {
      "GET": (req) -> {
        responseStatus: 200,
        headers: {
          "Content-Type": "text-plain"
        },
        body: in1
      }
    }
  })
---
{
  a: do {
    var result = try(() -> sendRequest({method: "GET", url: 'http://$LOCALHOST/decompress'}, DEFAULT_HTTP_REQUEST_CONFIG, createClientConfig(false))).error
    ---
    {
      kind: result.kind,
      message: result.message
    }
  },
  b: sendRequest({method: "GET", url: 'http://$LOCALHOST/decompress'}, DEFAULT_HTTP_REQUEST_CONFIG, createClientConfig(true)).body,
  c: sendRequest({method: "GET", url: 'http://$LOCALHOST/no-decompress'}, DEFAULT_HTTP_REQUEST_CONFIG, createClientConfig(true)).body,
  d: server.stop()
}