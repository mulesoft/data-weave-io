package org.mule.weave.v2.module.http.service;

import org.mule.weave.v2.module.http.service.metadata.ObjectMetadataValue;

import static java.util.stream.Collectors.toList;

import java.io.InputStream;
import java.net.HttpCookie;
import java.util.Collection;
import java.util.List;
import java.util.Optional;

/**
 * Representation of an HTTP response.
 */
public interface HttpClientResponse {

    String LOCATION = "Location";

    String SET_COOKIE = "Set-Cookie";

    /**
     * Returns the status code for the request.
     *
     * @return The status code
     */
    int getStatus();

    /**
     * Returns the status text for the request.
     *
     * @return The status text
     */
    Optional<String> getStatusText();

    /**
     * Return a {@link List} of the {@link HttpClientHeaders} response header value.
     * @return the {@link List} of the {@link HttpClientHeaders}
     */
    HttpClientHeaders getHeaders();

    /**
     * Return the content-type header value.
     *
     * @return the content-type header value.
     */
    Optional<String> getContentType();

    /**
     *  Returns an {@link Optional} input stream for the response body. Note that you should not try to get this more than once, and that you should not close the stream.
     *
     * @return The {@link Optional} input stream
     */
    Optional<InputStream> getBody();

    /**
     * Location header value sent for redirects. By default, this library will not follow redirects
     *
     * @return the location header value
     */
    default Optional<String> getLocation() {
        return getHeaders()
                .getHeaderValues(LOCATION)
                .stream()
                .findFirst();
    }

    /**
     * Get the parsed cookies from the "Set-Cookie" header
     *
     * @return the {@link List} of the {@link HttpCookie}
     */
    default List<HttpCookie> getCookies() {
        return getHeaders()
                .getHeaderValues(SET_COOKIE)
                .stream()
                .map(HttpCookie::parse)
                .flatMap(Collection::stream)
                .collect(toList());
    }

    /**
     * Get the metadata related to the HTTP request sent. (Useful to populate HTTP request metrics)
     *
     * @return the metadata.
     */
    default ObjectMetadataValue getMetadata() {
        return null;
    }
}
