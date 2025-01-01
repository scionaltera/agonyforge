package com.agonyforge.mud.demo.model.impl;

import com.agonyforge.mud.demo.model.constant.Effort;
import com.agonyforge.mud.demo.model.constant.Stat;
import jakarta.persistence.*;

import java.util.*;

@MappedSuperclass
public abstract class AbstractMudCharacter extends AbstractMudObject {
    public AbstractMudCharacter() {
        Arrays.stream(Stat.values()).forEach((stat) -> stats.put(stat, 0));
        Arrays.stream(Effort.values()).forEach((effort) -> efforts.put(effort, 0));
        Arrays.stream(Stat.values()).forEach((stat) -> speciesStats.put(stat, 0));
        Arrays.stream(Effort.values()).forEach((effort) -> speciesEfforts.put(effort, 0));
        Arrays.stream(Stat.values()).forEach((stat) -> professionStats.put(stat, 0));
        Arrays.stream(Effort.values()).forEach((effort) -> professionEfforts.put(effort, 0));
    }

    @ElementCollection
    @CollectionTable(joinColumns = {@JoinColumn(name = "character_id", referencedColumnName = "id")})
    @MapKeyColumn(name = "stat_id")
    private final Map<Stat, Integer> stats = new HashMap<>();

    @ElementCollection
    @CollectionTable(joinColumns = {@JoinColumn(name = "character_id", referencedColumnName = "id")})
    @MapKeyColumn(name = "effort_id")
    private final Map<Effort, Integer> efforts = new HashMap<>();

    @ElementCollection
    @CollectionTable(joinColumns = {@JoinColumn(name = "character_id", referencedColumnName = "id")})
    @MapKeyColumn(name = "stat_id")
    private final Map<Stat, Integer> speciesStats = new HashMap<>();

    @ElementCollection
    @CollectionTable(joinColumns = {@JoinColumn(name = "character_id", referencedColumnName = "id")})
    @MapKeyColumn(name = "effort_id")
    private final Map<Effort, Integer> speciesEfforts = new HashMap<>();

    @ElementCollection
    @CollectionTable(joinColumns = {@JoinColumn(name = "character_id", referencedColumnName = "id")})
    @MapKeyColumn(name = "stat_id")
    private final Map<Stat, Integer> professionStats = new HashMap<>();

    @ElementCollection
    @CollectionTable(joinColumns = {@JoinColumn(name = "character_id", referencedColumnName = "id")})
    @MapKeyColumn(name = "effort_id")
    private final Map<Effort, Integer> professionEfforts = new HashMap<>();

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
}
