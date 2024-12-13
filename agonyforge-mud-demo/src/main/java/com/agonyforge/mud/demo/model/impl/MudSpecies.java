package com.agonyforge.mud.demo.model.impl;

import com.agonyforge.mud.demo.model.constant.Effort;
import com.agonyforge.mud.demo.model.constant.Stat;
import jakarta.persistence.*;

import java.util.HashMap;
import java.util.Map;
import java.util.Objects;

@Entity
@Table(name = "mud_species")
public class MudSpecies extends Persistent {
    @Id
    @GeneratedValue(strategy = GenerationType.AUTO)
    private Long id;
    private String name;

    @ElementCollection
    @CollectionTable(name = "mud_species_stat_mapping",
    joinColumns = {@JoinColumn(name = "species_id", referencedColumnName = "id")})
    @MapKeyColumn(name = "stat_id")
    private final Map<Stat, Integer> stats = new HashMap<>();

    @ElementCollection
    @CollectionTable(name = "mud_species_effort_mapping",
    joinColumns = {@JoinColumn(name = "species_id", referencedColumnName = "id")})
    @MapKeyColumn(name = "effort_id")
    private final Map<Effort, Integer> efforts = new HashMap<>();

    public Long getId() {
        return id;
    }

    public void setId(Long id) {
        this.id = id;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public int getEffort(Effort effort) {
        Integer value = efforts.get(effort);

        return value == null ? 0 : value;
    }

    public void setEffort(Effort effort, int value) {
        efforts.put(effort, value);
    }

    public void addEffort(Effort effort, int addend) {
        efforts.put(effort, efforts.get(effort) + addend);
    }

    public int getStat(Stat stat) {
        Integer value = stats.get(stat);

        return value == null ? 0 : value;
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
        if (!(o instanceof MudSpecies that)) return false;
        return Objects.equals(getId(), that.getId());
    }

    @Override
    public int hashCode() {
        return Objects.hash(getId());
    }
}
