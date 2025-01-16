%dw 2.0

output application/json

import * from dw::io::http::Client
import * from dw::io::http::Server
import * from dw::io::http::utils::Port

var serverConfig = { host: "localhost", port: freePort(), normalizeResponseHeaders: false}
var LOCALHOST = '$(serverConfig.host):$(serverConfig.port)'
var server = api(serverConfig,
  {
    "/echo.*": {
      "POST": (req: HttpServerRequest) -> do {
        {
          responseStatus: 200,
          body: req
        }
      }
    }
  })
---
{
  a: do {
    var request = {
      method: "POST",
      url: {
        url: "http://$LOCALHOST/echo",
        queryParams: {
          asd: "123",
          space: "Mariano de Achaval",
          a: "a",
          A: "A"
        }
      },
      headers: {
        "CONTENT-Type": "application/xml",
        "X-API-TOKEN": "1st Token",
        "X-Api-Token": "2nd Token"
      },
      cookies: {
        "Cookie1": "A",
        "cookie1": "b"
      },
      body: {
        root: {
          child: "Hi"
        }
      }
    }
    var response = sendRequestAndReadResponse(request)
    var body = response.body
    ---
    {
      status: response.status,
      headers: response.headers,
      mimeType: body.^mimeType,
      body: {
        body: body.body,
        method: body.method,
        path: body.path,
        queryParams: body.queryParams,
        headers: (body.headers as Object) - "host"
      },
      contentType: response.contentType,
    }
  },
  z: server.stop()
}
