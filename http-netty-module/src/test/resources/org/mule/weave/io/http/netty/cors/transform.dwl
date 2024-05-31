import * from dw::io::http::Server
import * from dw::io::http::Client
import * from dw::io::http::Interceptors
import freePort from dw::io::http::utils::Port

/// SERVER
var serverConfig = { host: "localhost", port: freePort() }
var LOCALHOST = '$(serverConfig.host):$(serverConfig.port)'

var server = api(
  serverConfig ++ {interceptors: [CORS()]},
    {
      "/test1" : {
        "POST": (req) ->  {
          body: {
            req: req
          },
          responseStatus: 200
        },
        "GET": (req) ->  {
           body: {
             req: req
           },
           responseStatus: 200
         }
      }
   }

)
---
[
  get( 'http://$LOCALHOST/test1', { Origin: "localhost" })
    then {
      status: $.status,
      "Access-Control-Allow-Origin": $.headers."Access-Control-Allow-Origin"
  },
  options('http://$LOCALHOST/test1', { Origin: "localhost" })
    then {
       status: $.status,
       "Access-Control-Allow-Origin": $.headers."Access-Control-Allow-Origin"
  },
  server.stop()
]
