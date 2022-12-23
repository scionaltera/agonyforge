package com.agonyforge.mud.models.dynamodb.impl;

import com.agonyforge.mud.models.dynamodb.Persistent;
import software.amazon.awssdk.services.dynamodb.model.AttributeValue;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Objects;
import java.util.UUID;

import static com.agonyforge.mud.models.dynamodb.impl.Constants.DB_SPECIES;
import static com.agonyforge.mud.models.dynamodb.impl.Constants.SORT_DATA;
import static com.agonyforge.mud.models.dynamodb.impl.Constants.TYPE_SPECIES;

public class Species implements Persistent {
    private UUID id;
    private String name;
    private boolean playable;
    private List<String> wearSlots = new ArrayList<>();

    @Override
    public Map<String, AttributeValue> freeze() {
        Map<String, AttributeValue> map = new HashMap<>();
        Map<String, AttributeValue> data = new HashMap<>();

        map.put("pk", AttributeValue.builder().s(DB_SPECIES + getId()).build());
        map.put("sk", AttributeValue.builder().s(SORT_DATA).build());
        map.put("gsi1pk", AttributeValue.builder().s(TYPE_SPECIES).build());

        data.put("name", AttributeValue.builder().s(getName()).build());
        data.put("playable", AttributeValue.builder().bool(isPlayable()).build());
        data.put("wear_slots", AttributeValue.builder().ss(wearSlots).build());

        map.put("data", AttributeValue.builder().m(data).build());

        return map;
    }

    @Override
    public void thaw(Map<String, AttributeValue> item) {
        setId(UUID.fromString(item.get("pk").s().substring(DB_SPECIES.length())));

        Map<String, AttributeValue> data = item.get("data").m();
        setName(data.getOrDefault("name", AttributeValue.builder().nul(true).build()).s());
        setPlayable(data.getOrDefault("playable", AttributeValue.builder().bool(false).build()).bool());
        setWearSlots(data.getOrDefault("wear_slots", AttributeValue.builder().nul(true).build()).ss());
    }

    public UUID getId() {
        return id;
    }

    public void setId(UUID id) {
        this.id = id;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public boolean isPlayable() {
        return playable;
    }

    public void setPlayable(boolean playable) {
        this.playable = playable;
    }

    public List<String> getWearSlots() {
        return wearSlots;
    }

    public void setWearSlots(List<String> wearSlots) {
        this.wearSlots = wearSlots;
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (!(o instanceof Species species)) return false;
        return Objects.equals(getId(), species.getId());
    }

    @Override
    public int hashCode() {
        return Objects.hash(getId());
    }
}
