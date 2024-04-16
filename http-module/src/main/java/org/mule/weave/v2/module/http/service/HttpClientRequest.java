package org.mule.weave.v2.module.http.service;

import static java.util.Optional.empty;
import static java.util.Optional.of;

import java.io.InputStream;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Optional;

public class HttpClientRequest {
    private final String url;
    private final String method;
    private final Map<String, List<String>> headers;
    private final Map<String, List<String>> queryParams;
    private final Optional<InputStream> body;

    /** Do we accept header redirections? */
    private final boolean followRedirects;
    private final Optional<Integer> readTimeout; // default 20000ms
    private final Optional<Integer> requestTimeout; // default 10000ms

    private HttpClientRequest(String url,
                              String method,
                              Map<String, List<String>> headers,
                              Map<String, List<String>> queryParams,
                              Optional<InputStream> body,
                              boolean followRedirects,
                              Optional<Integer> readTimeout,
                              Optional<Integer> requestTimeout) {
        this.url = url;
        this.method = method;
        this.headers = headers;
        this.queryParams = queryParams;
        this.body = body;
        this.followRedirects = followRedirects;
        this.readTimeout = readTimeout;
        this.requestTimeout = requestTimeout;
    }

    public String getUrl() {
        return url;
    }

    public String getMethod() {
        return method;
    }

    public Map<String, List<String>> getHeaders() {
        return headers;
    }

    public Map<String, List<String>> getQueryParams() {
        return queryParams;
    }

    public Optional<InputStream> getBody() {
        return body;
    }

    public boolean isFollowRedirects() {
        return followRedirects;
    }

    public Optional<Integer> getReadTimeout() {
        return readTimeout;
    }

    public Optional<Integer> getRequestTimeout() {
        return requestTimeout;
    }

    public static final class Builder {
        private String url;
        private String method;
        private Map<String, List<String>> headers = new HashMap<>();
        private Map<String, List<String>> queryParams = new HashMap<>();
        private Optional<InputStream> body = empty();
        private boolean followRedirects;
        private Optional<Integer> readTimeout = empty();
        private Optional<Integer> requestTimeout = empty();

        public Builder withUrl(String url) {
            this.url = url;
            return this;
        }

        public Builder withMethod(String method) {
            this.method = method;
            return this;
        }

        public Builder withHeader(String name, String value) {
            List<String> values = headers.computeIfAbsent(name, k -> new ArrayList<>());
            values.add(value);
            return this;
        }

        public Builder withHeaders(String name, List<String> values) {
            headers.put(name, values);
            return this;
        }

        public Builder withQueryParam(String name, String value) {
            List<String> values = queryParams.computeIfAbsent(name, k -> new ArrayList<>());
            values.add(value);
            return this;
        }

        public Builder withBody(InputStream body) {
            this.body = of(body);
            return this;
        }

        public Builder withFollowRedirect(boolean followRedirects) {
            this.followRedirects = followRedirects;
            return this;
        }

        public Builder withReadTimeout(int readTimeout) {
            this.readTimeout = of(readTimeout);
            return this;
        }

        public Builder withRequestTimeout(int requestTimeout) {
            this.requestTimeout = of(requestTimeout);
            return this;
        }

        public HttpClientRequest build() {
            // TODO: Add requires validation
            return new HttpClientRequest(url, method, headers, queryParams, body, followRedirects,
                    readTimeout, requestTimeout);
        }
    }
}
