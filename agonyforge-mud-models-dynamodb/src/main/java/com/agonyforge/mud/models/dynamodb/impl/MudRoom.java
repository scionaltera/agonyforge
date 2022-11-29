package com.agonyforge.mud.models.dynamodb.impl;

import com.agonyforge.mud.models.dynamodb.Persistent;
import software.amazon.awssdk.services.dynamodb.model.AttributeValue;

import java.util.HashMap;
import java.util.Map;
import java.util.Objects;

import static com.agonyforge.mud.models.dynamodb.impl.Constants.DB_ROOM;
import static com.agonyforge.mud.models.dynamodb.impl.Constants.DB_ZONE;
import static com.agonyforge.mud.models.dynamodb.impl.Constants.SORT_DATA;
import static com.agonyforge.mud.models.dynamodb.impl.Constants.TYPE_ROOM;

public class MudRoom implements Persistent {
    private Long id;
    private Long zoneId;
    private String name;
    private String description;

    @Override
    public Map<String, AttributeValue> freeze() {
        Map<String, AttributeValue> map = new HashMap<>();
        Map<String, AttributeValue> data = new HashMap<>();

        map.put("pk", AttributeValue.builder().s(DB_ROOM + getId()).build());
        map.put("sk", AttributeValue.builder().s(SORT_DATA).build());
        map.put("gsi1pk", AttributeValue.builder().s(TYPE_ROOM).build());
        map.put("gsi2pk", AttributeValue.builder().s(DB_ZONE + zoneId).build());

        data.put("name", AttributeValue.builder().s(getName()).build());
        data.put("description", AttributeValue.builder().s(getDescription()).build());
        map.put("data", AttributeValue.builder().m(data).build());

        return map;
    }

    @Override
    public void thaw(Map<String, AttributeValue> item) {
        setId(Long.valueOf(item.get("pk").s().substring(DB_ROOM.length())));
        setZoneId(Long.valueOf(item.get("gsi2pk").s().substring(DB_ZONE.length())));

        Map<String, AttributeValue> data = item.get("data").m();

        setName(data.getOrDefault("name", AttributeValue.builder().nul(true).build()).s());
        setDescription(data.getOrDefault("description", AttributeValue.builder().nul(true).build()).s());
    }

    public Long getId() {
        return id;
    }

    public void setId(Long id) {
        this.id = id;
    }

    public Long getZoneId() {
        return zoneId;
    }

    public void setZoneId(Long zoneId) {
        this.zoneId = zoneId;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public String getDescription() {
        return description;
    }

    public void setDescription(String description) {
        this.description = description;
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (!(o instanceof MudRoom)) return false;
        MudRoom mudRoom = (MudRoom) o;
        return Objects.equals(getId(), mudRoom.getId());
    }

    @Override
    public int hashCode() {
        return Objects.hash(getId());
    }
}
