package org.mule.weave.v2.module.http.service;

/**
 * Factory object for {@link HttpClient}
 */
public interface HttpClientService {

    /**
     * Creates a {@link HttpClient} instance based on the {@link HttpClientConfiguration}.
     *
     * @param configuration the {@link HttpClientConfiguration} specifying the desired client.
     * @return a newly built {@link HttpClient} based on the {@code configuration}.
     */
    HttpClient getClient(HttpClientConfiguration configuration);
}

