import * from dw::io::http::Server
import * from dw::io::http::Client
import freePort from dw::io::http::utils::Port

/// MIDDLEWARES

fun authorization(req) =
  if(req.headers.Authorization? and (req.headers.Authorization matches /^Bearer .*/))
    //Authorized
    { request: req }
  else
    { response: { status: 401 } }

var authInterceptor = { onRequest: authorization }
/// SERVER

var serverConfig = { host: "localhost", port: freePort() }
var LOCALHOST = '$(serverConfig.host):$(serverConfig.port)'

var server = api(
  serverConfig ++ {interceptors: [authInterceptor]},
    {
      "/test1" : {
        "POST": (req) ->  {
          body: {
            req: req.body
          },
          responseStatus: 200
        },
        "GET": (req) ->  {
           body: {
             req: req.body
           },
           responseStatus: 200
         }
      }
   }

)

fun then<A, V>(result: A, assertions: (result: A) -> V): V = assertions(result)

---
{
  a: request(
    'GET', 'http://$LOCALHOST/test1', {}
  ) then {
     status: ($).status,
     body: $.body
  },
  b: POST('http://$LOCALHOST/test1', {
    headers: {
      Authorization: 'Bearer 123',
    },
    body: {
      name: 'Agustin'
    }
  }) then {
     status: ($).status,
     body: $.body
  }
}
