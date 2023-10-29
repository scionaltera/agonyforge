package com.agonyforge.mud.demo.model.impl;

import com.agonyforge.mud.demo.model.constant.Effort;
import com.agonyforge.mud.demo.model.constant.Stat;
import com.agonyforge.mud.models.dynamodb.Persistent;
import software.amazon.awssdk.services.dynamodb.model.AttributeValue;

import java.util.Arrays;
import java.util.HashMap;
import java.util.Map;
import java.util.Objects;
import java.util.UUID;

import static com.agonyforge.mud.demo.model.impl.ModelConstants.DB_PROFESSION;
import static com.agonyforge.mud.demo.model.impl.ModelConstants.SORT_DATA;
import static com.agonyforge.mud.demo.model.impl.ModelConstants.TYPE_PROFESSION;

public class MudProfession implements Persistent {
    private UUID id;
    private String name;
    private final Map<Stat, Integer> stats = new HashMap<>();
    private final Map<Effort, Integer> efforts = new HashMap<>();

    public MudProfession() {
        Arrays.stream(Stat.values()).forEach((stat) -> stats.put(stat, 0));
        Arrays.stream(Effort.values()).forEach((effort) -> efforts.put(effort, 0));
    }

    @Override
    public Map<String, AttributeValue> freeze() {
        Map<String, AttributeValue> map = new HashMap<>();
        Map<String, AttributeValue> data = new HashMap<>();

        map.put("pk", AttributeValue.builder().s(DB_PROFESSION + getId()).build());
        map.put("sk", AttributeValue.builder().s(SORT_DATA).build());
        map.put("gsi1pk", AttributeValue.builder().s(TYPE_PROFESSION).build());

        data.put("name", AttributeValue.builder().s(getName()).build());

        Map<String, AttributeValue> stats = new HashMap<>();
        Map<String, AttributeValue> efforts = new HashMap<>();

        Arrays.stream(Stat.values())
            .forEach(stat -> stats.put(stat.getName(), AttributeValue.builder().n(Integer.toString(getStat(stat))).build()));
        Arrays.stream(Effort.values())
            .forEach(effort -> efforts.put(effort.getName(), AttributeValue.builder().n(Integer.toString(getEffort(effort))).build()));

        data.put("stats", AttributeValue.builder().m(stats).build());
        data.put("efforts", AttributeValue.builder().m(efforts).build());

        map.put("data", AttributeValue.builder().m(data).build());

        return map;
    }

    @Override
    public void thaw(Map<String, AttributeValue> item) {
        setId(UUID.fromString(item.get("pk").s().substring(DB_PROFESSION.length())));

        Map<String, AttributeValue> data = item.get("data").m();
        setName(data.getOrDefault("name", AttributeValue.builder().nul(true).build()).s());

        Map<String, AttributeValue> stats = data.get("stats").m();
        Map<String, AttributeValue> efforts = data.get("efforts").m();

        Arrays.stream(Stat.values())
            .forEach(stat -> setStat(stat, Integer.parseInt(stats.getOrDefault(stat.getName(), AttributeValue.builder().n("0").build()).n())));
        Arrays.stream(Effort.values())
            .forEach(effort -> setEffort(effort, Integer.parseInt(efforts.getOrDefault(effort.getName(), AttributeValue.builder().n("0").build()).n())));
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

    public int getEffort(Effort effort) {
        return efforts.get(effort);
    }

    public void setEffort(Effort effort, int value) {
        efforts.put(effort, value);
    }

    public void addEffort(Effort effort, int addend) {
        efforts.put(effort, efforts.get(effort) + addend);
    }

    public int getStat(Stat stat) {
        return stats.get(stat);
    }

    public void setStat(Stat stat, int value) {
        stats.put(stat, value);
    }

    public void addStat(Stat stat, int addend) {
        stats.put(stat, stats.get(stat) + addend);
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (!(o instanceof MudProfession that)) return false;
        return Objects.equals(getId(), that.getId());
    }

    @Override
    public int hashCode() {
        return Objects.hash(getId());
    }
}
