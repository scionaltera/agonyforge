package com.agonyforge.mud.models.dynamodb.impl;

import com.agonyforge.mud.models.dynamodb.Persistent;
import software.amazon.awssdk.services.dynamodb.model.AttributeValue;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Objects;

import java.util.UUID;

import static com.agonyforge.mud.models.dynamodb.impl.Constants.DB_PC;
import static com.agonyforge.mud.models.dynamodb.impl.Constants.DB_ROOM;
import static com.agonyforge.mud.models.dynamodb.impl.Constants.DB_USER;
import static com.agonyforge.mud.models.dynamodb.impl.Constants.SORT_DATA;
import static com.agonyforge.mud.models.dynamodb.impl.Constants.SORT_INSTANCE;
import static com.agonyforge.mud.models.dynamodb.impl.Constants.TYPE_PC;

public class MudCharacter implements Persistent {
    private UUID id;
    private String user;
    private String webSocketSession;
    private Long roomId;
    private boolean isPrototype = true;
    private String name;
    private List<String> wearSlots = new ArrayList<>();

    @Override
    public Map<String, AttributeValue> freeze() {
        Map<String, AttributeValue> map = new HashMap<>();
        Map<String, AttributeValue> data = new HashMap<>();

        map.put("pk", AttributeValue.builder().s(DB_PC + getId()).build());
        map.put("gsi1pk", AttributeValue.builder().s(TYPE_PC).build());

        if (isPrototype()) {
            map.put("sk", AttributeValue.builder().s(SORT_DATA).build());
            map.put("gsi2pk", AttributeValue.builder().s(DB_USER + getUser()).build());
        } else {
            map.put("sk", AttributeValue.builder().s(SORT_INSTANCE).build());
            map.put("gsi2pk", AttributeValue.builder().s(DB_ROOM + getRoomId()).build());
        }

        data.put("principal", AttributeValue.builder().s(getUser()).build());
        data.put("name", AttributeValue.builder().s(getName()).build());

        if (!getWearSlots().isEmpty()) {
            data.put("wear_slots", AttributeValue.builder().ss(getWearSlots()).build());
        }

        if (!isPrototype()) {
            data.put("webSocketSession", AttributeValue.builder().s(getWebSocketSession()).build());
        }

        map.put("data", AttributeValue.builder().m(data).build());

        return map;
    }

    @Override
    public void thaw(Map<String, AttributeValue> item) {
        setId(UUID.fromString(item.get("pk").s().substring(DB_PC.length())));

        if (SORT_INSTANCE.equals(item.get("sk").s())) {
            setPrototype(false);
            setRoomId(Long.valueOf(item.get("gsi2pk").s().substring(DB_ROOM.length())));
        }

        Map<String, AttributeValue> data = item.get("data").m();
        setUser(data.getOrDefault("principal", AttributeValue.builder().nul(true).build()).s());
        setWebSocketSession(data.getOrDefault("webSocketSession", AttributeValue.builder().nul(true).build()).s());
        setName(data.getOrDefault("name", AttributeValue.builder().nul(true).build()).s());
        setWearSlots(data.getOrDefault("wear_slots", AttributeValue.builder().nul(true).build()).ss());
    }

    public MudCharacter buildInstance() {
        if (!isPrototype()) {
            throw new IllegalStateException("cannot build instance from instance");
        }

        MudCharacter instance = new MudCharacter();

        instance.setPrototype(false);
        instance.setId(getId());
        instance.setUser(getUser());
        instance.setName(getName());
        instance.getWearSlots().addAll(getWearSlots());

        return instance;
    }

    public UUID getId() {
        return id;
    }

    public void setId(UUID id) {
        this.id = id;
    }

    public String getUser() {
        return user;
    }

    public void setUser(String user) {
        this.user = user;
    }

    public String getWebSocketSession() {
        if (isPrototype()) {
            throw new IllegalStateException("webSocketSession is not available on prototype");
        }

        return webSocketSession;
    }

    public void setWebSocketSession(String webSocketSession) {
        this.webSocketSession = webSocketSession;
    }

    public Long getZoneId() {
        if (isPrototype()) {
            throw new IllegalStateException("zone ID is not available on prototype");
        }

        String roomIdString = getRoomId().toString();

        return Long.valueOf(roomIdString.substring(0, roomIdString.length() - 2));
    }

    public Long getRoomId() {
        if (isPrototype()) {
            throw new IllegalStateException("room ID is not available on prototype");
        }

        return roomId;
    }

    public void setRoomId(Long roomId) {
        this.roomId = roomId;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public List<String> getWearSlots() {
        return wearSlots;
    }

    public void setWearSlots(List<String> wearSlots) {
        this.wearSlots = wearSlots;
    }

    public boolean isPrototype() {
        return isPrototype;
    }

    private void setPrototype(boolean prototype) {
        isPrototype = prototype;
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (!(o instanceof MudCharacter)) return false;
        MudCharacter that = (MudCharacter) o;
        return isPrototype() == that.isPrototype() && Objects.equals(getId(), that.getId());
    }

    @Override
    public int hashCode() {
        return Objects.hash(getId(), isPrototype());
    }
}
