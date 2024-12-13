%dw 2.0
import * from dw::io::http::Server
import * from dw::io::http::Client
import * from dw::io::http::Types

var serverConfig = { host: "localhost", port: dw::io::http::utils::Port::freePort(), normalizeResponseHeaders: false }
var LOCALHOST = '$(serverConfig.host):$(serverConfig.port)'
var server = api(serverConfig,
  {
    "/insensitive-content-type": {
      "GET": (req) -> {
        responseStatus: 200,
        headers: {
          "COntent-TYPE": "application/json"
        }
      }
    },
    "/content-type": {
      "GET": (req) -> {
        responseStatus: 200,
        headers: {
          "Content-Type": "application/xml"
        }
      }
    },
    "/non-content-type": {
      "GET": (req) -> {
        responseStatus: 200
      }
    },
    "/json-content-type": {
      "GET": (req) -> {
        responseStatus: 200,
          headers: {
              "Content-Type": "application/json"
          },
          body: '{"name": "test"}' as Binary
      }
    }
  })
---
[
  a: sendRequest({method: "GET", url: 'http://$LOCALHOST/insensitive-content-type'})
    then {
      status: $.status,
      contentType: $.contentType,
      headers: $.headers
    },
  b: sendRequest({method: "GET", url: 'http://$LOCALHOST/content-type'})
    then {
      status: $.status,
      contentType: $.contentType,
      headers: $.headers
    },
  c: sendRequest({method: "GET", url: 'http://$LOCALHOST/non-content-type'})
    then {
      status: $.status,
      contentType: $.contentType,
      headers: $.headers
    },
    // Content-Type no body should return 'null' body
  d: sendRequestAndReadResponse({method: "GET", url: 'http://$LOCALHOST/content-type'})
    then {
      status: $.status,
      contentType: $.contentType,
      headers: $.headers,
      body: $.body
    },
  e: sendRequestAndReadResponse({method: "GET", url: 'http://$LOCALHOST/json-content-type'})
    then {
      status: $.status,
      contentType: $.contentType,
      headers: $.headers,
      body: $.body
    },
  f: sendRequestAndReadRawResponse({method: "GET", url: 'http://$LOCALHOST/json-content-type'})
    then {
      status: $.status,
      contentType: $.contentType,
      headers: $.headers,
      body: $.body is Binary
    },
  g: server.stop()
]

