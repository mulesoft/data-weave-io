import * from dw::io::http::Server
import * from dw::io::http::Client
import mustEqual from dw::core::Assertions

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
    "/properties": {
            GET: (request) -> {
              body: {
                method: request.method,
                path: request.path,
                headers: request.headers,
                params: request.queryParams
              }
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
    ($).response.body.name mustEqual 'Agustin',
    $.response.headers."Content-Type" mustEqual 'application/json',
    ($).response.status mustEqual 302,
  ],

  request('GET', 'http://$LOCALHOST/test', {}) then [
    ($).response.body.name mustEqual 'Mariano',
    ($).request.httpVersion mustEqual 'HTTP/1.1'
  ],
  request('POST', 'http://$LOCALHOST/testXml',
      {
        body: {name: 'Agustin'}
      }
    ) then [
      ($).response.body.name mustEqual 'Agustin',
      $.response.headers."Content-Type" mustEqual 'application/xml',
      ($).response.status mustEqual 302
    ],

    request('GET', 'http://$LOCALHOST/testXml', {}) then [
      ($).response.body.name mustEqual 'Mariano',
      $.response.headers."Content-Type" mustEqual 'application/xml',
      ($).request.httpVersion mustEqual 'HTTP/1.1'
    ],
    request('GET', 'http://$LOCALHOST/properties?some=query&other=value', {
        headers : {
            ("X-Custom"): "headerValue"
        }
    }) then [
      ($).request.httpVersion mustEqual 'HTTP/1.1',
      ($).response.body.method mustEqual 'GET',
      ($).response.body.path mustEqual '/properties',
      ($).response.body.params mustEqual {
                                            some : "query",
                                            other : "value"
                                          },
      ($).response.body.headers mustEqual {
                                             "X-Custom": "headerValue",
                                             "Accept-Encoding": "gzip,deflate",
                                             Host: "localhost:8081",
                                             Connection: "close",
                                             "User-Agent": "DataWeave/2.0",
                                             Accept: "text/html, image/gif, image/jpeg, *; q=.2, */*; q=.2",
                                             "Cache-Control": "no-cache",
                                             Pragma: "no-cache",
                                             "content-length": "0"
                                           }
    ],
  server.stop() mustEqual true
] is Array
