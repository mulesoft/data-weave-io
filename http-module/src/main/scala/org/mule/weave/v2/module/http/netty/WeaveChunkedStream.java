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
import java.io.PushbackInputStream;

/**
 * Implementation of {@link io.netty.handler.stream.ChunkedStream} which does not rely on {@link InputStream#available()}
 * returning accurate data. It will simply try to fill its buffer by reading several times from the stream and send it
 * whenever the buffer is full or the stream empty.
 */
public class WeaveChunkedStream implements ChunkedInput<ByteBuf> {

    static final int DEFAULT_CHUNK_SIZE = 8192;

    private final PushbackInputStream in;
    private final int chunkSize;
    private long offset;
    private boolean closed;

    /**
     * Creates a new instance that fetches data from the specified stream.
     */
    public WeaveChunkedStream(InputStream in) {
        this(in, DEFAULT_CHUNK_SIZE);
    }

    /**
     * Creates a new instance that fetches data from the specified stream.
     *
     * @param chunkSize the number of bytes to fetch on each
     *                  {@link #readChunk(ChannelHandlerContext)} call
     */
    public WeaveChunkedStream(InputStream in, int chunkSize) {
        if (in == null) {
            throw new NullPointerException("in");
        }
        if (chunkSize <= 0) {
            throw new IllegalArgumentException("chunkSize: " + chunkSize + " (expected: a positive integer)");
        }

        if (in instanceof PushbackInputStream) {
            this.in = (PushbackInputStream) in;
        } else {
            this.in = new PushbackInputStream(in);
        }
        this.chunkSize = chunkSize;
    }

    /**
     * Returns the number of transferred bytes.
     */
    public long transferredBytes() {
        return offset;
    }

    @Override
    public boolean isEndOfInput() throws Exception {
        if (closed) {
            return true;
        }

        int b = in.read();
        if (b < 0) {
            return true;
        } else {
            in.unread(b);
            return false;
        }
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

        boolean release = true;
        ByteBuf buffer = allocator.buffer(chunkSize);
        try {
            // transfer to buffer
            int read = 0;
            int total = 0;
            do {
                read = buffer.writeBytes(in, chunkSize - total);
                total += read;
            } while (read >= 0 && total < chunkSize);

            offset += total;
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
