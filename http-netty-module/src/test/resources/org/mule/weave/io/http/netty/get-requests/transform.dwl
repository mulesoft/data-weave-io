%dw 2.0

import * from dw::io::http::Client
import * from dw::io::http::Server

output application/json

var serverConfig = { host: "localhost", port: dw::io::http::utils::Port::freePort(), normalizeResponseHeaders: false}
var LOCALHOST = '$(serverConfig.host):$(serverConfig.port)'
var server = api(serverConfig,
  {
    "/echo.*": {
      "GET": (req: HttpServerRequest) -> do {
        {
          responseStatus: 200,
          body: req
        }
      },
    },
    "/bytes": {
      "GET": (req) -> {
        responseStatus: 200,
        headers: {
          "Content-Type": "application/octet-stream",
          "Content-Length": sizeOf(in0)
        },
        body: in0
      }
    }
  })

---
{
  a: do {
    var response = get( 'http://$LOCALHOST/echo')
    ---
    {
      statusText: response.statusText,
      status: response.status,
      contentType: response.contentType
    }
  },
  // validate urlEncode in invalid uri characters
  b: do {
    var response = get('http://$LOCALHOST/echo/anything/%7Basdasd%7D?a')
    var body = response.body
    ---
    {
      status : response.status,
      statusText: response.statusText,
      body: {
        body: body.body,
        method: body.method,
        path: body.path,
        queryParams: body.queryParams,
        headers: (body.headers as Object) - "host"
      },
      cookies: response.cookies,
      contentType: response.contentType,
    }
  },
  c: do {
    var response = get( 'http://$LOCALHOST/bytes')
    ---
    {
      statusText: response.statusText,
      status : response.status,
      contentType: response.contentType,
      headers: response.headers
    }
  },
  z: server.stop()
}