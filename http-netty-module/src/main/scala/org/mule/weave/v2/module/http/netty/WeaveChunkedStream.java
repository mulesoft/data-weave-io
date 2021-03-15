/*
 * Copyright (c) MuleSoft, Inc.  All rights reserved.  http://www.mulesoft.com
 * The software in this package is published under the terms of the CPAL v1.0
 * license, a copy of which has been included with this distribution in the
 * LICENSE.txt file.
 */
package org.mule.weave.v2.module.http.netty;

import io.netty.buffer.ByteBuf;
import io.netty.buffer.ByteBufAllocator;
import io.netty.channel.ChannelHandlerContext;
import io.netty.handler.stream.ChunkedInput;

import java.io.InputStream;

/**
 * Implementation of {@link io.netty.handler.stream.ChunkedStream} which does not rely on {@link InputStream#available()}
 * returning accurate data. It will simply try to fill its buffer by reading several times from the stream and send it
 * whenever the buffer is full or the stream empty.
 */
public class WeaveChunkedStream implements ChunkedInput<ByteBuf> {

    static final int DEFAULT_CHUNK_SIZE = 8192;

    private final InputStream in;
    private final int chunkSize = DEFAULT_CHUNK_SIZE;
    private long offset;
    private boolean closed;
    private long size;

    public WeaveChunkedStream(InputStream in, long size) {
        this.in = in;
        this.size = size;
    }

    public long transferredBytes() {
        return offset;
    }

    @Override
    public boolean isEndOfInput() throws Exception {
        if (closed) {
            return true;
        }
        return offset >= size;
    }

    @Override
    public void close() throws Exception {
        closed = true;
        in.close();
    }

    @Deprecated
    @Override
    public ByteBuf readChunk(ChannelHandlerContext ctx) throws Exception {
        return readChunk(ctx.alloc());
    }

    @Override
    public ByteBuf readChunk(ByteBufAllocator allocator) throws Exception {
        if (isEndOfInput()) {
            return null;
        }

        final int availableBytes = in.available();
        final int chunkSize;
        if (availableBytes <= 0) {
            chunkSize = this.chunkSize;
        } else {
            chunkSize = Math.min(this.chunkSize, in.available());
        }

        boolean release = true;
        ByteBuf buffer = allocator.buffer(chunkSize);
        try {
            // transfer to buffer
            offset += buffer.writeBytes(in, chunkSize);
            release = false;
            return buffer;
        } finally {
            if (release) {
                buffer.release();
            }
        }
    }

    @Override
    public long length() {
        return -1;
    }

    @Override
    public long progress() {
        return offset;
    }
}
