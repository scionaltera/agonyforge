package com.agonyforge.mud.demo.model.impl;

import com.agonyforge.mud.models.dynamodb.Persistent;
import com.agonyforge.mud.demo.model.constant.WearSlot;
import software.amazon.awssdk.services.dynamodb.model.AttributeValue;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Objects;
import java.util.UUID;

import static com.agonyforge.mud.demo.model.impl.Constants.DB_ITEM;
import static com.agonyforge.mud.demo.model.impl.Constants.DB_PC;
import static com.agonyforge.mud.demo.model.impl.Constants.DB_ROOM;
import static com.agonyforge.mud.demo.model.impl.Constants.SORT_DATA;
import static com.agonyforge.mud.demo.model.impl.Constants.SORT_INSTANCE;
import static com.agonyforge.mud.demo.model.impl.Constants.TYPE_ITEM;
import static com.agonyforge.mud.demo.model.impl.Constants.TYPE_PC;
import static com.agonyforge.mud.demo.model.impl.Constants.TYPE_ROOM;

public class MudItem implements Persistent {
    private UUID id;
    private UUID instanceId = null;
    private String containerType;
    private String containerId;
    private List<String> nameList = new ArrayList<>();
    private String shortDescription;
    private String longDescription;
    private List<WearSlot> wearSlots = new ArrayList<>();
    private WearSlot worn;

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
                map.put("gsi2pk", AttributeValue.builder().s(DB_PC + getCharacterId()).build());
            } else if (TYPE_ROOM.equals(containerType)) {
                map.put("gsi2pk", AttributeValue.builder().s(DB_ROOM + getRoomId()).build());
            }
        }

        data.put("nameList", AttributeValue.builder().ss(getNameList()).build());
        data.put("shortDescription", AttributeValue.builder().s(getShortDescription()).build());
        data.put("longDescription", AttributeValue.builder().s(getLongDescription()).build());

        if (!wearSlots.isEmpty()) {
            List<String> slots = wearSlots
                .stream()
                .map(WearSlot::name)
                .toList();

            data.put("wear_slots", AttributeValue.builder().ss(slots).build());
        }

        if (worn != null) {
            data.put("worn", AttributeValue.builder().s(worn.name()).build());
        }

        map.put("data", AttributeValue.builder().m(data).build());

        return map;
    }

    @Override
    public void thaw(Map<String, AttributeValue> item) {
        setId(UUID.fromString(item.get("pk").s().substring(DB_ITEM.length())));

        if (item.get("sk").s().startsWith(SORT_INSTANCE)) {
            instanceId = UUID.fromString(item.get("sk").s().substring(SORT_INSTANCE.length()));

            if (item.get("gsi2pk").s().startsWith(DB_PC)) {
                setCharacterId(UUID.fromString(item.get("gsi2pk").s().substring(DB_PC.length())));
            } else if (item.get("gsi2pk").s().startsWith(DB_ROOM)) {
                setRoomId(Long.valueOf(item.get("gsi2pk").s().substring(DB_ROOM.length())));
            }
        }

        Map<String, AttributeValue> data = item.get("data").m();
        setNameList(data.getOrDefault("nameList", AttributeValue.builder().ss().build()).ss());
        setShortDescription(data.getOrDefault("shortDescription", AttributeValue.builder().nul(true).build()).s());
        setLongDescription(data.getOrDefault("longDescription", AttributeValue.builder().nul(true).build()).s());

        List<String> slots = data.getOrDefault("wear_slots", AttributeValue.builder().nul(true).build()).ss();
        setWearSlots(slots.stream().map(WearSlot::valueOf).toList());

        String worn = data.getOrDefault("worn", AttributeValue.builder().nul(true).build()).s();

        if (worn != null) {
            setWorn(WearSlot.valueOf(worn));
        }
    }

    public MudItem buildInstance() {
        if (!isPrototype()) {
            throw new IllegalStateException("cannot build instance from instance");
        }

        MudItem instance = new MudItem();

        instance.setPrototype(false);
        instance.setId(getId());
        instance.setNameList(new ArrayList<>(getNameList()));
        instance.setShortDescription(getShortDescription());
        instance.setLongDescription(getLongDescription());
        instance.setWearSlots(getWearSlots());
        instance.setWorn(getWorn());

        return instance;
    }

    public UUID getId() {
        return id;
    }

    public void setId(UUID id) {
        this.id = id;
    }

    public UUID getCharacterId() {
        if (isPrototype()) {
            throw new IllegalStateException("character ID is not available on prototype");
        }

        if (TYPE_PC.equals(containerType)) {
            return UUID.fromString(containerId);
        }

        return null;
    }

    public void setCharacterId(UUID characterId) {
        containerType = TYPE_PC;
        containerId = characterId.toString();
    }

    public Long getRoomId() {
        if (isPrototype()) {
            throw new IllegalStateException("room ID is not available on prototype");
        }

        if (TYPE_ROOM.equals(containerType)) {
            return Long.valueOf(containerId);
        }

        return null;
    }

    public void setRoomId(Long roomId) {
        containerType = TYPE_ROOM;
        containerId = roomId.toString();
    }

    public List<String> getNameList() {
        return nameList;
    }

    public void setNameList(List<String> nameList) {
        this.nameList = nameList;
    }

    public String getShortDescription() {
        return shortDescription;
    }

    public void setShortDescription(String shortDescription) {
        this.shortDescription = shortDescription;
    }

    public String getLongDescription() {
        return longDescription;
    }

    public void setLongDescription(String longDescription) {
        this.longDescription = longDescription;
    }

    public List<WearSlot> getWearSlots() {
        return new ArrayList<>(wearSlots);
    }

    public void setWearSlots(List<WearSlot> wearSlots) {
        this.wearSlots = wearSlots;
    }

    public WearSlot getWorn() {
        return worn;
    }

    public void setWorn(WearSlot worn) {
        this.worn = worn;
    }

    public boolean isPrototype() {
        return instanceId == null;
    }

    public void setPrototype(boolean prototype) {
        if (prototype) {
            instanceId = null;
        } else {
            instanceId = UUID.randomUUID();
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
