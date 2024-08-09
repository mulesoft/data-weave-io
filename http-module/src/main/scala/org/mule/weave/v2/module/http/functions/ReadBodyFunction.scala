package org.mule.weave.v2.module.http.functions

import org.mule.weave.v2.core.functions.TernaryFunctionValue
import org.mule.weave.v2.core.util.CharsetUtil
import org.mule.weave.v2.model.EvaluationContext
import org.mule.weave.v2.model.types.BinaryType
import org.mule.weave.v2.model.types.ObjectType
import org.mule.weave.v2.model.types.StringType
import org.mule.weave.v2.model.values.ObjectValue
import org.mule.weave.v2.model.values.Value
import org.mule.weave.v2.model.values.ValueProvider
import org.mule.weave.v2.module.http.functions.utils.MimeTypeUtil
import org.mule.weave.v2.module.http.values.HttpClientResponseBodyValue
import org.mule.weave.v2.module.reader.SourceProvider
import org.mule.weave.v2.parser.location.SimpleLocation

import java.nio.charset.Charset

class ReadBodyFunction extends TernaryFunctionValue {

  override val First = StringType
  override val Second = BinaryType
  override val Third = ObjectType

  override val thirdDefaultValue: Option[ValueProvider] = Some(ValueProvider(ObjectValue.empty))

  override protected def doExecute(mimeTypeValue: First.V, bodyValue: Second.V, readerPropertiesValue: Third.V)(implicit ctx: EvaluationContext): Value[_] = {
    val mimeTypeValueString = mimeTypeValue.evaluate.toString
    val readerProperties = ObjectType.coerce(readerPropertiesValue).evaluate(ctx)

    // Extract charset
    val mimeType = MimeTypeUtil.fromSimpleString(mimeTypeValueString)
    val charset = mimeType match {
      case Some(mimeType) =>
        mimeType.parameters.get("charset")
          .map(charsetName => Charset.forName(charsetName))
          .getOrElse(CharsetUtil.defaultCharset)
      case _ =>
        CharsetUtil.defaultCharset
    }

    HttpClientResponseBodyValue(mimeTypeValueString, SourceProvider(bodyValue.evaluate, charset, mimeType), readerProperties, SimpleLocation("response.body"))
  }
}
