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
      "GET": (req: HttpServerRequest) -> do {
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
    var response = get( 'http://$LOCALHOST/echo')
    ---
    {
      statusText: response.statusText,
      status: response.status,
      contentType: response.contentType
    }
  },
  z: server.stop()
}
