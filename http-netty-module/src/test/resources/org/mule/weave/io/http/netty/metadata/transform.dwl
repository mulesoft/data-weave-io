%dw 2.0
import * from dw::io::http::Server
import * from dw::io::http::Client
import * from dw::io::http::Types

var serverConfig = { host: "localhost", port: dw::io::http::utils::Port::freePort() }
var LOCALHOST = '$(serverConfig.host):$(serverConfig.port)'
var server = api(serverConfig,
  {
    "/sample": {
      "GET": (req) -> {
        responseStatus: 200
      }
    }
  })
---
[
  a: do {
    var response = sendRequestAndReadResponse({method: "GET", url: 'http://$LOCALHOST/sample'})
    var schema = response.^
    var timers = schema.timers default {}
    ---
    response
      then {
        status: $.status,
        total: schema.total is Number,
        timers: {
          dns: timers.dns is Number,
          connect: timers.connect is Number,
          send: timers.send is Number,
          wait: timers.wait is Number,
          receive: timers.receive is Number,
          total: timers.total is Number
        }
      }
    },
  b: do {
    var response =  sendRequest({method: "GET", url: 'http://$LOCALHOST/sample'})
    var schema = response.^
    var timers = schema.timers default {}
    ---
    response
      then {
        status: $.status,
        total: schema.total is Number,
        timers: {
          connect: timers.connect is Number,
          send: timers.send is Number,
          wait: timers.wait is Number,
          receive: timers.receive is Number,
          total: timers.total is Number
        }
      }
    },
  c: server.stop()
]

