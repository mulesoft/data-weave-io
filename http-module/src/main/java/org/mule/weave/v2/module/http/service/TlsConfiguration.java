package org.mule.weave.v2.module.http.service;

/**
 * Representation of a TLS context.
 * Instances can only be obtained through an {@link TlsConfiguration.Builder}.
 */
public class TlsConfiguration {

    private final boolean insecure;

    private TlsConfiguration(boolean insecure) {
        this.insecure = insecure;
    }

    /**
     * @return Defines whether the trust store should be insecure, meaning no certificate validations should be performed.
     */
    public boolean isInsecure() {
        return insecure;
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;

        TlsConfiguration that = (TlsConfiguration) o;
        return insecure == that.insecure;
    }

    @Override
    public int hashCode() {
        return Boolean.hashCode(insecure);
    }

    /**
     * Builder of {@link TlsConfiguration}s. At the very least, an id must be provided.
     */
    public static final class Builder {
        private boolean insecure = false;

        /**
         * Defines whether the trust store should be insecure, meaning no certificate validations should be performed. Default value is
         * {@code false}.
         *
         * @param insecure if this client allow insecure trust store.
         * @return this builder.
         */
        public TlsConfiguration.Builder setInsecure(boolean insecure) {
            this.insecure = insecure;
            return this;
        }

        /**
         * Creates an instance of {@link TlsConfiguration}.
         *
         * @return an {@link TlsConfiguration} as described.
         */
        public TlsConfiguration build() {
            return new TlsConfiguration(insecure);
        }
    }
}
