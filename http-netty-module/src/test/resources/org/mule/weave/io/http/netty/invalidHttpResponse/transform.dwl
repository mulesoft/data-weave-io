%dw 2.0

import * from dw::io::http::Client
import * from dw::io::http::mock::MockServer
import * from dw::io::http::utils::Port

output application/json

var config = {
  port: freePort(),
  handlers: [
    {
      method: "GET",
      path: "/invalid-json",
      statusCode: 200,
      headers: {
        "Content-Type": "application/json"
      },
      response: '{"name": "Mariano", "lastname": '
    }
 ]
}
var server = startServer(config)

---
[
  {
    response: do {
      var response = request({
       method: 'GET',
       url: 'http://localhost:$(config.port)/invalid-json',
      })
     ---
     {
       status: response.status,
       "Content-Type": response.headers."Content-Type",
       // raw: response.body.^raw
     }
   }
  },
  stopped: stopServer(server.id)
]