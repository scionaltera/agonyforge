package com.agonyforge.mud.models.dynamodb.impl;

import com.agonyforge.mud.models.dynamodb.Persistent;
import software.amazon.awssdk.services.dynamodb.model.AttributeValue;

import java.util.HashMap;
import java.util.Map;
import java.util.Objects;

import static com.agonyforge.mud.models.dynamodb.impl.Constants.SORT_COMMAND;
import static com.agonyforge.mud.models.dynamodb.impl.Constants.TYPE_COMMAND;

public class CommandReference implements Persistent {
    private String name;
    private int priority;
    private String beanName;

    @Override
    public Map<String, AttributeValue> freeze() {
        Map<String, AttributeValue> map = new HashMap<>();
        Map<String, AttributeValue> data = new HashMap<>();

        map.put("pk", AttributeValue.builder().s(priority + "#" + getName()).build());
        map.put("sk", AttributeValue.builder().s(SORT_COMMAND + getName()).build());
        map.put("gsi1pk", AttributeValue.builder().s(TYPE_COMMAND).build());

        data.put("beanName", AttributeValue.builder().s(getBeanName()).build());
        map.put("data", AttributeValue.builder().m(data).build());

        return map;
    }

    @Override
    public void thaw(Map<String, AttributeValue> item) {
        String pkTemp = item.get("pk").s();
        setPriority(Integer.parseInt(pkTemp.substring(0, pkTemp.indexOf("#"))));
        setName(item.get("sk").s().substring(SORT_COMMAND.length()));

        Map<String, AttributeValue> data = item.get("data").m();
        setBeanName(data.get("beanName").s());
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public int getPriority() {
        return priority;
    }

    public void setPriority(int priority) {
        this.priority = priority;
    }

    public String getBeanName() {
        return beanName;
    }

    public void setBeanName(String beanName) {
        this.beanName = beanName;
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (!(o instanceof CommandReference)) return false;
        CommandReference that = (CommandReference) o;
        return Objects.equals(getName(), that.getName());
    }

    @Override
    public int hashCode() {
        return Objects.hash(getName());
    }
}
