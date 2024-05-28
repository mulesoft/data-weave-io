package org.mule.weave.v2.module.http.service.metadata;

/**
 * Representation of a Number metadata value.
 */
public class NumberMetadataValue implements MetadataValue {

    private final String value;

    public NumberMetadataValue(String value) {
        this.value = value;
    }

    public String getValue() {
        return value;
    }
}
