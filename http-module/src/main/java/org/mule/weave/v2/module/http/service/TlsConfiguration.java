package org.mule.weave.v2.module.http.service;

public class SslConfiguration {

    private final boolean insecure;

    public SslConfiguration(boolean insecure) {
        this.insecure = insecure;
    }

    public boolean isInsecure() {
        return insecure;
    }
}
