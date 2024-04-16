package org.mule.weave.v2.module.http.service;

public interface HttpClientService {
    HttpClient getClient(HttpClientConfiguration configuration);
}

