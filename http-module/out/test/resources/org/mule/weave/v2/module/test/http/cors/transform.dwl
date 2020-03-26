import * from dw::io::http::Server
import * from dw::io::http::Client
import * from dw::io::http::Interceptors
import dw::core::Assertions

/// SERVER
var serverConfig = { host: "localhost", port: 8082 }
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

fun then<A, V>(result: A, assertions: (result: A) -> V): V = assertions(result)

---
[
  request(
    'GET', 'http://$LOCALHOST/test1', {
      headers: {
        Origin: "localhost"
      }
    }
  ) then [
     Assertions::mustEqual(($).response.status, 200),
     Assertions::mustEqual(($).response.headers."Access-Control-Allow-Origin", "*")
  ],
  request(
      'OPTIONS', 'http://$LOCALHOST/test1', {
        headers: {
          Origin: "localhost"
        }
      }
    ) then [
       Assertions::mustEqual(($).response.status, 200),
       Assertions::mustEqual(($).response.headers."Access-Control-Allow-Origin", "*")
    ]
]
