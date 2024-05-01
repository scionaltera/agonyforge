package com.agonyforge.mud.demo.model.impl;

import com.agonyforge.mud.models.dynamodb.Persistent;
import com.agonyforge.mud.demo.model.constant.Effort;
import com.agonyforge.mud.demo.model.constant.Pronoun;
import com.agonyforge.mud.demo.model.constant.Stat;
import com.agonyforge.mud.demo.model.constant.WearSlot;
import software.amazon.awssdk.services.dynamodb.model.AttributeValue;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Objects;

import java.util.UUID;

import static com.agonyforge.mud.demo.config.ProfessionLoader.DEFAULT_PROFESSION_ID;
import static com.agonyforge.mud.demo.config.SpeciesLoader.DEFAULT_SPECIES_ID;
import static com.agonyforge.mud.demo.model.impl.ModelConstants.DB_PC;
import static com.agonyforge.mud.demo.model.impl.ModelConstants.DB_ROOM;
import static com.agonyforge.mud.demo.model.impl.ModelConstants.DB_USER;
import static com.agonyforge.mud.demo.model.impl.ModelConstants.SORT_DATA;
import static com.agonyforge.mud.demo.model.impl.ModelConstants.SORT_INSTANCE;
import static com.agonyforge.mud.demo.model.impl.ModelConstants.TYPE_PC;

public class MudCharacter implements Persistent {
    private UUID id;
    private String user;
    private String webSocketSession;
    private Long roomId;
    private boolean isPrototype = true;
    private String name;
    private Pronoun pronoun;
    private List<WearSlot> wearSlots = new ArrayList<>();
    private final Map<Stat, Integer> stats = new HashMap<>();
    private final Map<Effort, Integer> efforts = new HashMap<>();
    private final Map<Stat, Integer> speciesStats = new HashMap<>();
    private final Map<Effort, Integer> speciesEfforts = new HashMap<>();
    private final Map<Stat, Integer> professionStats = new HashMap<>();
    private final Map<Effort, Integer> professionEfforts = new HashMap<>();
    private UUID speciesId;
    private UUID professionId;

    public MudCharacter() {
        Arrays.stream(Stat.values()).forEach((stat) -> stats.put(stat, 0));
        Arrays.stream(Effort.values()).forEach((effort) -> efforts.put(effort, 0));
        Arrays.stream(Stat.values()).forEach((stat) -> speciesStats.put(stat, 0));
        Arrays.stream(Effort.values()).forEach((effort) -> speciesEfforts.put(effort, 0));
        Arrays.stream(Stat.values()).forEach((stat) -> professionStats.put(stat, 0));
        Arrays.stream(Effort.values()).forEach((effort) -> professionEfforts.put(effort, 0));
    }

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

        if (getPronoun() != null) {
            data.put("pronoun", AttributeValue.builder().s(getPronoun().name()).build());
        } else {
            data.put("pronoun", AttributeValue.builder().s(Pronoun.IT.name()).build());
        }

        if (!getWearSlots().isEmpty()) {
            List<String> slots = wearSlots
                .stream()
                .map(WearSlot::name)
                .toList();

            data.put("wear_slots", AttributeValue.builder().ss(slots).build());
        }

        Map<String, AttributeValue> stats = new HashMap<>();
        Map<String, AttributeValue> efforts = new HashMap<>();
        Map<String, AttributeValue> speciesStats = new HashMap<>();
        Map<String, AttributeValue> speciesEfforts = new HashMap<>();
        Map<String, AttributeValue> professionStats = new HashMap<>();
        Map<String, AttributeValue> professionEfforts = new HashMap<>();

        Arrays.stream(Stat.values())
            .forEach(stat -> {
                stats.put(stat.getName(), AttributeValue.builder().n(Integer.toString(getBaseStat(stat))).build());
                speciesStats.put(stat.getName(), AttributeValue.builder().n(Integer.toString(getSpeciesStat(stat))).build());
                professionStats.put(stat.getName(), AttributeValue.builder().n(Integer.toString(getProfessionStat(stat))).build());
            });
        Arrays.stream(Effort.values())
            .forEach(effort -> {
                efforts.put(effort.getName(), AttributeValue.builder().n(Integer.toString(getBaseEffort(effort))).build());
                speciesEfforts.put(effort.getName(), AttributeValue.builder().n(Integer.toString(getSpeciesEffort(effort))).build());
                professionEfforts.put(effort.getName(), AttributeValue.builder().n(Integer.toString(getProfessionEffort(effort))).build());
            });

        data.put("stats", AttributeValue.builder().m(stats).build());
        data.put("efforts", AttributeValue.builder().m(efforts).build());
        data.put("species_stats", AttributeValue.builder().m(speciesStats).build());
        data.put("species_efforts", AttributeValue.builder().m(speciesEfforts).build());
        data.put("profession_stats", AttributeValue.builder().m(professionStats).build());
        data.put("profession_efforts", AttributeValue.builder().m(professionEfforts).build());

        if (getSpeciesId() != null) {
            data.put("species", AttributeValue.builder().s(speciesId.toString()).build());
        } else {
            data.put("species", AttributeValue.builder().s(DEFAULT_SPECIES_ID.toString()).build());
        }

        if (getProfessionId() != null) {
            data.put("profession", AttributeValue.builder().s(professionId.toString()).build());
        } else {
            data.put("profession", AttributeValue.builder().s(DEFAULT_PROFESSION_ID.toString()).build());
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
        setPronoun(Pronoun.valueOf(data.getOrDefault("pronoun", AttributeValue.builder().s("IT").build()).s()));

        List<String> slots = data.getOrDefault("wear_slots", AttributeValue.builder().nul(true).build()).ss();
        setWearSlots(slots
            .stream()
            .map(WearSlot::valueOf)
            .toList());

        Map<String, AttributeValue> stats = data.get("stats").m();
        Map<String, AttributeValue> efforts = data.get("efforts").m();
        Map<String, AttributeValue> speciesStats = data.get("species_stats").m();
        Map<String, AttributeValue> speciesEfforts = data.get("species_efforts").m();
        Map<String, AttributeValue> professionStats = data.get("profession_stats").m();
        Map<String, AttributeValue> professionEfforts = data.get("profession_efforts").m();

        Arrays.stream(Stat.values())
            .forEach(stat -> {
                setBaseStat(stat, Integer.parseInt(stats.getOrDefault(stat.getName(), AttributeValue.builder().n("0").build()).n()));
                setSpeciesStat(stat, Integer.parseInt(speciesStats.getOrDefault(stat.getName(), AttributeValue.builder().n("0").build()).n()));
                setProfessionStat(stat, Integer.parseInt(professionStats.getOrDefault(stat.getName(), AttributeValue.builder().n("0").build()).n()));
            });
        Arrays.stream(Effort.values())
            .forEach(effort -> {
                setBaseEffort(effort, Integer.parseInt(efforts.getOrDefault(effort.getName(), AttributeValue.builder().n("0").build()).n()));
                setSpeciesEffort(effort, Integer.parseInt(speciesEfforts.getOrDefault(effort.getName(), AttributeValue.builder().n("0").build()).n()));
                setProfessionEffort(effort, Integer.parseInt(professionEfforts.getOrDefault(effort.getName(), AttributeValue.builder().n("0").build()).n()));
            });

        setSpeciesId(UUID.fromString(data.get("species").s()));
        setProfessionId(UUID.fromString(data.get("profession").s()));
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
        instance.setPronoun(getPronoun());
        instance.setWearSlots(getWearSlots());
        instance.setSpeciesId(getSpeciesId());
        instance.setProfessionId(getProfessionId());

        Arrays.stream(Stat.values())
            .forEach(stat -> {
                instance.setBaseStat(stat, getBaseStat(stat));
                instance.setSpeciesStat(stat, getSpeciesStat(stat));
                instance.setProfessionStat(stat, getProfessionStat(stat));
            });
        Arrays.stream(Effort.values())
            .forEach(effort -> {
                instance.setBaseEffort(effort, getBaseEffort(effort));
                instance.setSpeciesEffort(effort, getSpeciesEffort(effort));
                instance.setProfessionEffort(effort, getProfessionEffort(effort));
            });

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

    public Pronoun getPronoun() {
        return pronoun;
    }

    public void setPronoun(Pronoun pronoun) {
        this.pronoun = pronoun;
    }

    public List<WearSlot> getWearSlots() {
        return new ArrayList<>(wearSlots);
    }

    public void setWearSlots(List<WearSlot> wearSlots) {
        this.wearSlots = wearSlots;
    }

    public int getStat(Stat stat) {
        return stats.get(stat) + speciesStats.get(stat) + professionStats.get(stat);
    }

    public int getBaseStat(Stat stat) {
        return stats.get(stat);
    }

    public void setBaseStat(Stat stat, int value) {
        stats.put(stat, value);
    }

    public void addBaseStat(Stat stat, int addend) {
        stats.put(stat, stats.get(stat) + addend);
    }

    public int getSpeciesStat(Stat stat) {
        return speciesStats.get(stat);
    }

    public void setSpeciesStat(Stat stat, int value) {
        speciesStats.put(stat, value);
    }

    public void addSpeciesStat(Stat stat, int addend) {
        speciesStats.put(stat, speciesStats.get(stat) + addend);
    }

    public int getProfessionStat(Stat stat) {
        return professionStats.get(stat);
    }

    public void setProfessionStat(Stat stat, int value) {
        professionStats.put(stat, value);
    }

    public void addProfessionStat(Stat stat, int addend) {
        professionStats.put(stat, professionStats.get(stat) + addend);
    }

    public int getDefense() {
        return getBaseStat(Stat.CON) + getSpeciesStat(Stat.CON);
    }

    public int getEffort(Effort effort) {
        return efforts.get(effort) + speciesEfforts.get(effort) + professionEfforts.get(effort);
    }

    public int getBaseEffort(Effort effort) {
        return efforts.get(effort);
    }

    public void setBaseEffort(Effort effort, int value) {
        efforts.put(effort, value);
    }

    public void addBaseEffort(Effort effort, int addend) {
        efforts.put(effort, efforts.get(effort) + addend);
    }

    public int getSpeciesEffort(Effort effort) {
        return speciesEfforts.get(effort);
    }

    public void setSpeciesEffort(Effort effort, int value) {
        speciesEfforts.put(effort, value);
    }

    public void addSpeciesEffort(Effort effort, int addend) {
        speciesEfforts.put(effort, speciesEfforts.get(effort) + addend);
    }

    public int getProfessionEffort(Effort effort) {
        return professionEfforts.get(effort);
    }

    public void setProfessionEffort(Effort effort, int value) {
        professionEfforts.put(effort, value);
    }

    public void addProfessionEffort(Effort effort, int addend) {
        professionEfforts.put(effort, professionEfforts.get(effort) + addend);
    }

    public UUID getSpeciesId() {
        return speciesId;
    }

    public void setSpeciesId(UUID speciesId) {
        this.speciesId = speciesId;
    }

    public UUID getProfessionId() {
        return professionId;
    }

    public void setProfessionId(UUID professionId) {
        this.professionId = professionId;
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
