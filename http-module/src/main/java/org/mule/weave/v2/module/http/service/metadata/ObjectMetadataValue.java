package org.mule.weave.v2.module.http.service.metadata;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;

/**
 * Representation of an Object metadata value. Instances can only be obtained through an {@link ObjectMetadataValue.Builder}.
 */
public class ObjectMetadataValue implements MetadataValue {
    private final List<KeyValuePairMetadataValue> properties;

    private ObjectMetadataValue(List<KeyValuePairMetadataValue> fields) {
       this.properties = fields;
    }

    public List<KeyValuePairMetadataValue> getProperties() {
        return Collections.unmodifiableList(properties);
    }

    public static final class Builder {
        private final List<KeyValuePairMetadataValue> properties = new ArrayList<>();

        public Builder addKeyValuePair(String key, MetadataValue value) {
            properties.add(new KeyValuePairMetadataValue(key, value));
            return this;
        }

        public ObjectMetadataValue build() {
            return new ObjectMetadataValue(properties);
        }
    }
}
