package org.mule.weave.v2.module.http.service.metadata;

/**
 * Representation of a String metadata value.
 */
public class StringMetadataValue implements MetadataValue {

    private final String value;

    public StringMetadataValue(String value) {
        this.value = value;
    }

    public String getValue() {
        return value;
    }
}
