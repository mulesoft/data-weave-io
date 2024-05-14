package org.mule.weave.v2.module.http.service;

import java.util.Objects;

/**
 * Configuration component that specifies how an {@link HttpClient} should be created.
 * Instances can only be obtained through an {@link HttpClientConfiguration.Builder}.
 */
public class HttpClientConfiguration {
    private final int connectionTimeout;
    private final boolean compressionEnforced;
    private final TlsConfiguration tlsConfiguration;

    private HttpClientConfiguration(int connectionTimeout, boolean compressionEnforced,
                                   TlsConfiguration tlsConfiguration) {
        this.connectionTimeout = connectionTimeout;
        this.compressionEnforced = compressionEnforced;
        this.tlsConfiguration = tlsConfiguration;
    }

    /**
     * @return the maximum time in millisecond an {@link HttpClient} can wait when connecting to a remote host.
     */
    public int getConnectionTimeout() {
        return connectionTimeout;
    }

    /**
     * Is HTTP `Accept-Encoding` HTTP header enforced.
     *
     * @return {@code true} if the `Accept-Encoding: gzip, deflate` HTTP header will be sent to each request.
     */
    public boolean isCompressionEnforced() {
        return compressionEnforced;
    }

    /**
     * @return The TLS related data.
     */
    public TlsConfiguration getTlsConfiguration() {
        return tlsConfiguration;
    }


    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;

        HttpClientConfiguration that = (HttpClientConfiguration) o;
        return connectionTimeout == that.connectionTimeout
                && compressionEnforced == that.compressionEnforced
                && Objects.equals(tlsConfiguration, that.tlsConfiguration);
    }

    @Override
    public int hashCode() {
        int result = connectionTimeout;
        result = 31 * result + Boolean.hashCode(compressionEnforced);
        result = 31 * result + Objects.hashCode(tlsConfiguration);
        return result;
    }

    /**
     * Builder of {@link HttpClientConfiguration}s. At the very least, an id must be provided.
     */
    public static final class Builder {
        private int connectionTimeout = 5000;
        private boolean compressionEnforced = false;
        private TlsConfiguration tlsConfiguration;

        /**
         * Defines the number of milliseconds that a connection can wait until established a connection.
         *
         * @param connectionTimeout timeout value (in milliseconds).
         * @return this builder.
         */
        public Builder setConnectionTimeout(int connectionTimeout) {
            this.connectionTimeout = connectionTimeout;
            return this;
        }

        /**
         * Defines the `Accept-Encoding: gzip, deflate` HTTP header will be sent to each request {@code false} by default.
         *
         * @param compressionEnforced whether `Accept-Encoding` should be sent.
         * @return this builder.
         */
        public Builder setCompressionEnforced(boolean compressionEnforced) {
            this.compressionEnforced = compressionEnforced;
            return this;
        }

        /**
         * Required exclusively for HTTPS, this defines through a {@link TlsConfiguration} all the TLS related data to establish such
         * connections.
         *
         * @param tlsConfiguration a {@link TlsConfiguration} with the required data.
         * @return this builder.
         */
        public Builder setTlsConfiguration(TlsConfiguration tlsConfiguration) {
            this.tlsConfiguration = tlsConfiguration;
            return this;
        }

        /**
         * Creates an instance of {@link HttpClientConfiguration}.
         *
         * @return an {@link HttpClientConfiguration} as described.
         */
        public HttpClientConfiguration build() {
            return new HttpClientConfiguration(connectionTimeout, compressionEnforced, tlsConfiguration);
        }
    }
}
