package org.mule.weave.io.http.netty

import org.mule.weave.v2.core.functions.EmptyFunctionValue
import org.mule.weave.v2.model.EvaluationContext
import org.mule.weave.v2.model.values.BinaryValue
import org.mule.weave.v2.model.values.Value

import java.io.InputStream

class FailBinaryFunction extends EmptyFunctionValue {


  override protected def doExecute()(implicit ctx: EvaluationContext): Value[_] = {
    BinaryValue(new FailingInputStream())
  }

  class FailingInputStream extends InputStream {
    private var bytesRead = 0
    private val failAfterBytes = 1.5 * 1024 * 1024 // 1.5MB

    override def read(): Int = {
      if (bytesRead >= failAfterBytes) {
        throw new RuntimeException("Stream failed after reading 1.5MB")
      }
      bytesRead += 1
      'x'.toInt
    }

    override def read(b: Array[Byte], off: Int, len: Int): Int = {
      if (bytesRead >= failAfterBytes) {
        throw new RuntimeException("Stream failed after reading 1.5MB")
      }
      val bytesToRead = Math.min(len, (failAfterBytes - bytesRead).toInt)
      for (i <- 0 until bytesToRead) {
        b(off + i) = 'x'.toByte
      }
      bytesRead += bytesToRead
      bytesToRead
    }
  }
}
