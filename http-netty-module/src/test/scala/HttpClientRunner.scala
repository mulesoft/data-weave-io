import java.io.ByteArrayInputStream
import org.mule.weave.v2.module.http.netty.NettyHttpClientService
import org.mule.weave.v2.module.http.service.HttpClientConfiguration
import org.mule.weave.v2.module.http.service.HttpClientRequest

object HttpClientRunner extends App {
    val service = new NettyHttpClientService()
    val config = new HttpClientConfiguration.Builder().withId("runner").build()
    val client = service.getClient(config)
    println("Google >>>>")
    client.request(new HttpClientRequest.Builder()
      .withMethod("GET")
      .withUrl("https://google.com")
      .build()).get()
    println("<<<< Google")


    println("Httpbin >>>")
    client.request(new HttpClientRequest.Builder()
      .withMethod("POST")
      .withUrl("http://httpbin.org/post")
      .withQueryParam("a", "1")
      .withQueryParam("a", "2")
      .withBody(new ByteArrayInputStream("{}".getBytes("UTF-8")))
      .build()).get()
    println("<<< Httpbin")

    service.stop()
}
