import * from dw::io::http::Server
import * from dw::io::http::Client
import * from dw::io::http::Types

var serverConfig = { host: "localhost", port: dw::io::http::utils::Port::freePort() }
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
---
[
  post('http://$LOCALHOST/test', {name: 'Agustin'})
    then {
      name: ($).body.name ,
      headers: $.headers."Content-Type",
      status: ($).status ,
      raw: ($).body.^raw
  },

  get(url: 'http://$LOCALHOST/test')
    then {
      name: ($).body.name,
  },

  post('http://$LOCALHOST/testXml', {name: 'Agustin'})
    then {
     name: ($).body.name,
     contentType: $.headers."Content-Type",
     status: ($).status
  },

  get('http://$LOCALHOST/testXml')
    then {
     name: ($).body.name,
     contentType : $.headers."Content-Type"
    },

  get('http://$LOCALHOST/properties?some=query&other=value',{("X-Custom"): "headerValue"})
    then {
      method: ($).body.method ,
      path: ($).body.path ,
      params: ($).body.params,
      headers: ($).body.headers - "host" //We remove host as it changes based on the port
    },
  server.stop()
]
