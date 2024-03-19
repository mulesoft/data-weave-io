package org.mule.weave.v2.module.http.functions

import org.mule.weave.v2.core.functions.SecureBinaryFunctionValue
import org.mule.weave.v2.model.EvaluationContext
import org.mule.weave.v2.model.service.WeaveRuntimePrivilege
import org.mule.weave.v2.model.structure.ObjectSeq
import org.mule.weave.v2.model.types.ObjectType
import org.mule.weave.v2.model.values.ObjectValue
import org.mule.weave.v2.model.values.Value
import org.mule.weave.v2.model.values.ValueProvider
import org.mule.weave.v2.module.http.functions.exceptions.InvalidUrlException
import org.mule.weave.v2.module.http.functions.exceptions.UrlConnectionException
import org.mule.weave.v2.module.http.functions.utils.HttpClientConfigurationConverter
import org.mule.weave.v2.module.http.functions.utils.HttpClientRequestConverter
import org.mule.weave.v2.module.http.functions.utils.HttpClientResponseConverter
import org.mule.weave.v2.module.http.service.HttpClientService
import org.mule.weave.v2.parser.exception.WeaveRuntimeException
import org.mule.weave.v2.parser.location.UnknownLocation

import java.net.ConnectException
import java.net.UnknownHostException

class HttpSendRequestFunction extends SecureBinaryFunctionValue {

  override val requiredPrivilege: WeaveRuntimePrivilege = HttpWeaveRuntimePrivilege.HTTP_CLIENT

  override val L: ObjectType = ObjectType

  override val R: ObjectType = ObjectType

  override val rightDefaultValue: Option[ValueProvider] = Some(new ValueProvider {
    override def value()(implicit ctx: EvaluationContext): Value[_] = {
      ObjectValue.empty
    }
  })

  override protected def onSecureExecution(leftValue: L.V, rightValue: R.V)(implicit ctx: EvaluationContext): Value[_] = {
    val request = leftValue.evaluate.materialize()
    val configuration: ObjectSeq = rightValue.evaluate.materialize()

    val httpRequest = HttpClientRequestConverter().convert(request)
    val httpClientConfiguration = HttpClientConfigurationConverter().convert(configuration)

    val httpClientService: HttpClientService = ctx.serviceManager
      .lookupCustomService(classOf[HttpClientService])
      .getOrElse(throw new WeaveRuntimeException("HttpClientService was not registered", UnknownLocation))

    try {
      val httpResponse = httpClientService.sendRequest(httpRequest, httpClientConfiguration)
      val readerProperties = Map.empty[String, Any]
      HttpClientResponseConverter().convert(httpResponse, readerProperties)
    } catch {
      case uh: UnknownHostException =>
        throw new UrlConnectionException(httpRequest.getUrl, uh.getMessage, this.location())
      case _: IllegalArgumentException =>
        throw new InvalidUrlException(httpRequest.getUrl, this.location())
      case ce: ConnectException =>
        throw new UrlConnectionException(httpRequest.getUrl, ce.getMessage, this.location())
    }
  }
}
