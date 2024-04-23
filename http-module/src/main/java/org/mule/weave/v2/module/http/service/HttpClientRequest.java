package org.mule.weave.v2.module.http.service;

import static java.util.Objects.requireNonNull;
import static java.util.Optional.empty;
import static java.util.Optional.of;

import java.io.InputStream;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Optional;

/**
 * Representation of an HTTP request. Instances can only be obtained through an {@link HttpClientRequest.Builder}.
 */
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

    /**
     * @return the request URL.
     */
    public String getUrl() {
        return url;
    }

    /**
     * @return the request HTTP method.
     */
    public String getMethod() {
        return method;
    }

    /**
     * @return the all the HTTP headers.
     */
    public Map<String, List<String>> getHeaders() {
        return headers;
    }

    /**
     * @return the query parameters.
     */
    public Map<String, List<String>> getQueryParams() {
        return queryParams;
    }

    /**
     * @return the request's body InputStream.
     */
    public Optional<InputStream> getBody() {
        return body;
    }

    /**
     * @return if this request is to follow redirects.
     */
    public boolean isFollowRedirects() {
        return followRedirects;
    }

    /**
     * @return the read timeout.
     */
    public Optional<Integer> getReadTimeout() {
        return readTimeout;
    }

    /**
     * @return the request timeout.
     */
    public Optional<Integer> getRequestTimeout() {
        return requestTimeout;
    }

    /**
     * Builder of {@link HttpClientRequest}s.
     */
    public static final class Builder {
        private String url;
        private String method;
        private Map<String, List<String>> headers = new HashMap<>();
        private Map<String, List<String>> queryParams = new HashMap<>();
        private Optional<InputStream> body = empty();
        private boolean followRedirects;
        private Optional<Integer> readTimeout = empty();
        private Optional<Integer> requestTimeout = empty();


        /**
         * Declares the url where this {@link HttpClientRequest} will be sent. Required configuration.
         *
         * @param url the url of the {@link HttpClientRequest} desired.
         * @return this builder.
         */
        public Builder setUrl(String url) {
            this.url = url;
            return this;
        }

        /**
         * Set the HTTP method of the {@link HttpClientRequest} desired.
         *
         * @param method the HTTP method of the {@link HttpClientRequest} desired. Not null.
         * @return this builder.
         */
        public Builder setMethod(String method) {
            this.method = method;
            return this;
        }

        /**
         * Includes a new header to be sent in the desired {@link HttpClientRequest}.
         *
         * @param name the name of the HTTP header.
         * @param value the value of the HTTP header.
         * @return this builder.
         */
        public Builder addHeader(String name, String value) {
            List<String> values = headers.computeIfAbsent(name, k -> new ArrayList<>());
            values.add(value);
            return this;
        }

        /**
         * Includes a new header with multiple values to be sent in the desired {@link HttpClientRequest}.
         *
         * @param name the name of the HTTP header.
         * @param values the values of the HTTP header.
         * @return this builder.
         */
        public Builder addHeaders(String name, List<String> values) {
            headers.put(name, values);
            return this;
        }

        /**
         * Includes a new queryParam to be sent in the desired {@link HttpClientRequest}.
         *
         * @param name the name of the HTTP queryParam.
         * @param value the value of the HTTP queryParam.
         * @return this builder.
         */
        public Builder addQueryParam(String name, String value) {
            List<String> values = queryParams.computeIfAbsent(name, k -> new ArrayList<>());
            values.add(value);
            return this;
        }

        /**
         * Includes the HTTP entity that should be used sent in the desired {@link HttpClientRequest}.
         *
         * @param body the {@link InputStream} that should be used as body for the {@link HttpClientRequest}. Not null.
         * @return this builder
         */
        public Builder setBody(InputStream body) {
            requireNonNull(body, "body cannot be null");
            this.body = of(body);
            return this;
        }

        /**
         * If {@link HttpClientRequest} is to follow redirects.
         *
         * @param followRedirects if this request is to follow redirects.
         * @return this builder.
         */
        public Builder setFollowRedirect(boolean followRedirects) {
            this.followRedirects = followRedirects;
            return this;
        }

        /**
         * Set the HTTP request timeout of the {@link HttpClientRequest} desired.
         *
         * @param readTimeout the read timeout.
         * @return this builder.
         */
        public Builder setReadTimeout(int readTimeout) {
            this.readTimeout = of(readTimeout);
            return this;
        }

        /**
         * Set the HTTP request timeout of the {@link HttpClientRequest} desired.
         *
         * @param requestTimeout the request timeout.
         * @return this builder.
         */
        public Builder setRequestTimeout(int requestTimeout) {
            this.requestTimeout = of(requestTimeout);
            return this;
        }

        /**
         * Creates an instance of {@link HttpClientRequest}.
         *
         * @return an {@link HttpClientRequest} as described.
         */
        public HttpClientRequest build() {
            requireNonNull(url, "http client request 'url' must not be null");
            requireNonNull(method, "http client request 'method' must not be null");
            return new HttpClientRequest(url, method, headers, queryParams, body, followRedirects,
                    readTimeout, requestTimeout);
        }
    }
}
