package org.mule.weave.v2.module.http.service.metadata;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;

/**
 * Representation of an Array metadata value. Instances can only be obtained through an {@link ArrayMetadataValue.Builder}.
 */
public class ArrayMetadataValue implements MetadataValue {

    private final List<MetadataValue> elements;

    private ArrayMetadataValue(List<MetadataValue> elements) {
        this.elements = elements;
    }

    public List<MetadataValue> getElements() {
        return Collections.unmodifiableList(elements);
    }

    public static class Builder {
        private final List<MetadataValue> elements = new ArrayList<>();

        public Builder add(MetadataValue element) {
            this.elements.add(element);
            return this;
        }

        public ArrayMetadataValue build() {
            return new ArrayMetadataValue(elements);
        }
    }
}
