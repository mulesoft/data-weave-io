%dw 2.0
output application/json

import * from dw::io::http::Client
import * from dw::io::http::Server
import * from dw::io::http::utils::Port
import mergeWith from dw::core::Objects

var serverConfig = { host: "localhost", port: dw::io::http::utils::Port::freePort()}
var LOCALHOST = '$(serverConfig.host):$(serverConfig.port)'
var server = api(serverConfig,
  {
    "/test": {
      "GET": (req) -> {
        responseStatus: 200,
        headers: {
          "Content-Type": "application/csv"
        },
        body: in0
      }
    }
  })
var request = {
      method: "GET",
      url: {
        url: "http://$LOCALHOST/test"
      }
    }
var response = sendRequest(request,DEFAULT_HTTP_REQUEST_CONFIG, DEFAULT_SERIALIZATION_CONFIG)
---
{
  status: response.status
}