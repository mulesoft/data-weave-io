package org.mule.weave.io.contribution

import org.mule.weave.v2.module.javaplain.api.contribution.JavaPlainBasedFunction
import org.mule.weave.v2.module.javaplain.api.contribution.JavaPlainBasedFunctionArgument
import org.mule.weave.v2.module.javaplain.api.contribution.JavaPlainBasedFunctionResult
import org.mule.weave.v2.module.javaplain.api.contribution.ServiceProvider

import java.io.IOException
import java.io.InputStream
import java.util

class CreateContentDocumentFunction extends JavaPlainBasedFunction {

  private val ARGUMENT: JavaPlainBasedFunctionArgument = new JavaPlainBasedFunctionArgument(){}

  override def functionName: String = "create"

  override def argument: JavaPlainBasedFunctionArgument = {
    ARGUMENT
  }

  override def call(args: Array[AnyRef], serviceProvider: ServiceProvider): JavaPlainBasedFunctionResult = {
    val name: String = args(0).asInstanceOf[String]
    val mediaTypeArgument: String = args(1).asInstanceOf[String]
    val content: InputStream = args(2).asInstanceOf[InputStream]
    val buffer: Array[Byte] = new Array[Byte](4 * 1024)
    var chunk: Int = 0
    var size: Long = 0
    try {
      chunk = content.read(buffer)
      while (chunk != -1) {
        size = size + chunk
        chunk = content.read(buffer)
      }
      val total = size
      new JavaPlainBasedFunctionResult() {
        override def value: AnyRef = {
          val map: util.Map[String, Object] = new util.HashMap[String, Object]
          map.put("id", "CONTENT-DOCUMENT-ID")
          map.put("name", name)
          map.put("mediaType", mediaTypeArgument)
          map.put("size", java.lang.Long.valueOf(total))
          map
        }
      }
    } catch {
      case e: IOException =>
        throw new RuntimeException(e)
    }
  }
}
