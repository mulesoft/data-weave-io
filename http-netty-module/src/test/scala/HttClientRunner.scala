import java.io.ByteArrayInputStream

import org.mule.weave.v2.module.http.netty.HttpAsyncClientService
import org.mule.weave.v2.module.http.service.HttpClientOptions

object HttClientRunner extends App{

  {
    val service = new HttpAsyncClientService()
    println("Google >>>>")
    service.request(HttpClientOptions("https://google.com","GET")).get()
    println("<<<< Google")
    println("Httpbin >>>")
    service.request(HttpClientOptions("http://httpbin.org/post","POST",Map("a" -> Seq("1","2")), body = Some(new ByteArrayInputStream("{}".getBytes("UTF-8"))))).get()
    println("<<< Httpbin")

    service.stop()
  }

}
