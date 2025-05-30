package org.mule.weave.v2.module.http.service;

import static java.util.Objects.requireNonNull;

import java.io.InputStream;
import java.util.List;

/**
 * Representation of an HTTP request. Instances can only be obtained through an {@link HttpClientRequest.Builder}.
 */
public class HttpClientRequest {
    private final String url;
    private final String method;
    private final HttpClientHeaders headers;
    private final HttpClientQueryParams queryParams;
    private final InputStream body;

    /** Do we accept header redirections? */
    private final boolean followRedirects;
    private final int readTimeout;
    private final int requestTimeout;
    private final boolean streamResponse;
    private final boolean enableMetrics;

    private HttpClientRequest(String url,
                              String method,
                              HttpClientHeaders headers,
                              HttpClientQueryParams queryParams,
                              InputStream body,
                              boolean followRedirects,
                              int readTimeout,
                              int requestTimeout,
                              boolean streamResponse,
                              boolean enableMetrics) {
        this.url = url;
        this.method = method;
        this.headers = headers;
        this.queryParams = queryParams;
        this.body = body;
        this.followRedirects = followRedirects;
        this.readTimeout = readTimeout;
        this.requestTimeout = requestTimeout;
        this.streamResponse = streamResponse;
        this.enableMetrics = enableMetrics;
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
    public HttpClientHeaders getHeaders() {
        return headers;
    }

    /**
     * @return the {@link HttpClientQueryParams}.
     */
    public HttpClientQueryParams getQueryParams() {
        return queryParams;
    }

    /**
     * @return the request's body InputStream.
     */
    public InputStream getBody() {
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
    public int getReadTimeout() {
        return readTimeout;
    }

    /**
     * @return the request timeout.
     */
    public int getRequestTimeout() {
        return requestTimeout;
    }

    /**
     * @return if this request is to stream response.
     */
    public boolean isStreamResponse(){
        return streamResponse;
    }

    /**
     * @return if this request has metrics enabled.
     */
    public boolean isEnableMetrics(){return enableMetrics;}

    /**
     * Builder of {@link HttpClientRequest}s.
     */
    public static final class Builder {
        private String url;
        private String method;
        private final HttpClientHeaders.Builder headersBuilder = new HttpClientHeaders.Builder();
        private final HttpClientQueryParams.Builder queryParamsBuilder = new HttpClientQueryParams.Builder();
        private InputStream body = null;
        private boolean followRedirects = false;
        private int readTimeout = 60000;
        private int requestTimeout = 60000;
        private boolean streamResponse = false;
        private boolean enableMetrics = false;

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
            requireNonNull(name, "name cannot be null");
            requireNonNull(value, "value cannot be null");
            this.headersBuilder.addHeader(name, value);
            return this;
        }

        /**
         * Includes a new header to be sent in the desired {@link HttpClientRequest}.
         *
         * @param header the {@link HttpClientHeaders.HttpHeader} that should be used. Not null.
         * @return this builder.
         */
        public Builder addHeader(HttpClientHeaders.HttpHeader header) {
            requireNonNull(header, "header cannot be null");
            this.headersBuilder.addHeader(header);
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
            requireNonNull(name, "name cannot be null");
            requireNonNull(values, "values cannot be null");
            values.forEach(value -> this.headersBuilder.addHeader(name, value));
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
            requireNonNull(name, "name cannot be null");
            requireNonNull(value, "value cannot be null");
            this.queryParamsBuilder.addQueryParam(name, value);
            return this;
        }

        /**
         * Includes a new queryParam to be sent in the desired {@link HttpClientRequest}.
         *
         * @param queryParam the {@link HttpClientQueryParams.HttpQueryParam} that should be used. Not null.
         * @return this builder.
         */
        public Builder addQueryParam(HttpClientQueryParams.HttpQueryParam queryParam) {
            requireNonNull(queryParam, "queryParam cannot be null");
            this.queryParamsBuilder.addQueryParam(queryParam);
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
            this.body = body;
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
            this.readTimeout = readTimeout;
            return this;
        }

        /**
         * Set the HTTP request timeout of the {@link HttpClientRequest} desired.
         *
         * @param requestTimeout the request timeout.
         * @return this builder.
         */
        public Builder setRequestTimeout(int requestTimeout) {
            this.requestTimeout = requestTimeout;
            return this;
        }

        /**
         * Set the HTTP request streamingResponse of the {@link HttpClientRequest} desired.
         *
         * @param streamResponse if this request response should be streamed.
         * @return this builder.
         */
        public Builder setStreamResponse(boolean streamResponse) {
            this.streamResponse = streamResponse;
            return this;
        }

        /**
         * Set the HTTP request enableMetrics of the {@link HttpClientRequest} desired.
         *
         * @param enableMetrics if this request response should be measured.
         * @return this builder.
         */
        public Builder setEnableMetrics(boolean enableMetrics) {
            this.enableMetrics = enableMetrics;
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
            return new HttpClientRequest(url, method, headersBuilder.build(), queryParamsBuilder.build(), body, followRedirects,
                    readTimeout, requestTimeout, streamResponse, enableMetrics);
        }
    }
}
