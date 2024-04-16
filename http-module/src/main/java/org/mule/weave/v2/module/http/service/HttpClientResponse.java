package org.mule.weave.v2.module.http.service;

import static java.util.stream.Collectors.toList;

import java.io.InputStream;
import java.net.HttpCookie;
import java.util.Collection;
import java.util.List;
import java.util.Optional;

public interface HttpClientResponse {

    String LOCATION = "Location";

    String SET_COOKIE = "Set-Cookie";

    /** Example: 200 */
    int getStatus();

    Optional<String> getStatusText();

    /** Response headers * */
    HttpClientHeaders getHeaders();

    Optional<String> getContentType();

    /** Response's raw body */
    Optional<InputStream> getBody();

    /** Location header value sent for redirects. By default, this library will not follow redirects. */
    default Optional<String> getLocation() {
        return getHeaders()
                .getHeaderValues(LOCATION)
                .stream()
                .findFirst();
    }


    /** Get the parsed cookies from the "Set-Cookie" header * */
    default List<HttpCookie> getCookies() {
        return getHeaders()
                .getHeaderValues(SET_COOKIE)
                .stream()
                .map(HttpCookie::parse)
                .flatMap(Collection::stream)
                .collect(toList());
    }
}
