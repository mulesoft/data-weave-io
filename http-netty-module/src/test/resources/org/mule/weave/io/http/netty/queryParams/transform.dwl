%dw 2.0
import * from dw::io::http::Client
import * from dw::io::http::Server

var serverConfig = { host: "localhost", port: dw::io::http::utils::Port::freePort()}
var LOCALHOST = '$(serverConfig.host):$(serverConfig.port)'
var server = api(serverConfig,
  {
    "/queryParams": {
          "GET": (req) -> {
            responseStatus: 200,
            body: {
              queryParams: req.queryParams
            }
          }
        }
  })
---
{
  a: do {
    var response = sendRequestAndReadResponse({method: "GET", url: {url: 'http://$LOCALHOST/queryParams', queryParams: {a: "A", b: "B"}}})
    ---
    response.body.queryParams
  },
  b: do {
    var response = sendRequestAndReadResponse({method: "GET", url: {url: 'http://$LOCALHOST/queryParams', queryParams: {a: "A", a: "B"}}})
    ---
    response.body.queryParams
  }
}
