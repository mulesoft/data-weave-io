package org.mule.weave.v2.module.http.service;

import java.util.List;
import java.util.Optional;
import java.util.Set;

/**
 * Representation of an HTTP header.
 */
public interface HttpClientHeaders {

    /**
     * Return a {@link Set} of header names
     *
     * @return the {@link Set} of header names
     */
    Set<String> getHeaderNames();

    /**
     * Gets a {@link List} of response header values for a given {@code name}.
     *
     * @param name the header name.
     * @return the {@link List} of header values for the given {@code name}.
     */
    List<String> getHeaderValues(String name);

    /**
     * Gets the header value for a given {@code name}
     *
     * @param name the header name.
     * @return the first response header value for the given {@code name}.
     */
    Optional<String> getHeaderValue(String name);
}
