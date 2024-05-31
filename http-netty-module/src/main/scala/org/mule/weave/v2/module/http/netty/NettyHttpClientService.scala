package org.mule.weave.v2.module.http.netty

import org.mule.weave.v2.model.ServiceRegistration
import org.mule.weave.v2.module.http.service.HttpClient
import org.mule.weave.v2.module.http.service.HttpClientConfiguration
import org.mule.weave.v2.module.http.service.HttpClientService

class NettyHttpClientService extends HttpClientService {

  private lazy val httpClientRegistry = HttpClientRegistry()

  override def getClient(configuration: HttpClientConfiguration): HttpClient = {
    httpClientRegistry.get(configuration)
  }

  def stop(): Unit = {
    httpClientRegistry.cleanup()
  }
}

class HttpClientServiceRegistration extends ServiceRegistration[HttpClientService] {
  //Re-use same http client
  private val httpAsyncClientService = new NettyHttpClientService()

  override def service: Class[HttpClientService] = classOf[HttpClientService]

  override def implementation: NettyHttpClientService = {
    httpAsyncClientService
  }
}
