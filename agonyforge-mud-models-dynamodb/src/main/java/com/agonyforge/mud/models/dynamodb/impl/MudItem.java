package com.agonyforge.mud.models.dynamodb.impl;

import com.agonyforge.mud.models.dynamodb.Persistent;
import software.amazon.awssdk.services.dynamodb.model.AttributeValue;

import java.util.HashMap;
import java.util.Map;
import java.util.Objects;
import java.util.UUID;

import static com.agonyforge.mud.models.dynamodb.impl.Constants.DB_ITEM;
import static com.agonyforge.mud.models.dynamodb.impl.Constants.DB_PC;
import static com.agonyforge.mud.models.dynamodb.impl.Constants.DB_ROOM;
import static com.agonyforge.mud.models.dynamodb.impl.Constants.SORT_DATA;
import static com.agonyforge.mud.models.dynamodb.impl.Constants.SORT_INSTANCE;
import static com.agonyforge.mud.models.dynamodb.impl.Constants.TYPE_ITEM;
import static com.agonyforge.mud.models.dynamodb.impl.Constants.TYPE_PC;
import static com.agonyforge.mud.models.dynamodb.impl.Constants.TYPE_ROOM;

public class MudItem implements Persistent {
    private UUID id;
    private UUID instanceId = null;
    private String containerType;
    private String containerId;
    private String name;
    private String description;

    @Override
    public Map<String, AttributeValue> freeze() {
        Map<String, AttributeValue> map = new HashMap<>();
        Map<String, AttributeValue> data = new HashMap<>();

        map.put("pk", AttributeValue.builder().s(DB_ITEM + getId()).build());
        map.put("gsi1pk", AttributeValue.builder().s(TYPE_ITEM).build());

        if (isPrototype()) {
            map.put("sk", AttributeValue.builder().s(SORT_DATA).build());
        } else {
            map.put("sk", AttributeValue.builder().s(SORT_INSTANCE + instanceId).build());

            if (TYPE_PC.equals(containerType)) {
                map.put("gsi2pk", AttributeValue.builder().s(TYPE_PC + getCharacterId()).build());
            } else if (TYPE_ROOM.equals(containerType)) {
                map.put("gsi2pk", AttributeValue.builder().s(TYPE_ROOM + getRoomId()).build());
            }
        }

        data.put("name", AttributeValue.builder().s(getName()).build());
        data.put("description", AttributeValue.builder().s(getDescription()).build());

        map.put("data", AttributeValue.builder().m(data).build());

        return map;
    }

    @Override
    public void thaw(Map<String, AttributeValue> item) {
        setId(UUID.fromString(item.get("pk").s().substring(DB_ITEM.length())));

        if (SORT_INSTANCE.equals(item.get("sk").s())) {
            setPrototype(false);

            if (item.get("gsi2pk").s().startsWith(DB_PC)) {
                setCharacterId(item.get("gsi2pk").s().substring(DB_PC.length()));
            } else if (item.get("gsi2pk").s().startsWith(DB_ROOM)) {
                setRoomId(Long.valueOf(item.get("gsi2pk").s().substring(DB_ROOM.length())));
            }
        }

        Map<String, AttributeValue> data = item.get("data").m();
        setName(data.getOrDefault("name", AttributeValue.builder().nul(true).build()).s());
        setDescription(data.getOrDefault("description", AttributeValue.builder().nul(true).build()).s());
    }

    public MudItem buildInstance() {
        if (!isPrototype()) {
            throw new IllegalStateException("cannot build instance from instance");
        }

        MudItem instance = new MudItem();

        instance.setPrototype(false);
        instance.setId(getId());
        instance.setName(getName());
        instance.setDescription(getDescription());

        return instance;
    }

    public UUID getId() {
        return id;
    }

    public void setId(UUID id) {
        this.id = id;
    }

    public String getCharacterId() {
        if (TYPE_PC.equals(containerType)) {
            return containerId;
        }

        return null;
    }

    public void setCharacterId(String characterId) {
        containerType = TYPE_PC;
        containerId = characterId;
    }

    public Long getRoomId() {
        if (TYPE_ROOM.equals(containerType)) {
            return Long.valueOf(containerId);
        }

        return null;
    }

    public void setRoomId(Long roomId) {
        containerType = TYPE_ROOM;
        containerId = roomId.toString();
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

    public boolean isPrototype() {
        return instanceId == null;
    }

    public void setPrototype(boolean prototype) {
        if (prototype) {
            instanceId = UUID.randomUUID();
        } else {
            instanceId = null;
        }
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (!(o instanceof MudItem mudItem)) return false;
        return isPrototype() == mudItem.isPrototype() && Objects.equals(getId(), mudItem.getId());
    }

    @Override
    public int hashCode() {
        return Objects.hash(getId(), isPrototype());
    }
}
