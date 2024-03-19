import * from dw::io::http::Client
import * from dw::io::http::Server

var serverConfig = { host: "localhost", port: dw::io::http::utils::Port::freePort() }
var LOCALHOST = '$(serverConfig.host):$(serverConfig.port)'

var server = api(
  serverConfig, {
    "/test": {
      GET: (request) -> {
        body: {
          name: "Mariano"
        }
      },
      POST: (request) -> {
        body: {
          name: (request).body.name as String
        },
        status: 302
      }
    }
  }
)

var config: HttpClientConfiguration = {
    connectionTimeout: 1000
}

var client = Http(config)
---
[
  client.send({ method: 'GET', url: 'http://$LOCALHOST/test', body: {} })
    then {
      name: ($).body.name,
  },
  server.stop()
]