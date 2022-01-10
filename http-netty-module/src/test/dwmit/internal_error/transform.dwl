import * from dw::io::http::Server
import * from dw::io::http::Client


var serverConfig = { host: "localhost", port: dw::io::http::utils::Port::freePort() }
var LOCALHOST = '$(serverConfig.host):$(serverConfig.port)'

var server = api(
  serverConfig, {
    "/test": {
      POST: (request) -> {
        body: {
          name: dw::Runtime::fail("Fail!")
        },
        status: 302
      }
    }

  }
)
---
[
  request('POST', 'http://$LOCALHOST/test',
    {
      body: "{}TATO"
    }
  ) then {
    name: ($).body ,
    "Content-Type": $.headers."Content-Type",
    status: ($).status ,
  },
  server.stop()
]
