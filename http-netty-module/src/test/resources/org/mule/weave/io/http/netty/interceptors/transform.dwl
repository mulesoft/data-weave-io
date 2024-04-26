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
---
{
  a: request( { method: "GET", url: 'http://$LOCALHOST/test1'})
    then {
     status: ($).status,
     body: $.body,
     (contentType: $.contentType) if ($.contentType?),
     cookies: $.cookies,
     headers: $.headers
  },
  b: post('http://$LOCALHOST/test1', { Authorization: 'Bearer 123'}, { name: 'Agustin' }) then {
     status: ($).status,
     body: $.body,
     contentType: $.contentType,
     cookies: $.cookies,
     headers: $.headers
  }
}
