package org.mule.weave.v2.module.http.service;

import java.util.Optional;

public class HttpClientConfiguration {

    private final Optional<Integer> connectionTimeout;

    private HttpClientConfiguration(Optional<Integer> connectionTimeout) {
        this.connectionTimeout = connectionTimeout;
    }

    public Optional<Integer> getConnectionTimeout() {
        return connectionTimeout;
    }

    public static class Builder {
        private Optional<Integer> connectionTimeout = Optional.empty();


        public Builder withConnectionTimeout(int connectionTimeout) {
            this.connectionTimeout = Optional.of(connectionTimeout);
            return this;
        }

        public HttpClientConfiguration build() {
            return new HttpClientConfiguration(connectionTimeout);
        }
    }
}
