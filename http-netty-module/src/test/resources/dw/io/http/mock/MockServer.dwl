%dw 2.0

import * from dw::io::http::Types

type RoutingHandler = {
  method: String,
  path: String,
  statusCode: Number,
  headers: HttpHeaders,
  response: String
}

type ServerConfig = {
  port: Number,
  handlers: Array<RoutingHandler>
}

type StartedServer = {
  started: Boolean,
  id: String
}

fun startServer(config: ServerConfig): StartedServer = do {
  fun internalStartServer(config: ServerConfig, id: String): Boolean = native("MockServer::MockServerStartFunction")

  var id = uuid()
  var started = internalStartServer(config, id)
  ---
  {
    started: started,
    id: id
  }
}

fun stopServer(id: String): Boolean = native("MockServer::MockServerStopFunction")

