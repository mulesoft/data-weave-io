
package org.mule.weave.v2.module.http.functions

import org.mule.weave.v2.core.functions.SecureTernaryFunctionValue
import org.mule.weave.v2.model.EvaluationContext
import org.mule.weave.v2.model.service.WeaveRuntimePrivilege
import org.mule.weave.v2.model.structure.ObjectSeq
import org.mule.weave.v2.model.types._
import org.mule.weave.v2.model.values._
import org.mule.weave.v2.model.values.wrappers.LazyValue
import org.mule.weave.v2.module.http.functions.exceptions.InvalidUrlException
import org.mule.weave.v2.module.http.functions.exceptions.UrlConnectionException
import org.mule.weave.v2.module.http.functions.utils.HttpClientConfigurationConverter
import org.mule.weave.v2.module.http.functions.utils.HttpClientLoggingUtil
import org.mule.weave.v2.module.http.functions.utils.HttpClientRequestConverter
import org.mule.weave.v2.module.http.functions.utils.HttpClientResponseConverter
import org.mule.weave.v2.module.http.functions.utils.StopWatch
import org.mule.weave.v2.module.http.service.HttpClientService
import org.mule.weave.v2.parser.exception.WeaveRuntimeException
import org.mule.weave.v2.parser.location.UnknownLocation

import java.net.ConnectException
import java.net.UnknownHostException
import java.util.concurrent.ExecutionException

class HttpRequestFunction extends SecureTernaryFunctionValue {

  override val requiredPrivilege: WeaveRuntimePrivilege = HttpWeaveRuntimePrivilege.HTTP_CLIENT

  override val First: ObjectType = ObjectType

  override val Second: ObjectType = ObjectType

  override val Third: ObjectType = ObjectType

  override protected def onSecureExecution(requestValue: Value[ObjectSeq], requestConfigurationValue: Value[ObjectSeq], clientConfigurationValue: Value[ObjectSeq])(implicit ctx: EvaluationContext): Value[_] = {
    val stopWatch = StopWatch(on = true)

    val requestObjectSeq = requestValue.evaluate.materialize()
    val requestConfigurationObjectSeq = requestConfigurationValue.evaluate.materialize()
    val clientConfigurationObjectSeq = clientConfigurationValue.evaluate.materialize()

    val requestConfig = HttpClientRequestConfig.parse(requestConfigurationObjectSeq)
    val request = HttpClientRequestConverter(requestObjectSeq, requestConfig, this).convert()
    val clientConfiguration = HttpClientConfigurationConverter(clientConfigurationObjectSeq).convert()

    val httpClientService = ctx.serviceManager
      .lookupCustomService(classOf[HttpClientService])
      .getOrElse(throw new WeaveRuntimeException("HttpClientService was not registered", UnknownLocation))

    try {
      if (ctx.serviceManager.loggingService.isInfoEnabled()) {
        val buffer = HttpClientLoggingUtil.appendClientConfiguration(new StringBuilder(), clientConfiguration)
        ctx.serviceManager.loggingService.logInfo(s"Searching HTTP Client for configuration: [${buffer.toString()}]")
      }

      val client = httpClientService.getClient(clientConfiguration)

      if (ctx.serviceManager.loggingService.isInfoEnabled()) {
        ctx.serviceManager.loggingService.logInfo("Found HTTP Client instance")
      }

      if (ctx.serviceManager.loggingService.isInfoEnabled()) {
        val buffer = HttpClientLoggingUtil.appendRequest(new StringBuilder(), request)
        ctx.serviceManager.loggingService.logInfo(s"Sending request: [${buffer.toString()}]")
      }

      val httpResponse = client.request(request)

      new LazyValue({
        try {
          if (ctx.serviceManager.loggingService.isInfoEnabled()) {
            val buffer = HttpClientLoggingUtil.appendResponse(new StringBuilder(), httpResponse)
            ctx.serviceManager.loggingService.logInfo(s"Received response: [${buffer.toString()}]")
          }
          HttpClientResponseConverter(httpResponse, stopWatch).convert()
        } catch {
          case ee: ExecutionException =>
            ee.getCause match {
              case ce: Exception =>
                throw new UrlConnectionException(request.getUrl, ce.getMessage, this.location())
            }
            throw new WeaveRuntimeException(ee.getLocalizedMessage, this.location())
        }
      }, this)
    } catch {
      case uh: UnknownHostException =>
        throw new UrlConnectionException(request.getUrl, uh.getMessage, this.location())
      case _: IllegalArgumentException =>
        throw new InvalidUrlException(request.getUrl, this.location())
      case ce: ConnectException =>
        throw new UrlConnectionException(request.getUrl, ce.getMessage, this.location())
      case ee: ExecutionException =>
        ee.getCause match {
          case ce: Exception =>
            throw new UrlConnectionException(request.getUrl, ce.getMessage, this.location())
        }
    }
  }
}
