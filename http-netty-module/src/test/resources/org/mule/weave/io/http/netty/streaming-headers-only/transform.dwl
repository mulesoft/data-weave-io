%dw 2.0
output application/json

import * from dw::io::http::Client
import * from dw::io::http::Server
import * from dw::io::http::utils::Port
import * from dw::Client

var serverConfig = { host: "localhost", port: dw::io::http::utils::Port::freePort()}
var LOCALHOST = '$(serverConfig.host):$(serverConfig.port)'

// Create a large response body (1MB of data)
var server = api(serverConfig,
  {
    "/large-response": {
      "GET": (req) -> {
        responseStatus: 200,
        headers: {
          "Content-Type": "text/plain",
          "X-Custom-Header": "test-value"
        },
        body: failBinary()
      }
    }
  })

---
{
  // Make a request that only needs headers
  response: do {
    var response = sendRequest({method: "GET", url: 'http://$LOCALHOST/large-response'}, {
                                                                                           followRedirects: false,
                                                                                           readTimeout: 60000,
                                                                                           requestTimeout: 60000,
                                                                                           streamResponse: true
                                                                                         }, DEFAULT_SERIALIZATION_CONFIG)
    ---
    {
      status: response.status,
      headers: response.headers,
      // Verify we can access headers
      customHeader: response.headers["X-Custom-Header"]
    }
  },
  // Cleanup
  cleanup: server.stop()
} 