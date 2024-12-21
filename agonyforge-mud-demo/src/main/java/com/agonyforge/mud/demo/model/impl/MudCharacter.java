package com.agonyforge.mud.demo.model.impl;

import com.agonyforge.mud.demo.model.constant.Effort;
import com.agonyforge.mud.demo.model.constant.Pronoun;
import com.agonyforge.mud.demo.model.constant.Stat;
import com.agonyforge.mud.demo.model.constant.WearSlot;
import jakarta.persistence.*;

import java.util.*;

@Entity
@Table(name = "mud_character")
public class MudCharacter extends Persistent {
    @Id
    @GeneratedValue(strategy = GenerationType.AUTO)
    private Long id;
    private Long prototypeId;
    private String username;
    private String webSocketSession;
    private Long roomId;
    private String name;
    private Pronoun pronoun;

    @ManyToMany
    private Set<Role> roles = new HashSet<>();

    @ElementCollection
    @CollectionTable(name = "mud_character_wearslot_mapping",
    joinColumns = {@JoinColumn(name = "character_id", referencedColumnName = "id")})
    private List<WearSlot> wearSlots = new ArrayList<>();

    @ElementCollection
    @CollectionTable(name = "mud_character_stat_mapping",
    joinColumns = {@JoinColumn(name = "character_id", referencedColumnName = "id")})
    @MapKeyColumn(name = "stat_id")
    private final Map<Stat, Integer> stats = new HashMap<>();

    @ElementCollection
    @CollectionTable(name = "mud_character_effort_mapping",
    joinColumns = {@JoinColumn(name = "character_id", referencedColumnName = "id")})
    @MapKeyColumn(name = "effort_id")
    private final Map<Effort, Integer> efforts = new HashMap<>();

    @ElementCollection
    @CollectionTable(name = "mud_character_speciesstats_mapping",
    joinColumns = {@JoinColumn(name = "character_id", referencedColumnName = "id")})
    @MapKeyColumn(name = "stat_id")
    private final Map<Stat, Integer> speciesStats = new HashMap<>();

    @ElementCollection
    @CollectionTable(name = "mud_character_specieseffort_mapping",
    joinColumns = {@JoinColumn(name = "character_id", referencedColumnName = "id")})
    @MapKeyColumn(name = "effort_id")
    private final Map<Effort, Integer> speciesEfforts = new HashMap<>();

    @ElementCollection
    @CollectionTable(name = "mud_character_professionstats_mapping",
    joinColumns = {@JoinColumn(name = "character_id", referencedColumnName = "id")})
    @MapKeyColumn(name = "stat_id")
    private final Map<Stat, Integer> professionStats = new HashMap<>();

    @ElementCollection
    @CollectionTable(name = "mud_character_professioneffort_mapping",
    joinColumns = {@JoinColumn(name = "character_id", referencedColumnName = "id")})
    @MapKeyColumn(name = "effort_id")
    private final Map<Effort, Integer> professionEfforts = new HashMap<>();

    private Long speciesId;
    private Long professionId;

    public MudCharacter() {
        Arrays.stream(Stat.values()).forEach((stat) -> stats.put(stat, 0));
        Arrays.stream(Effort.values()).forEach((effort) -> efforts.put(effort, 0));
        Arrays.stream(Stat.values()).forEach((stat) -> speciesStats.put(stat, 0));
        Arrays.stream(Effort.values()).forEach((effort) -> speciesEfforts.put(effort, 0));
        Arrays.stream(Stat.values()).forEach((stat) -> professionStats.put(stat, 0));
        Arrays.stream(Effort.values()).forEach((effort) -> professionEfforts.put(effort, 0));
    }

    public Long getId() {
        return id;
    }

    public void setId(Long id) {
        this.id = id;
    }

    public Long getPrototypeId() {
        return prototypeId;
    }

    public void setPrototypeId(Long prototypeId) {
        this.prototypeId = prototypeId;
    }

    public String getUsername() {
        return username;
    }

    public void setUsername(String user) {
        this.username = user;
    }

    public String getWebSocketSession() {
        return webSocketSession;
    }

    public void setWebSocketSession(String webSocketSession) {
        this.webSocketSession = webSocketSession;
    }

    public Long getZoneId() {
        String roomIdString = getRoomId().toString();

        return Long.valueOf(roomIdString.substring(0, roomIdString.length() - 2));
    }

    public Long getRoomId() {
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

    public Set<Role> getRoles() {
        return roles;
    }

    public void setRoles(Set<Role> roles) {
        this.roles = roles;
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

    public Long getSpeciesId() {
        return speciesId;
    }

    public void setSpeciesId(Long speciesId) {
        this.speciesId = speciesId;
    }

    public Long getProfessionId() {
        return professionId;
    }

    public void setProfessionId(Long professionId) {
        this.professionId = professionId;
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (!(o instanceof MudCharacter)) return false;
        MudCharacter that = (MudCharacter) o;
        return Objects.equals(getId(), that.getId());
    }

    @Override
    public int hashCode() {
        return Objects.hash(getId());
    }
}
