%dw 2.0
import * from dw::io::http::Client
import * from dw::io::http::Server

var serverConfig = { host: "localhost", port: dw::io::http::utils::Port::freePort()}
var LOCALHOST = '$(serverConfig.host):$(serverConfig.port)'
var server = api(serverConfig,
  {
    "/headers": {
          "GET": (req) -> {
            responseStatus: 200,
            body: {
              headers: req.headers
            }
          }
        }
  })
---
{
  a: do {
    var response = sendRequestAndReadResponse({method: "GET", url: 'http://$LOCALHOST/headers', headers: {a: 'A', b: 'B'}})
    ---
    response.body.headers - "host" - "accept" - "user-agent" - "content-length"
  },
  b: do {
    var response = sendRequestAndReadResponse({method: "GET", url: 'http://$LOCALHOST/headers', headers: {a: "A", a: "B"}})
    ---
    response.body.headers - "host" - "accept" - "user-agent" - "content-length"
  }
}
