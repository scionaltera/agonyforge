package com.agonyforge.mud.demo.model.impl;

import com.agonyforge.mud.models.dynamodb.Persistent;
import software.amazon.awssdk.services.dynamodb.model.AttributeValue;

import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Objects;
import java.util.stream.Collectors;

import static com.agonyforge.mud.demo.model.impl.Constants.DB_ROOM;
import static com.agonyforge.mud.demo.model.impl.Constants.DB_ZONE;
import static com.agonyforge.mud.demo.model.impl.Constants.SORT_DATA;
import static com.agonyforge.mud.demo.model.impl.Constants.TYPE_ROOM;

public class MudRoom implements Persistent {
    private Long id;
    private Long zoneId;
    private String name;
    private String description;
    private final Map<String, Exit> exits = new HashMap<>();

    public static class Exit implements Persistent {
        private Long destinationId;

        @Override
        public Map<String, AttributeValue> freeze() {
            Map<String, AttributeValue> map = new HashMap<>();

            map.put("destination", AttributeValue.builder().s(destinationId.toString()).build());

            return map;
        }

        @Override
        public void thaw(Map<String, AttributeValue> item) {
            setDestinationId(Long.valueOf(item.getOrDefault("destination", AttributeValue.builder().nul(true).build()).s()));
        }

        public Exit(Long destinationId) {
            this.destinationId = destinationId;
        }

        private Exit() {}

        public Long getDestinationId() {
            return destinationId;
        }

        public void setDestinationId(Long destinationId) {
            this.destinationId = destinationId;
        }
    }

    @Override
    public Map<String, AttributeValue> freeze() {
        Map<String, AttributeValue> map = new HashMap<>();
        Map<String, AttributeValue> data = new HashMap<>();
        List<AttributeValue> exitNames;

        map.put("pk", AttributeValue.builder().s(DB_ROOM + getId()).build());
        map.put("sk", AttributeValue.builder().s(SORT_DATA).build());
        map.put("gsi1pk", AttributeValue.builder().s(TYPE_ROOM).build());
        map.put("gsi2pk", AttributeValue.builder().s(DB_ZONE + zoneId).build());

        exitNames = exits.keySet()
            .stream()
            .map(key -> {
                Exit exit = exits.get(key);
                String name = "exit_" + key;

                data.put(name, AttributeValue.builder().m(exit.freeze()).build());

                return name;
            })
            .map(name -> AttributeValue.builder().s(name).build())
            .collect(Collectors.toList());

        data.put("name", AttributeValue.builder().s(getName()).build());
        data.put("description", AttributeValue.builder().s(getDescription()).build());
        data.put("exit_names", AttributeValue.builder().l(exitNames).build());

        map.put("data", AttributeValue.builder().m(data).build());

        return map;
    }

    @Override
    public void thaw(Map<String, AttributeValue> item) {
        setId(Long.valueOf(item.get("pk").s().substring(DB_ROOM.length())));
        setZoneId(Long.valueOf(item.get("gsi2pk").s().substring(DB_ZONE.length())));

        Map<String, AttributeValue> data = item.get("data").m();
        List<AttributeValue> exitNames = data.get("exit_names").l();

        exitNames
            .forEach(name -> {
                Map<String, AttributeValue> map = data.get(name.s()).m();
                String exitName = name.s().substring("exit_".length());
                MudRoom.Exit exit = new MudRoom.Exit();

                exit.thaw(map);
                exits.put(exitName, exit);
            });

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

    public List<String> getExits() {
        return exits.keySet()
            .stream()
            .sorted()
            .collect(Collectors.toList());
    }

    public Exit getExit(String direction) {
        return exits.get(direction);
    }

    public void setExit(String direction, Exit exit) {
        exits.put(direction, exit);
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
