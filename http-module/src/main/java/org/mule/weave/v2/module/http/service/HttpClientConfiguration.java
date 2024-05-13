package org.mule.weave.v2.module.http.service;

import java.util.Objects;

/**
 * Configuration component that specifies how an {@link HttpClient} should be created.
 * Instances can only be obtained through an {@link HttpClientConfiguration.Builder}.
 */
public class HttpClientConfiguration {
    private final int connectionTimeout;
    private final TlsConfiguration tlsConfiguration;

    private HttpClientConfiguration(int connectionTimeout, TlsConfiguration tlsConfiguration) {
        this.connectionTimeout = connectionTimeout;
        this.tlsConfiguration = tlsConfiguration;
    }

    /**
     * @return the maximum time in millisecond an {@link HttpClient} can wait when connecting to a remote host.
     */
    public int getConnectionTimeout() {
        return connectionTimeout;
    }

    public TlsConfiguration getTlsConfiguration() {
        return tlsConfiguration;
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;

        HttpClientConfiguration that = (HttpClientConfiguration) o;
        return Objects.equals(connectionTimeout, that.connectionTimeout) && Objects.equals(tlsConfiguration, that.tlsConfiguration);
    }

    @Override
    public int hashCode() {
        int result = Objects.hashCode(connectionTimeout);
        result = 31 * result + Objects.hashCode(tlsConfiguration);
        return result;
    }

    /**
     * Builder of {@link HttpClientConfiguration}s. At the very least, an id must be provided.
     */
    public static final class Builder {
        private int connectionTimeout = 5000;
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
            return new HttpClientConfiguration(connectionTimeout, tlsConfiguration);
        }
    }
}
