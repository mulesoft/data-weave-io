package org.mule.weave.v2.module.http.functions

import java.io.File
import java.nio.file.Paths

import io.undertow.server.handlers.resource.ClassPathResourceManager
import io.undertow.server.handlers.resource.PathResourceManager
import io.undertow.server.handlers.resource.ResourceManager
import io.undertow.util.MimeMappings
import org.mule.weave.v2.core.functions.UnaryFunctionValue
import org.mule.weave.v2.model.EvaluationContext
import org.mule.weave.v2.model.structure.KeyValuePair
import org.mule.weave.v2.model.types.StringType
import org.mule.weave.v2.model.values.BinaryValue
import org.mule.weave.v2.model.values.KeyValue
import org.mule.weave.v2.model.values.ObjectValue
import org.mule.weave.v2.model.values.StringValue
import org.mule.weave.v2.model.values.Value
import org.mule.weave.v2.model.values.wrappers.LazyValue
import org.mule.weave.v2.module.http.HttpHeader
import org.mule.weave.v2.module.http.functions.ServeResourceFunction._
/**
  * This function returns the HttpResponse with the binary content at the provided path
  * and corresponding headers set (Content-Type, content-length, etc)
  */
class ServeResourceFunction extends UnaryFunctionValue {

  val resourceManager: ResourceManager = {
    val customBasePath = System.getenv(RESOURCE_BASE_PATH)
    if (customBasePath != null) {
      new PathResourceManager(Paths.get(customBasePath), 100)
    } else {
      //By default use the classpath resource manager
      val defaultBasePath = s"dw${File.separator}playground${File.separator}www"
      new ClassPathResourceManager(getClass.getClassLoader, defaultBasePath)
    }
  }
  override val R = StringType

  override def doExecute(pathValue: R.V)(implicit ctx: EvaluationContext): Value[_] = {
    val path = StringType.coerce(pathValue).evaluate
    val resource = resourceManager.getResource(path)
    if (resource != null) {
      val bodyKvp = KeyValuePair(KeyValue(HttpServerFunction.BODY_KEY_NAME), LazyValue(BinaryValue(resource.getUrl.openStream())))
      val headersBuilder = Seq.newBuilder[KeyValuePair]
      val contentType = resource.getContentType(MimeMappings.DEFAULT)
      headersBuilder.+=(KeyValuePair(KeyValue(HttpHeader.CONTENT_TYPE_HEADER), StringValue(if (contentType != null) contentType else "application/octet-stream")))
      headersBuilder.+=(KeyValuePair(KeyValue(HttpHeader.CONTENT_LENGTH_HEADER), StringValue(resource.getContentLength.toString)))

      val lastModifiedString = resource.getLastModifiedString
      if (lastModifiedString != null) {
        headersBuilder.+=(KeyValuePair(KeyValue(HttpHeader.LAST_MODIFIED_HEADER), StringValue(lastModifiedString)))
      }

      if (resource.getETag != null && resource.getETag.getTag != null) {
        headersBuilder.+=(KeyValuePair(KeyValue(HttpHeader.ETAG_HEADER), StringValue(resource.getETag.getTag)))
      }
      val headersKvp = KeyValuePair(KeyValue(HttpServerFunction.HEADERS_KEY_NAME), ObjectValue(headersBuilder.result()))
      ObjectValue(Seq(bodyKvp, headersKvp))
    } else {
      ObjectValue.empty
    }
  }
}

object ServeResourceFunction {
  val RESOURCE_BASE_PATH = "dw.resource.base.path"
  val defaultBasePath: String = System.getProperty("user.dir")

}
