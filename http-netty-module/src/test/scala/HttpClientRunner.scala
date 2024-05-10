import java.io.ByteArrayInputStream
import org.mule.weave.v2.module.http.netty.NettyHttpClientService
import org.mule.weave.v2.module.http.service.HttpClientConfiguration
import org.mule.weave.v2.module.http.service.HttpClientRequest

object HttpClientRunner extends App {
    val service = new NettyHttpClientService()
    val config = new HttpClientConfiguration.Builder().build()
    val client = service.getClient(config)
    println("Google >>>>")
    client.request(new HttpClientRequest.Builder()
      .setMethod("GET")
      .setUrl("https://google.com")
      .build())
    println("<<<< Google")


    println("Httpbin >>>")
    client.request(new HttpClientRequest.Builder()
      .setMethod("POST")
      .setUrl("http://httpbin.org/post")
      .addQueryParam("a", "1")
      .addQueryParam("a", "2")
      .setBody(new ByteArrayInputStream("{}".getBytes("UTF-8")))
      .build())
    println("<<< Httpbin")

    service.stop()
}
