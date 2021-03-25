import org.mule.weave.v2.module.http.netty.NettyHttpServerService
import org.mule.weave.v2.module.http.service.{HttpServerConfig, HttpServerResponse}

import java.io.ByteArrayInputStream

object HttpServerRunner extends App {

  val serverService = new NettyHttpServerService()
  val config = HttpServerConfig(
    port = 8081,
    host = "localhost",
    maxContentLength = 65536
  )
  serverService.server(config, (request) => {
    HttpServerResponse(
      body = new ByteArrayInputStream("foo".getBytes("UTF-8")),
      headers = Map(),
      closeCallBack = () => {},
    )
  })
}
