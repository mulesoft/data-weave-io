import * from dw::io::http::Server
import * from dw::io::http::Client
import dw::core::Assertions

var serverConfig = { host: "localhost", port: 8081 }
var LOCALHOST = '$(serverConfig.host):$(serverConfig.port)'

var server = api(
  serverConfig, {
    "/test": {
      GET: (request) -> {
        body: {
          name: "Mariano"
        }
      },
      POST: (request) -> {
        body: {
          // TODO: Remove as String
          name: (request).body.name as String
        },
        status: 302
      }
    },
    "/testXml": {
          GET: (request) -> {
            body: {
              name: "Mariano"
            },
         headers: {
             "Content-Type": "application/xml"
         }
          },
          POST: (request) -> {
            body: {
              name: (request).body.name
            },
            headers: {
                "Content-Type": "application/xml"
            },
            status: 302
          }
        }

  }
)

fun then<A, V>(result: A, assertions: (result: A) -> V): V = assertions(result)

---
[
  request('POST', 'http://$LOCALHOST/test',
    {
      body: {name: 'Agustin'}
    }
  ) then [
    Assertions::mustEqual(($).response.body.name, 'Agustin'),
    Assertions::mustEqual($.response.headers."Content-Type", 'application/json'),
    Assertions::mustEqual(($).response.status, 302),
  ],

  request('GET', 'http://$LOCALHOST/test', {}) then [
    Assertions::mustEqual(($).response.body.name, 'Mariano'),
    Assertions::mustEqual(($).request.httpVersion, 'HTTP/1.1')
  ],
  request('POST', 'http://$LOCALHOST/testXml',
      {
        body: {name: 'Agustin'}
      }
    ) then [
      Assertions::mustEqual(($).response.body.name, 'Agustin'),
      Assertions::mustEqual($.response.headers."Content-Type", 'application/xml'),
      Assertions::mustEqual(($).response.status, 302),
    ],

    request('GET', 'http://$LOCALHOST/testXml', {}) then [
      Assertions::mustEqual(($).response.body.name, 'Mariano'),
      Assertions::mustEqual($.response.headers."Content-Type", 'application/xml'),
      Assertions::mustEqual(($).request.httpVersion, 'HTTP/1.1')
    ],

  Assertions::mustEqual(server.stop(), true)
] is Array
