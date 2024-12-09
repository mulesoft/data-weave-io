package org.mule.weave.v2.module.http.service;

import static java.util.Collections.unmodifiableList;
import static java.util.Objects.requireNonNull;
import static java.util.stream.Collectors.toList;
import static java.util.stream.Collectors.toSet;

import java.util.ArrayList;
import java.util.List;
import java.util.Set;
import java.util.TreeSet;

/**
 * Representation of an HTTP Query Params.
 */
public class HttpClientQueryParams {

    private final List<HttpQueryParam> queryParams;

    public HttpClientQueryParams(List<HttpQueryParam> queryParams) {
        this.queryParams = queryParams;
    }

    /**
     * @return the {@link List} of {@link HttpQueryParam}.
     */
    public List<HttpQueryParam> getQueryParams() {
        return unmodifiableList(queryParams);
    }

    /**
     *
     * @return the {@link Set} of HTTP query param names.
     */
    public Set<String> names() {
        return queryParams
                .stream()
                .map(HttpQueryParam::getName)
                .collect(toSet());
    }

    /**
     * @return the {@link Set} of HTTP query param names ignoring case.
     */
    public Set<String> namesIgnoreCase() {
        final Set<String> names = new TreeSet<>(String.CASE_INSENSITIVE_ORDER);
        queryParams.forEach(queryParam -> names.add(queryParam.getName()));
        return names;
    }

    /**
     * Gets a {@link List} of HTTP query params values for a given {@code name}.
     *
     * @param name The HTTP query param name to search.
     * @return the {@link List} of HTTP query values for the given {@code name}.
     */
    public List<String> allValues(String name) {
        return queryParams
                .stream()
                .filter(h -> h.getName().equals(name))
                .map(HttpQueryParam::getValue)
                .collect(toList());
    }

    /**
     * Gets a {@link List} of HTTP query params values for a given {@code name} ignoring case.
     *
     * @param name The HTTP query param name to search.
     * @return the {@link List} of HTTP query param values for the given {@code name}.
     */
    public List<String> allValuesIgnoreCase(String name) {
        return queryParams
                .stream()
                .filter(h -> h.getName().equalsIgnoreCase(name))
                .map(HttpQueryParam::getValue)
                .collect(toList());
    }

    /**
     * Builder of {@link HttpClientQueryParams}s.
     */
    public static class Builder {
        private final List<HttpQueryParam> queryParams = new ArrayList<>();

        /**
         * Includes a new HTTP query param in the desired {@link HttpClientQueryParams}.
         *
         * @param name the name of the HTTP queryParam.
         * @param value the value of the HTTP queryParam.
         * @return this builder.
         */
        public Builder addQueryParam(String name, String value) {
            requireNonNull(name, "name cannot be null");
            requireNonNull(value, "value cannot be null");
            return addQueryParam(HttpQueryParam.of(name, value));
        }

        /**
         * Includes a new HTTP query param in the desired {@link HttpClientQueryParams}.
         *
         * @param queryParam the {@link HttpQueryParam} to add.
         * @return this builder.
         */
        public Builder addQueryParam(HttpQueryParam queryParam) {
            requireNonNull(queryParam, "queryParam cannot be null");
            this.queryParams.add(queryParam);
            return this;
        }

        /**
         * Creates an instance of {@link HttpClientQueryParams}.
         *
         * @return an {@link HttpClientQueryParams} as described.
         */
        public HttpClientQueryParams build() {
            return new HttpClientQueryParams(queryParams);
        }
    }

    /**
     * Representation of an HTTP Query Param.
     */
    public static class HttpQueryParam {
        private final String name;
        private final String value;

        public HttpQueryParam(String name, String value) {
            requireNonNull(name, "name must not be null");
            requireNonNull(value, "value must not be null");
            this.name = name;
            this.value = value;
        }

        /**
         * @return The Query Param name.
         */
        public String getName() {
            return name;
        }

        /**
         * @return The Query Param value.
         */
        public String getValue() {
            return value;
        }

        /**
         * Creates an instance of {@link HttpQueryParam}.
         *
         * @param name the name of the HTTP queryParam.
         * @param value the value of the HTTP queryParam.
         * @return an {@link HttpQueryParam} .
         */
        public static HttpQueryParam of(String name, String value) {
            return new HttpQueryParam(name, value);
        }
    }
}
