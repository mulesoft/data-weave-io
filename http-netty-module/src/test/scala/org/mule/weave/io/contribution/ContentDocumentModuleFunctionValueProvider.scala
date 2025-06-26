package org.mule.weave.io.contribution

import org.mule.weave.v2.module.javaplain.api.contribution.JavaPlainBasedFunction
import org.mule.weave.v2.module.javaplain.api.contribution.JavaPlainBasedFunctionProvider

class ContentDocumentModuleFunctionValueProvider extends JavaPlainBasedFunctionProvider {
  override def moduleFQNIdentifier: String = "org::mule::weave::io::http::ContentDocument"

  override def functions: Array[JavaPlainBasedFunction] = {
    Array(
      new CreateContentDocumentFunction()
    )
  }
}
