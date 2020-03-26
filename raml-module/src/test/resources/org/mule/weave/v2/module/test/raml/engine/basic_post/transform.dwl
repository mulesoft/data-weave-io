import * from dw::io::http::Server
import * from dw::io::http::Client
import raml!org::mule::weave::v2::module::test::raml::engine::basic_post::api as Api

var serverConfig = { host: "localhost", port: 8081 }
var LOCALHOST = '$(serverConfig.host):$(serverConfig.port)'

var server = api(
  serverConfig, {
    "/user": {
      POST: (request) -> {
        body: request.body
      }
    }
  }
)

var client = Api::client()
---
{
    a: client."/user".post(body: {name: "Leandro", lastName: "Shokida"}).body
}