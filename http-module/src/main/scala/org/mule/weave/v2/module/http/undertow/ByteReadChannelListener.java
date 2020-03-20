package org.mule.weave.v2.module.http.undertow;

import io.undertow.connector.ByteBufferPool;
import io.undertow.connector.PooledByteBuffer;
import io.undertow.server.XnioByteBufferPool;
import org.xnio.ChannelListener;
import org.xnio.IoUtils;
import org.xnio.Pool;
import org.xnio.channels.StreamSourceChannel;

import java.io.ByteArrayOutputStream;
import java.io.IOException;
import java.nio.ByteBuffer;

/**
 * Simple utility class for reading a byte array
 */
public abstract class ByteReadChannelListener implements ChannelListener<StreamSourceChannel> {

    private final ByteArrayOutputStream string = new ByteArrayOutputStream();
    private final ByteBufferPool bufferPool;

    public ByteReadChannelListener(final ByteBufferPool bufferPool) {
        this.bufferPool = bufferPool;
    }

    @Deprecated
    public ByteReadChannelListener(final Pool<ByteBuffer> bufferPool) {
        this.bufferPool = new XnioByteBufferPool(bufferPool);
    }

    public void setup(final StreamSourceChannel channel) {
        PooledByteBuffer resource = bufferPool.allocate();
        ByteBuffer buffer = resource.getBuffer();
        try {
            int r = 0;
            do {
                r = channel.read(buffer);
                if (r == 0) {
                    channel.getReadSetter().set(this);
                    channel.resumeReads();
                } else if (r == -1) {
                    bytesDone(string.toByteArray());
                    IoUtils.safeClose(channel);
                } else {
                    buffer.flip();
                    byte[] arr = new byte[buffer.remaining()];
                    buffer.get(arr);
                    string.write(arr);
                }
            } while (r > 0);
        } catch (IOException e) {
            error(e);
        } finally {
            resource.close();
        }
    }

    @Override
    public void handleEvent(final StreamSourceChannel channel) {
        PooledByteBuffer resource = bufferPool.allocate();
        ByteBuffer buffer = resource.getBuffer();
        try {
            int r = 0;
            do {
                r = channel.read(buffer);
                if (r == 0) {
                    return;
                } else if (r == -1) {
                    bytesDone(string.toByteArray());
                    IoUtils.safeClose(channel);
                } else {
                    buffer.flip();
                    byte[] arr = new byte[buffer.remaining()];
                    buffer.get(arr);
                    string.write(arr);
                }
            } while (r > 0);
        } catch (IOException e) {
            error(e);
        } finally {
            resource.close();
        }
    }

    protected abstract void bytesDone(byte[] string);

    protected abstract void error(IOException e);
}
