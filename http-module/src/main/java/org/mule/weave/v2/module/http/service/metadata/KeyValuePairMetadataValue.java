package org.mule.weave.v2.module.http.service.metadata;

public class KeyValuePairMetadataValue implements MetadataValue {

    private final String key;
    private final MetadataValue value;

    public KeyValuePairMetadataValue(String key, MetadataValue value) {
        this.key = key;
        this.value = value;
    }

    public String getKey() {
        return key;
    }

    public MetadataValue getValue() {
        return value;
    }
}
