package org.mule.weave.v2.module.http.service;

import static java.util.Optional.empty;
import static java.util.Optional.of;

import java.util.Objects;
import java.util.Optional;

/**
 * Configuration component that specifies how an {@link HttpClient} should be created.
 * Instances can only be obtained through an {@link HttpClientConfiguration.Builder}.
 */
public class HttpClientConfiguration {
    private final Optional<Integer> connectionTimeout;

    private HttpClientConfiguration(Optional<Integer> connectionTimeout) {
        this.connectionTimeout = connectionTimeout;
    }

    /**
     * @return the maximum time in millisecond an {@link HttpClient} can wait when connecting to a remote host.
     */
    public Optional<Integer> getConnectionTimeout() {
        return connectionTimeout;
    }

    /**
     * Builder of {@link HttpClientConfiguration}s. At the very least, an id must be provided.
     */
    public static final class Builder {
        private Optional<Integer> connectionTimeout = empty();

        /**
         * Defines the number of milliseconds that a connection can wait until established a connection.
         *
         * @param connectionTimeout timeout value (in milliseconds).
         * @return this builder.
         */
        public Builder setConnectionTimeout(int connectionTimeout) {
            this.connectionTimeout = of(connectionTimeout);
            return this;
        }

        /**
         * Creates an instance of {@link HttpClientConfiguration}.
         *
         * @return an {@link HttpClientConfiguration} as described.
         */
        public HttpClientConfiguration build() {
            return new HttpClientConfiguration(connectionTimeout);
        }
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;

        HttpClientConfiguration that = (HttpClientConfiguration) o;
        return Objects.equals(connectionTimeout, that.connectionTimeout);
    }

    @Override
    public int hashCode() {
        return Objects.hashCode(connectionTimeout);
    }
}
