package org.mule.weave.v2.module.http.service;

import java.util.List;
import java.util.Optional;
import java.util.Set;

public interface HttpClientHeaders {

    Set<String> getHeaderNames();

    List<String> getHeaderValues(String name);

    Optional<String> getHeaderValue(String name);
}
