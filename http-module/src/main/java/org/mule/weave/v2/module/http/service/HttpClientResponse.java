package org.mule.weave.v2.module.http.service;

import static java.util.Collections.emptyList;
import static java.util.Optional.empty;
import static java.util.Optional.ofNullable;
import static java.util.stream.Collectors.toList;

import org.mule.weave.v2.module.http.service.metadata.ObjectMetadataValue;

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
        Optional<HttpClientHeaders> headers = ofNullable(getHeaders());
        if (headers.isPresent()) {
            Optional<List<String>> locationHeaderValues = ofNullable(headers.get().getHeaderValues(LOCATION));
            if (locationHeaderValues.isPresent()) {
                return locationHeaderValues.get()
                        .stream()
                        .findFirst();
            }
        }
        return empty();
    }

    /**
     * Get the parsed cookies from the "Set-Cookie" header
     *
     * @return the {@link List} of the {@link HttpCookie}
     */
    default List<HttpCookie> getCookies() {
        final Optional<HttpClientHeaders> headers = ofNullable(getHeaders());
        if (headers.isPresent()) {
            Optional<List<String>> setCookiesHeaderValues = ofNullable(headers.get().getHeaderValues(SET_COOKIE));
            if (setCookiesHeaderValues.isPresent()) {
               return setCookiesHeaderValues.get()
                        .stream()
                        .map(HttpCookie::parse)
                        .flatMap(Collection::stream)
                        .collect(toList());
            }
        }
        return emptyList();
    }

    /**
     * Get the metadata related to the HTTP request sent. (Useful to populate HTTP request metrics)
     *
     * @return the metadata.
     */
    default Optional<ObjectMetadataValue> getMetadata() {
        return empty();
    }
}
