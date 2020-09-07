import * from dw::io::http::Server

var serverConfig = {host:"localhost", port: 8081, "Content-Type": "application/xml"}

fun main() =
api(serverConfig,
  {
    "/test": {
      GET: (request) -> {
         body: {
           name: "Mariano"
         }
      },
      POST: (request) -> {
        body: {
           name: request.body.name
        }
      }
    }
  }
)

