package com.agonyforge.mud.demo.model.impl;

import com.agonyforge.mud.models.dynamodb.Persistent;
import software.amazon.awssdk.services.dynamodb.model.AttributeValue;

import java.util.HashMap;
import java.util.Map;
import java.util.Objects;

import static com.agonyforge.mud.demo.model.impl.ModelConstants.DB_ZONE;
import static com.agonyforge.mud.demo.model.impl.ModelConstants.SORT_DATA;
import static com.agonyforge.mud.demo.model.impl.ModelConstants.TYPE_ZONE;

public class MudZone implements Persistent {
    private Long id;
    private String name;

    @Override
    public Map<String, AttributeValue> freeze() {
        Map<String, AttributeValue> map = new HashMap<>();
        Map<String, AttributeValue> data = new HashMap<>();

        map.put("pk", AttributeValue.builder().s(DB_ZONE + getId()).build());
        map.put("sk", AttributeValue.builder().s(SORT_DATA).build());
        map.put("gsi1pk", AttributeValue.builder().s(TYPE_ZONE).build());

        data.put("name", AttributeValue.builder().s(getName()).build());
        map.put("data", AttributeValue.builder().m(data).build());

        return map;
    }

    @Override
    public void thaw(Map<String, AttributeValue> item) {
        setId(Long.valueOf(item.get("pk").s().substring(DB_ZONE.length())));

        Map<String, AttributeValue> data = item.get("data").m();

        setName(data.getOrDefault("name", AttributeValue.builder().nul(true).build()).s());
    }

    public Long getId() {
        return id;
    }

    public void setId(Long id) {
        this.id = id;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (!(o instanceof MudZone)) return false;
        MudZone mudZone = (MudZone) o;
        return Objects.equals(getId(), mudZone.getId());
    }

    @Override
    public int hashCode() {
        return Objects.hash(getId());
    }
}
