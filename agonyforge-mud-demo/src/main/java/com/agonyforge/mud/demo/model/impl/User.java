package com.agonyforge.mud.demo.model.impl;

import com.agonyforge.mud.models.dynamodb.Persistent;
import software.amazon.awssdk.services.dynamodb.model.AttributeValue;

import java.util.HashMap;
import java.util.Map;
import java.util.Objects;

import static com.agonyforge.mud.demo.model.impl.Constants.DB_USER;
import static com.agonyforge.mud.demo.model.impl.Constants.SORT_DATA;
import static com.agonyforge.mud.demo.model.impl.Constants.TYPE_USER;

public class User implements Persistent {
    private String principalName; // Principal name
    private String givenName; // real life name
    private String emailAddress;

    @Override
    public Map<String, AttributeValue> freeze() {
        Map<String, AttributeValue> map = new HashMap<>();
        Map<String, AttributeValue> data = new HashMap<>();

        data.put("givenName", AttributeValue.builder().s(getGivenName()).build());
        data.put("emailAddress", AttributeValue.builder().s(getEmailAddress()).build());

        map.put("pk", AttributeValue.builder().s(DB_USER + getPrincipalName()).build());
        map.put("sk", AttributeValue.builder().s(SORT_DATA).build());
        map.put("gsi1pk", AttributeValue.builder().s(TYPE_USER).build());
        map.put("data", AttributeValue.builder().m(data).build());

        return map;
    }

    @Override
    public void thaw(Map<String, AttributeValue> item) {
        setPrincipalName(item.get("pk").s().substring(DB_USER.length()));

        Map<String, AttributeValue> data = item.get("data").m();

        setGivenName(data.getOrDefault("givenName", AttributeValue.builder().nul(true).build()).s());
        setEmailAddress(data.getOrDefault("emailAddress", AttributeValue.builder().nul(true).build()).s());
    }

    public String getPrincipalName() {
        return principalName;
    }

    public void setPrincipalName(String principalName) {
        this.principalName = principalName;
    }

    public String getGivenName() {
        return givenName;
    }

    public void setGivenName(String givenName) {
        this.givenName = givenName;
    }

    public String getEmailAddress() {
        return emailAddress;
    }

    public void setEmailAddress(String emailAddress) {
        this.emailAddress = emailAddress;
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (!(o instanceof User)) return false;
        User user = (User) o;
        return Objects.equals(getPrincipalName(), user.getPrincipalName());
    }

    @Override
    public int hashCode() {
        return Objects.hash(getPrincipalName());
    }
}
