package org.mule.weave.v2.module.http.service;

import java.util.concurrent.CompletableFuture;

public interface HttpClient {
    CompletableFuture<HttpClientResponse> request(HttpClientRequest request);
}
