package org.mule.weave.v2.module.http.service;

import static java.util.Objects.requireNonNull;
import static java.util.Optional.empty;
import static java.util.Optional.of;

import java.util.Optional;

/**
 * Configuration component that specifies how an {@link HttpClient} should be created.
 * Instances can only be obtained through an {@link HttpClientConfiguration.Builder}.
 */
public class HttpClientConfiguration {
    private final String id;
    private final Optional<Integer> connectionTimeout;

    private HttpClientConfiguration(String id, Optional<Integer> connectionTimeout) {
        this.id = id;
        this.connectionTimeout = connectionTimeout;
    }

    public String getId() {
        return id;
    }

    public Optional<Integer> getConnectionTimeout() {
        return connectionTimeout;
    }

    /**
     * Builder of {@link HttpClientConfiguration}s. At the very least, an id must be provided.
     */
    public static final class Builder {
        private String id;
        private Optional<Integer> connectionTimeout = empty();


        /**
         * Defines the id of the {@link HttpClient}. Must be specified.
         *
         * @param id a {@link String} to identify the {@link HttpClient}.
         * @return this builder.
         */
        public Builder setId(String id) {
            this.id = id;
            return this;
        }

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
            requireNonNull(id, "http client configuration 'id' must not be null");
            return new HttpClientConfiguration(id, connectionTimeout);
        }
    }
}
