package com.agonyforge.mud.models.dynamodb;

import software.amazon.awssdk.services.dynamodb.model.AttributeValue;

import java.util.Map;

public interface Persistent {
    Map<String, AttributeValue> freeze();
    void thaw(Map<String, AttributeValue> item);
}
