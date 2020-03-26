import * from dw::io::http::Server
import * from dw::io::http::Client
import raml!org::mule::weave::v2::module::test::raml::engine::basic_get::api as Api

var serverConfig = { host: "localhost", port: 8081 }
var LOCALHOST = '$(serverConfig.host):$(serverConfig.port)'

var server = api(
  serverConfig, {
    "/user": {
      GET: (request) -> {
        body: {
          name: "Mariano",
          lastName: "Achaval"
        }
      }
    }
  }
)

var client = Api::client()
---
{
    a: client."/user".get().body
}