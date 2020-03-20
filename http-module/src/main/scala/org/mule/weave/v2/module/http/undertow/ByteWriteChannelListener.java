package org.mule.weave.v2.module.http.undertow;


import io.undertow.Undertow;
import io.undertow.UndertowLogger;
import io.undertow.server.handlers.resource.PathResourceManager;
import org.xnio.ChannelListener;
import org.xnio.ChannelListeners;
import org.xnio.IoUtils;
import org.xnio.channels.StreamSinkChannel;

import java.io.IOException;
import java.nio.ByteBuffer;
import java.nio.file.Paths;

import static io.undertow.Handlers.resource;


/**
 * A simple write listener that can be used to write out the contents of a String. When the string is written
 * out it closes the channel.
 * <p>
 * This should not be added directly to the channel, instead {@link #setup(org.xnio.channels.StreamSinkChannel)}
 * should be called, which will attempt a write, and only add the listener if required.
 */
public class ByteWriteChannelListener implements ChannelListener<StreamSinkChannel> {

    private final ByteBuffer buffer;

    public ByteWriteChannelListener(final ByteBuffer buffer) {
        this.buffer = buffer;
    }

    public void setup(final StreamSinkChannel channel) {
        try {
            int c;
            do {
                c = channel.write(buffer);
            } while (buffer.hasRemaining() && c > 0);
            if (buffer.hasRemaining()) {
                channel.getWriteSetter().set(this);
                channel.resumeWrites();
            } else {
                writeDone(channel);
            }
        } catch (IOException e) {
            handleError(channel, e);
        }
    }

    protected void handleError(StreamSinkChannel channel, IOException e) {
        UndertowLogger.REQUEST_IO_LOGGER.ioException(e);
        IoUtils.safeClose(channel);
    }

    @Override
    public void handleEvent(final StreamSinkChannel channel) {
        try {
            int c;
            do {
                c = channel.write(buffer);
            } while (buffer.hasRemaining() && c > 0);
            if (buffer.hasRemaining()) {
                channel.resumeWrites();
                return;
            } else {
                writeDone(channel);
            }
        } catch (IOException e) {
            handleError(channel, e);
        }
    }

    public boolean hasRemaining() {
        return buffer.hasRemaining();
    }

    protected void writeDone(final StreamSinkChannel channel) {
        try {
            channel.shutdownWrites();
            if (!channel.flush()) {
                ChannelListener<StreamSinkChannel> listener = ChannelListeners.flushingChannelListener(o -> IoUtils.safeClose(channel), ChannelListeners.closingChannelExceptionHandler());
                channel.getWriteSetter().set(listener);
                channel.resumeWrites();

            }
        } catch (IOException e) {
            handleError(channel, e);
        }
    }

    public static void main(final String[] args) {
        Undertow server = Undertow.builder()
                .addHttpListener(8080, "localhost")
                .setHandler(resource(new PathResourceManager(Paths.get(System.getProperty("user.home")), 100))
                        .setDirectoryListingEnabled(true))
                .build();
        server.start();
    }
}
