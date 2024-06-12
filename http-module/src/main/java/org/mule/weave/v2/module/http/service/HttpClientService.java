package org.mule.weave.v2.module.http.service;

/**
 * Factory object for {@link HttpClient} instances. It creates a {@link HttpClient} instance
 * for every {@link HttpClientConfiguration}.
 * The {@link HttpClient} was designed to be stateless and reusable for the same {@link HttpClientConfiguration}.
 * If you need to reuse {@link HttpClient} instances for a set of requests consider implementing a cache strategy based
 * on {@link HttpClientConfiguration}.
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

