%dw 2.0
import * from dw::io::http::Server
import * from dw::io::http::Client

var serverConfig = { host: "localhost", port: dw::io::http::utils::Port::freePort() }
var LOCALHOST = '$(serverConfig.host):$(serverConfig.port)'
var server = api(
               serverConfig, {
                     //TODO: need uri params to extract parts of the path
                   "/.+": {
                     GET: (request) -> resourceResponse(request.path),
                   },
               }
             )

fun then<A, V>(result: A, assertions: (result: A) -> V): V = assertions(result)
---
{
  a: GET('http://$LOCALHOST/www/index.html')
   then {
    contentType: $.headers."Content-Type" ,
    status: $.status,
    statusText: $.statusText,
  }
}