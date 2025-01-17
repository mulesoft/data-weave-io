package org.mule.weave.io.http.netty

import io.netty.buffer.PooledByteBufAllocator
import org.mule.weave.v2.module.http.netty.WeaveChunkedStream
import org.scalatest.freespec.AnyFreeSpec
import org.scalatest.matchers.should.Matchers

import java.io.ByteArrayInputStream
import java.nio.charset.StandardCharsets

class WeaveChunkedStreamTest extends AnyFreeSpec with Matchers {

  "WeaveChunkedStream" - {
    val byteBufAllocator = new PooledByteBufAllocator()
    val message = "My message"
    val bytes = message.getBytes

    "`isEndOfInput` should work" in {
      val is = new ByteArrayInputStream(bytes)
      val stream = WeaveChunkedStream(is, bytes.length)

      val isEndOfInput = stream.isEndOfInput

      isEndOfInput shouldBe false
    }

    "`length` should work" in {
      val is = new ByteArrayInputStream(bytes)
      val stream = WeaveChunkedStream(is, bytes.length)

      val length = stream.length()

      length shouldBe -1
    }

    "`close` should work" in {
      val is = new ByteArrayInputStream(bytes)
      val stream = WeaveChunkedStream(is, bytes.length)

      stream.close()

      stream.isEndOfInput shouldBe true
    }

    "`readChunk` should work" in {
      val is = new ByteArrayInputStream(bytes)
      val stream = WeaveChunkedStream(is, bytes.length)

      val byteBuffer = stream.readChunk(byteBufAllocator)

      byteBuffer should not be null
      val result = byteBuffer.toString(StandardCharsets.UTF_8)
      result shouldBe message
    }

    "`readChunk` should work form empty `InputStream`" in {
      val emptyIs = new ByteArrayInputStream(new Array[Byte](0))
      val stream = WeaveChunkedStream(emptyIs, 0)

      val byteBuffer = stream.readChunk(byteBufAllocator)

      byteBuffer shouldBe null
    }

    "`readChunk` should stream by chunk size" in {
      val is = new ByteArrayInputStream(bytes)
      val stream = WeaveChunkedStream(is, bytes.length, 1)
      val byteBuffer = stream.readChunk(byteBufAllocator)

      byteBuffer should not be null
      val result = byteBuffer.toString(StandardCharsets.UTF_8)
      result shouldBe "M"
    }
  }
}
