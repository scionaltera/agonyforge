package com.agonyforge.mud.models.dynamodb.impl;

import com.agonyforge.mud.models.dynamodb.Persistent;
import software.amazon.awssdk.services.dynamodb.model.AttributeValue;

import java.util.HashMap;
import java.util.Map;
import java.util.Objects;

import static com.agonyforge.mud.models.dynamodb.impl.Constants.DB_PROPERTY;
import static com.agonyforge.mud.models.dynamodb.impl.Constants.TYPE_PROPERTY;

public class MudProperty implements Persistent {
    private String name;
    private String value;

    public MudProperty() {
        // this method intentionally left blank
    }

    public MudProperty(String name, String value) {
        setName(name);
        setValue(value);
    }

    @Override
    public Map<String, AttributeValue> freeze() {
        Map<String, AttributeValue> map = new HashMap<>();
        Map<String, AttributeValue> data = new HashMap<>();

        map.put("pk", AttributeValue.builder().s(DB_PROPERTY).build());
        map.put("sk", AttributeValue.builder().s(getName()).build());
        map.put("gsi1pk", AttributeValue.builder().s(TYPE_PROPERTY).build());

        data.put("value", AttributeValue.builder().s(getValue()).build());
        map.put("data", AttributeValue.builder().m(data).build());

        return map;
    }

    @Override
    public void thaw(Map<String, AttributeValue> item) {
        Map<String, AttributeValue> data = item.get("data").m();

        setName(item.get("sk").s());
        setValue(data.get("value").s());
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public String getValue() {
        return value;
    }

    public void setValue(String value) {
        this.value = value;
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;
        MudProperty that = (MudProperty) o;
        return Objects.equals(name, that.name) && Objects.equals(value, that.value);
    }

    @Override
    public int hashCode() {
        return Objects.hash(name, value);
    }
}
