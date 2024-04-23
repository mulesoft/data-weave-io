package org.mule.weave.v2.module.http.service;

import java.util.concurrent.CompletableFuture;

/**
 * Object that sends an HTTP request, and returns the response.
 */
public interface HttpClient {

    /**
     * Sends an HttpRequest without blocking the current thread. When a response is available or the request times out the returned
     * {@link CompletableFuture} will be completed.
     *
     * @param request the {@link HttpClientRequest} to send.
     * @return a {@link CompletableFuture} that will be complete once the {@link HttpClientResponse} is available.
     */
    CompletableFuture<HttpClientResponse> request(HttpClientRequest request);
}
