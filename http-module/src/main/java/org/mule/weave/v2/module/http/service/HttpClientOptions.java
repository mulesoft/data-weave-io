package org.mule.weave.v2.module.http.service;

import java.io.InputStream;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Optional;

@Deprecated
public class HttpClientOptions {

    /** Full url for the request, including domain */
    private final String url;

    /** HTTP Method */
    private final String method;

    private final Map<String, List<String>> headers;

    private final Map<String, List<String>> queryParams;
    private final Optional<InputStream> body;

    /** Do we accept header redirections? */
    private final boolean allowRedirect;

    private final Optional<Integer> readTimeout; // default 20000ms

    private final Optional<Integer> requestTimeout; // default 10000ms

    /**
     * Should HTTP compression be used?
     * If true, Accept-Encoding: gzip,deflate will be sent with request.
     * If the server response with Content-Encoding: (gzip|deflate) the client will automatically handle decompression
     * This is true by default
     */
    private boolean allowCompression = true;

    private HttpClientOptions(String url, String method, Map<String, List<String>> headers,  Map<String, List<String>> queryParams,
                             Optional<InputStream> body, boolean allowRedirect, Optional<Integer> readTimeout,
                             Optional<Integer> requestTimeout) {
        this.url = url;
        this.method = method;
        this.headers = headers;
        this.queryParams = queryParams;
        this.body = body;
        this.allowRedirect = allowRedirect;
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

    public boolean isAllowRedirect() {
        return allowRedirect;
    }


    public Optional<Integer> getReadTimeout() {
        return readTimeout;
    }

    public Optional<Integer> getRequestTimeout() {
        return requestTimeout;
    }

    @Deprecated
    public static class Builder {
        private String url;
        private String method;
        private Map<String, List<String>> headers = new HashMap<>();
        private Map<String, List<String>> queryParams = new HashMap<>();
        private Optional<InputStream> body = Optional.empty();
        private boolean allowRedirect;
        private Optional<Integer> readTimeout = Optional.empty();
        private Optional<Integer> requestTimeout = Optional.empty();

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

        public Builder withQueryParam(String name, String value) {
            List<String> values = queryParams.computeIfAbsent(name, k -> new ArrayList<>());
            values.add(value);
            return this;
        }

        public Builder withBody(InputStream body) {
            this.body = Optional.of(body);
            return this;
        }

        public Builder withAllowRedirect(boolean allowRedirect) {
            this.allowRedirect = allowRedirect;
            return this;
        }


        public Builder withReadTimeout(int readTimeout) {
            this.readTimeout = Optional.of(readTimeout);
            return this;
        }

        public Builder withRequestTimeout(int requestTimeout) {
            this.requestTimeout = Optional.of(requestTimeout);
            return this;
        }

        public HttpClientOptions build() {
            return new HttpClientOptions(url, method, headers, queryParams, body, allowRedirect, readTimeout, requestTimeout);
        }
    }
}