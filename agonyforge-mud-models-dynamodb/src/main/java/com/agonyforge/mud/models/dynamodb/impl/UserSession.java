package com.agonyforge.mud.models.dynamodb.impl;

import com.agonyforge.mud.models.dynamodb.Persistent;
import software.amazon.awssdk.services.dynamodb.model.AttributeValue;

import java.util.Date;
import java.util.HashMap;
import java.util.Map;

import static com.agonyforge.mud.models.dynamodb.impl.Constants.DB_USER;
import static com.agonyforge.mud.models.dynamodb.impl.Constants.ISO_8601;
import static com.agonyforge.mud.models.dynamodb.impl.Constants.SORT_DATA;
import static com.agonyforge.mud.models.dynamodb.impl.Constants.SORT_SESSION;
import static com.agonyforge.mud.models.dynamodb.impl.Constants.TYPE_SESSION;
import static com.agonyforge.mud.models.dynamodb.impl.Constants.TYPE_USER;

public class UserSession implements Persistent {
    private String principalName;
    private String remoteIpAddress;

    @Override
    public Map<String, AttributeValue> freeze() {
        Map<String, AttributeValue> map = new HashMap<>();
        Map<String, AttributeValue> data = new HashMap<>();

        data.put("remoteIpAddress", AttributeValue.builder().s(getRemoteIpAddress()).build());

        map.put("pk", AttributeValue.builder().s(DB_USER + getPrincipalName()).build());
        map.put("sk", AttributeValue.builder().s(SORT_SESSION + ISO_8601.format(new Date())).build());
        map.put("gsi1pk", AttributeValue.builder().s(TYPE_SESSION).build());
        map.put("data", AttributeValue.builder().m(data).build());

        return map;
    }

    @Override
    public void thaw(Map<String, AttributeValue> item) {
        setPrincipalName(item.get("pk").s().substring(DB_USER.length()));

        Map<String, AttributeValue> data = item.get("data").m();

        setRemoteIpAddress(data.getOrDefault("remoteIpAddress", AttributeValue.builder().nul(true).build()).s());
    }

    public String getPrincipalName() {
        return principalName;
    }

    public void setPrincipalName(String principalName) {
        this.principalName = principalName;
    }

    public String getRemoteIpAddress() {
        return remoteIpAddress;
    }

    public void setRemoteIpAddress(String remoteIpAddress) {
        this.remoteIpAddress = remoteIpAddress;
    }
}
