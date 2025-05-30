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
    var response = sendRequestAndReadResponse({method: "GET", url: 'http://$LOCALHOST/sample'},{
                                                                                              followRedirects: false,
                                                                                              readTimeout: 60000,
                                                                                              requestTimeout: 60000,
                                                                                              streamResponse: false,
                                                                                              enableMetrics: true
                                                                                            })
    var schema = response.^
    var timers = schema.timers default {}
    ---
    response
      then {
        status: $.status,
        timers: {
          dns: timers.dns >= 0,
          connect: timers.connect >= 0,
          send: timers.send >= 0,
          wait: timers.wait >= 0,
          receive: timers.receive >= 0,
          total: timers.total >= 0
        }
      }
    },
  b: do {
    var response =  sendRequest({method: "GET", url: 'http://$LOCALHOST/sample'},{
                                                                                   followRedirects: false,
                                                                                   readTimeout: 60000,
                                                                                   requestTimeout: 60000,
                                                                                   streamResponse: false,
                                                                                   enableMetrics: true
                                                                                 })
    var schema = response.^
    var timers = schema.timers default {}
    ---
    response
      then {
        status: $.status,
        timers: {
          connect: timers.connect >= 0,
          send: timers.send >= 0,
          wait: timers.wait >= 0,
          receive: timers.receive >= 0,
          total: timers.total >= 0
        }
      }
    },
  c: server.stop()
]

