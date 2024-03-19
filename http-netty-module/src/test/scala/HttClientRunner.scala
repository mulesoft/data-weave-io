import java.io.ByteArrayInputStream

import org.mule.weave.v2.module.http.netty.HttpAsyncClientService
import org.mule.weave.v2.module.http.service.HttpClientOptions

object HttClientRunner extends App{

  {
    val service = new HttpAsyncClientService()
    println("Google >>>>")

    val options = new HttpClientOptions.Builder()
      .withUrl("https://google.com")
      .withMethod("GET")
      .build()
    service.request(options).get()
    println("<<<< Google")
    println("Httpbin >>>")

    val options2 = new HttpClientOptions.Builder()
      .withUrl("http://httpbin.org/post")
      .withMethod("POST")
      .withHeader("a", "1")
      .withHeader("a", "2")
      .withBody(new ByteArrayInputStream("{}".getBytes("UTF-8")))
      .build()
    service.request(options2).get()
    println("<<< Httpbin")

    service.stop()
  }

}
