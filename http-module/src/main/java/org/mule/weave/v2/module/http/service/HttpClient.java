package org.mule.weave.v2.module.http.service;

/**
 * Object that sends an HTTP request, and returns the response.
 */
public interface HttpClient {

    /**
     * Sends a HttpRequest blocking the current thread until a response is available or the request times out.
     *
     * @param request the {@link HttpClientRequest} to send.
     * @return the received {@link HttpClientResponse}
     */
    HttpClientResponse request(HttpClientRequest request);
}
