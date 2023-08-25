package com.agonyforge.mud.models.dynamodb.impl;

import com.agonyforge.mud.models.dynamodb.Persistent;
import software.amazon.awssdk.services.dynamodb.model.AttributeValue;

import java.util.HashMap;
import java.util.Map;
import java.util.Objects;
import java.util.UUID;

public class Example implements Persistent {
    public static final String DB_EXAMPLE = "EXAMPLE#";
    public static final String SORT_DATA = "DATA";
    public static final String TYPE_EXAMPLE = "EXAMPLE";

    private UUID id;
    private String foo;

    @Override
    public Map<String, AttributeValue> freeze() {
        Map<String, AttributeValue> map = new HashMap<>();
        Map<String, AttributeValue> data = new HashMap<>();

        map.put("pk", AttributeValue.builder().s(DB_EXAMPLE + getId()).build());
        map.put("sk", AttributeValue.builder().s(SORT_DATA).build());
        map.put("gsi1pk", AttributeValue.builder().s(TYPE_EXAMPLE).build());

        data.put("foo", AttributeValue.builder().s(foo).build());

        map.put("data", AttributeValue.builder().m(data).build());

        return map;
    }

    @Override
    public void thaw(Map<String, AttributeValue> item) {
        setId(UUID.fromString(item.get("pk").s().substring(DB_EXAMPLE.length())));

        Map<String, AttributeValue> data = item.get("data").m();

        setFoo(data.getOrDefault("foo", AttributeValue.builder().nul(true).build()).s());
    }

    public UUID getId() {
        return id;
    }

    public void setId(UUID id) {
        this.id = id;
    }

    public String getFoo() {
        return foo;
    }

    public void setFoo(String foo) {
        this.foo = foo;
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (!(o instanceof Example example)) return false;
        return Objects.equals(getId(), example.getId());
    }

    @Override
    public int hashCode() {
        return Objects.hash(getId());
    }
}
