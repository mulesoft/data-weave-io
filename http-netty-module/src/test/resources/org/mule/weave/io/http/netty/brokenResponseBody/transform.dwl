%dw 2.0
import * from dw::io::http::Server
import * from dw::io::http::Client
import * from dw::Runtime

type User = {
  name: String
}

var serverConfig = { host: "localhost", port: dw::io::http::utils::Port::freePort() }
var LOCALHOST = '$(serverConfig.host):$(serverConfig.port)'
var server = api(serverConfig,
  {
    "/broken-json-body": {
      "GET": (req) -> {
        responseStatus: 200,
        headers: {
          "Content-Type": "application/json"
        },
        body: '{"name": "Ma' as Binary
      }
    }
  })
---
[
 a: do {
   var response = sendRequestAndReadResponse({method: "GET", url: 'http://$LOCALHOST/broken-json-body'})
   ---
   {
     status: response.status,
     contentType: response.contentType,
     headers: response.headers,
     raw: response.body.^raw,
     mimeType: response.body.^mimeType,
   }
 },
 b: do {
   var response = sendRequestAndReadResponse({method: "GET", url: 'http://$LOCALHOST/broken-json-body'})
   ---
   {
     status: response.status,
     contentType: response.contentType,
     headers: response.headers,
     raw: response.body.^raw,
     mimeType: response.body.^mimeType,
     body: do {
       var bodyResult = try(() -> response.body as User)
       ---
       {
         success: bodyResult.success,
         error: {
           kind: bodyResult.error.kind,
           message: bodyResult.error.message,
         },
         result: bodyResult.result
       }
     }
   }
 },
 z: server.stop()
]