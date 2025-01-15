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
    // Using binary request encoder
  f: sendRequest({method: "GET", url: 'http://$LOCALHOST/json-content-type'},
    { encode: (httpRequest) -> createBinaryHttpRequest(httpRequest, DEFAULT_SERIALIZATION_CONFIG) })
    then {
     status: $.status,
      contentType: $.contentType,
      headers: $.headers,
      body: $.body is Binary
  },
  // Using binary response decoder
  g: do {
    var httpRequest = { method: "GET", url: 'http://$LOCALHOST/json-content-type'}
    var binaryRequest = createBinaryHttpRequest(httpRequest, DEFAULT_SERIALIZATION_CONFIG)
    var response = sendRequest(binaryRequest,
      { decode: (httpResponse) -> readHttpResponseBody(httpResponse, DEFAULT_SERIALIZATION_CONFIG)})
    ---
    {
      status: response.status,
      contentType: response.contentType,
      headers: response.headers,
      body: response.body
    }
  },
  // Using binary request encoder and  binary response decoder
  h: do {
    var response = sendRequest({ method: "GET", url: 'http://$LOCALHOST/json-content-type'},
      { encode: (httpRequest) -> createBinaryHttpRequest(httpRequest, DEFAULT_SERIALIZATION_CONFIG)},
      { decode: (httpResponse) -> readHttpResponseBody(httpResponse, DEFAULT_SERIALIZATION_CONFIG)})
    ---
    {
      status: response.status,
      contentType: response.contentType,
      headers: response.headers,
      body: response.body
    }
  },
  z: server.stop()
]

