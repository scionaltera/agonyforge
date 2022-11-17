package com.agonyforge.mud.models.dynamodb.impl;

import com.agonyforge.mud.models.dynamodb.Persistent;
import software.amazon.awssdk.services.dynamodb.model.AttributeValue;

import java.util.HashMap;
import java.util.Map;
import java.util.UUID;

import static com.agonyforge.mud.models.dynamodb.impl.Constants.DB_PC;
import static com.agonyforge.mud.models.dynamodb.impl.Constants.DB_USER;
import static com.agonyforge.mud.models.dynamodb.impl.Constants.SORT_DATA;
import static com.agonyforge.mud.models.dynamodb.impl.Constants.TYPE_PC;

public class MudCharacter implements Persistent {
    private UUID id;
    private String user;
    private String name;

    @Override
    public Map<String, AttributeValue> freeze() {
        Map<String, AttributeValue> map = new HashMap<>();
        Map<String, AttributeValue> data = new HashMap<>();

        data.put("name", AttributeValue.builder().s(getName()).build());

        map.put("pk", AttributeValue.builder().s(DB_PC + getId()).build());
        map.put("sk", AttributeValue.builder().s(SORT_DATA).build());
        map.put("gsi1pk", AttributeValue.builder().s(TYPE_PC).build());
        map.put("gsi2pk", AttributeValue.builder().s(DB_USER + getUser()).build());
        map.put("data", AttributeValue.builder().m(data).build());

        return map;
    }

    @Override
    public void thaw(Map<String, AttributeValue> item) {
        setId(UUID.fromString(item.get("pk").s().substring(DB_PC.length())));
        setUser(item.get("gsi2pk").s().substring(DB_USER.length()));

        Map<String, AttributeValue> data = item.get("data").m();

        setName(data.getOrDefault("name", AttributeValue.builder().nul(true).build()).s());
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

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }
}
