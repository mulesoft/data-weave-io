package org.mule.weave.v2.module.http.service.metadata;

/**
 * Representation of a Number metadata value.
 */
public class NumberMetadataValue implements MetadataValue {

    private final Number value;

    public NumberMetadataValue(Number value) {
        this.value = value;
    }

    public Number getValue() {
        return value;
    }
}
