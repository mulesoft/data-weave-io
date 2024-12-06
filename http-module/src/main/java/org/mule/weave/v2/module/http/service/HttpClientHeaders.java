package org.mule.weave.v2.module.http.service;

import static java.util.Collections.unmodifiableList;
import static java.util.Objects.requireNonNull;
import static java.util.stream.Collectors.toList;
import static java.util.stream.Collectors.toSet;

import java.util.ArrayList;
import java.util.List;
import java.util.Map;
import java.util.Optional;
import java.util.Set;
import java.util.TreeSet;

/**
 * Representation of an HTTP headers.
 */
public class HttpClientHeaders {
    private final List<HttpHeader> headers;

    public HttpClientHeaders(List<HttpHeader> headers) {
        this.headers = headers;
    }

    /**
     * @return the {@link List} of {@link HttpHeader}.
     */
    public List<HttpHeader> getHeaders() {
        return unmodifiableList(headers);
    }

    /**
     * @return the {@link Set} of header names.
     */
    public Set<String> names() {
        return headers
                .stream()
                .map(HttpHeader::getName)
                .collect(toSet());
    }

    /**
     * @return the {@link Set} of HTTP header names ignoring case.
     */
    public Set<String> namesIgnoreCase() {
        final Set<String> names = new TreeSet<>(String.CASE_INSENSITIVE_ORDER);
        headers.forEach(header -> names.add(header.getName()));
        return names;
    }

    /**
     * Gets a {@link List} of header values for a given {@code name}.
     *
     * @param name The HTTP header name to search.
     * @return the {@link List} of header values for the given {@code name}.
     */
    public List<String> allValues(String name) {
        return headers
                .stream()
                .filter(h -> h.getName().equals(name))
                .map(HttpHeader::getValue)
                .collect(toList());
    }

    /**
     * Gets a {@link List} of header values for a given {@code name} ignoring case.
     *
     * @param name The HTTP header name to search.
     * @return the {@link List} of header values for the given {@code name}.
     */
    public List<String> allValuesIgnoreCase(String name) {
        return headers
                .stream()
                .filter(h -> h.getName().equalsIgnoreCase(name))
                .map(HttpHeader::getValue)
                .collect(toList());
    }

    /**
     * Gets the header value for a given {@code name}
     *
     * @param name The HTTP header name to search.
     * @return the first HTTP header value for the given {@code name}.
     */
    public Optional<String> firstValue(String name) {
        return headers
                .stream()
                .filter(h -> h.getName().equals(name))
                .map(HttpHeader::getValue)
                .findFirst();
    }

    /**
     * Gets the header value for a given {@code name} ignoring case.
     *
     * @param name The HTTP header name to search.
     * @return the first HTTP header value for the given {@code name}.
     */
    public Optional<String> firstValueIgnoreCase(String name) {
        return headers
                .stream()
                .filter(h -> h.getName().equalsIgnoreCase(name))
                .map(HttpHeader::getValue)
                .findFirst();
    }

    public static HttpClientHeaders of(Map<String, List<String>> headers) {
        requireNonNull(headers, "'headers' must not be null");
        final List<HttpHeader> headersList = new ArrayList<>();
        headers.forEach((key, values) -> values.forEach(value -> {
            final HttpHeader header = new HttpHeader(key, value);
            headersList.add(header);
        }));
        return new HttpClientHeaders(headersList);
    }

    /**
     * Builder of {@link HttpClientHeaders}s.
     */
    public static final class Builder {
        private final List<HttpHeader> headers = new ArrayList<>();

        /**
         * Includes a new header in the desired {@link HttpClientHeaders}.
         *
         * @param name the name of the HTTP header.
         * @param value the value of the HTTP header.
         * @return this builder.
         */
        public Builder addHeader(String name, String value) {
            requireNonNull(name, "name must not be null");
            requireNonNull(value, "value must not be null");
            return addHeader(new HttpHeader(name, value));
        }

        /**
         * Includes a new header in the desired {@link HttpClientHeaders}.
         *
         * @param header the {@link HttpHeader} to add.
         * @return this builder.
         */
        public Builder addHeader(HttpHeader header) {
            requireNonNull(header, "header must not be null");
            this.headers.add(header);
            return this;
        }

        /**
         * Creates an instance of {@link HttpClientHeaders}.
         *
         * @return an {@link HttpClientHeaders} as described.
         */
        public HttpClientHeaders build() {
            return new HttpClientHeaders(headers);
        }
    }

    /**
     * Representation of an HTTP header.
     */
    public static final class HttpHeader {
        private final String name;
        private final String value;

        public HttpHeader(String name, String value) {
            requireNonNull(name, "name must not be null");
            requireNonNull(value, "value must not be null");
            this.name = name;
            this.value = value;
        }

        /**
         * @return The HTTP header name.
         */
        public String getName() {
            return name;
        }

        /**
         * @return The HTTP header value.
         */
        public String getValue() {
            return value;
        }
    }
}
