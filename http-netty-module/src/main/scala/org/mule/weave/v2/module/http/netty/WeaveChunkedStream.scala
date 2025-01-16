package org.mule.weave.v2.module.http.netty

import io.netty.buffer.ByteBuf
import io.netty.buffer.ByteBufAllocator
import io.netty.channel.ChannelHandlerContext
import io.netty.handler.stream.ChunkedInput
import org.mule.weave.v2.module.http.netty.WeaveChunkedStream.DEFAULT_CHUNK_SIZE

import java.io.InputStream

class WeaveChunkedStream(in: InputStream, size: Long, chunkSize: Int = DEFAULT_CHUNK_SIZE) extends ChunkedInput[ByteBuf] {
  private var offset: Long = 0L
  private var closed: Boolean = _

  override def isEndOfInput: Boolean = {
    if (closed) {
      true
    } else {
      offset >= size
    }
  }

  override def close(): Unit = {
    closed = true
    in.close()
  }

  override def readChunk(ctx: ChannelHandlerContext): ByteBuf = {
    readChunk(ctx.alloc())
  }

  override def readChunk(allocator: ByteBufAllocator): ByteBuf = {
    if (isEndOfInput) {
      return null
    }

    val availableBytes = in.available
    var chunkSize = 0
    if (availableBytes <= 0) {
      chunkSize = this.chunkSize
    } else {
      chunkSize = Math.min(this.chunkSize, in.available)
    }

    var release = true
    val buffer = allocator.buffer(chunkSize)
    try {
      // transfer to buffer
      offset += buffer.writeBytes(in, chunkSize)
      release = false
      buffer
    } finally {
      if (release) {
        buffer.release
      }
    }
  }

  override def length(): Long = {
    -1
  }

  override def progress(): Long = {
    offset
  }
}

object WeaveChunkedStream {

  val DEFAULT_CHUNK_SIZE: Int = 8192

  def apply(in: InputStream, size: Long, chunkSize: Int = DEFAULT_CHUNK_SIZE): WeaveChunkedStream = new WeaveChunkedStream(in, size, chunkSize)
}