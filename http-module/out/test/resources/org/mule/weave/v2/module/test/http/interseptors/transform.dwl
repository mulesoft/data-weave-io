import * from dw::io::http::Server
import * from dw::io::http::Client
import dw::core::Assertions

/// MIDDLEWARES

fun authorization(req) =
  if(req.headers.Authorization? and (req.headers.Authorization matches /^Bearer .*/))
    //Authorized
    { request: req }
  else
    { response: { status: 401 } }

var authInterceptor = { onRequest: authorization }
/// SERVER

var serverConfig = { host: "localhost", port: 8082 }
var LOCALHOST = '$(serverConfig.host):$(serverConfig.port)'

var server = api(
  serverConfig ++ {interceptors: [authInterceptor]},
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
    'GET', 'http://$LOCALHOST/test1', {}
  ) then [
     Assertions::mustEqual(($).response.status, 401),
  ],

  request(
    'GET', 'http://$LOCALHOST/test1', {}
  ) then [
     Assertions::mustEqual($.request.url, 'http://$LOCALHOST/test1'), // URL must preseve PORT
     Assertions::mustEqual($.request.port, serverConfig.port), // URL extract the PORT
     Assertions::mustEqual($.request.path, '/test1'),
     Assertions::mustEqual($.request.method, 'GET'),
     Assertions::mustEqual($.request.headers.Host, LOCALHOST), // MUST PRESERVE PORT
  ],


  request('POST', 'http://$LOCALHOST/test1', {
    headers: Authorization: 'Bearer 123',
    body: {
      name: 'Agustin'
    }
  }) then [
     Assertions::mustEqual(($).response.status, 200),
  ],

  Assertions::mustEqual(server.stop(), true)
]
