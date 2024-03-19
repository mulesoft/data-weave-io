package org.mule.weave.v2.module.http.service;

import java.io.InputStream;
import java.util.Optional;

public class HttpClientRequest {

    private final String url;
    private final String method;
    private final Optional<InputStream> body;

    private HttpClientRequest(String url, String method, Optional<InputStream> body) {
        this.url = url;
        this.method = method;
        this.body = body;
    }

    public String getUrl() {
        return url;
    }

    public String getMethod() {
        return method;
    }

    public Optional<InputStream> getBody() {
        return body;
    }


    public static class Builder {
        private String url;
        private String method;
        private Optional<InputStream> body = Optional.empty();

        public Builder withUrl(String url) {
            this.url = url;
            return this;
        }

        public Builder withMethod(String method) {
            this.method = method;
            return this;
        }

        public Builder withBody(InputStream body) {
            this.body = Optional.of(body);
            return this;
        }

        public HttpClientRequest build() {
            return new HttpClientRequest(url, method, body);
        }
    }
}
