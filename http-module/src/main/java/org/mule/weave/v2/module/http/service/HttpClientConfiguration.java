package org.mule.weave.v2.module.http.service;

import static java.util.Optional.empty;
import static java.util.Optional.of;

import java.util.Objects;
import java.util.Optional;

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

    public static final class Builder {
        private String id;
        private Optional<Integer> connectionTimeout = empty();

        public Builder withId(String id) {
            this.id = id;
            return this;
        }

        public Builder withConnectionTimeout(int connectionTimeout) {
            this.connectionTimeout = of(connectionTimeout);
            return this;
        }

        public HttpClientConfiguration build() {
            Objects.requireNonNull(id, "http client configuration 'id' must not be null");
            return new HttpClientConfiguration(id, connectionTimeout);
        }
    }
}
