%dw 2.0
import * from dw::core::Objects
import * from dw::io::http::Client
import * from dw::io::http::Server
import * from dw::io::http::Types

output application/json

fun createClientConfig(compressionHeader: Boolean): HttpClientConfig = do {
  DEFAULT_HTTP_CLIENT_CONFIG mergeWith
    {
      compressionHeader: compressionHeader
    }
}

var serverConfig = { host: "localhost", port: dw::io::http::utils::Port::freePort()}
var LOCALHOST = '$(serverConfig.host):$(serverConfig.port)'
var server = api(serverConfig,
  {
    "/compression": {
          "GET": (req) -> {
            responseStatus: 200,
            headers: req.headers
          }
        }
  })
---
{
  a: do {
    var response = sendRequest({method: "GET", url: 'http://$LOCALHOST/compression'}, DEFAULT_HTTP_REQUEST_CONFIG, createClientConfig(false))
    ---
    response.headers["Accept-Encoding"]
  },
  b: do {
    var response = sendRequest({method: "GET", url: 'http://$LOCALHOST/compression'}, DEFAULT_HTTP_REQUEST_CONFIG, createClientConfig(true))
    ---
    response.headers["Accept-Encoding"]
  },
  c: server.stop()
}