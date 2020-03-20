package org.mule.weave.v2.module.http.client

import org.mule.weave.v2.model.ServiceRegistration
import org.mule.weave.v2.module.http.service.HttpClientOptions
import org.mule.weave.v2.module.http.service.HttpClientResult
import org.mule.weave.v2.module.http.service.HttpClientService

class JavaHttpClientService extends HttpClientService {
  override def request(config: HttpClientOptions): HttpClientResult = {
    new HttpRequest(config).doConnection()
  }
}

class HttpClientServiceRegistration extends ServiceRegistration[HttpClientService] {
  override def service = classOf[HttpClientService]

  override def implementation = new JavaHttpClientService()
}
