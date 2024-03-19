package org.mule.weave.v2.module.http.service;

import java.util.concurrent.CompletableFuture;

public interface HttpClientService {

    @Deprecated
    CompletableFuture<HttpClientResponse> request(HttpClientOptions config);

    HttpClientResponse sendRequest(HttpClientRequest request, HttpClientConfiguration configuration);
}
