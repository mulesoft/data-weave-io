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
        requestFunctionTotal: schema.requestFunctionTotal is Number,
        timers: {
          hostnameResolutionSuccess: timers.hostnameResolutionSuccess is Number,
          tcpConnectSuccess: timers.tcpConnectSuccess is Number,
          statusReceived: timers.statusReceived is Number,
          completed: timers.completed is Number,
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
        requestFunctionTotal: schema.requestFunctionTotal is Number,
        timers: {
          connectionPooled: timers.connectionPooled is Number,
          statusReceived: timers.statusReceived is Number,
          completed: timers.completed is Number,
          total: timers.total is Number
        }
      }
    },
  c: server.stop()
]

