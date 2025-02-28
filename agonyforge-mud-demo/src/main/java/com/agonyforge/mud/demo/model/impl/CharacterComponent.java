package com.agonyforge.mud.demo.model.impl;

import com.agonyforge.mud.demo.model.constant.Effort;
import com.agonyforge.mud.demo.model.constant.Pronoun;
import com.agonyforge.mud.demo.model.constant.Stat;
import com.agonyforge.mud.demo.model.constant.WearSlot;
import jakarta.persistence.*;

import java.util.EnumSet;
import java.util.HashMap;
import java.util.Map;
import java.util.Objects;

@Entity
public class CharacterComponent extends Persistent {
    @Id
    @GeneratedValue(strategy = GenerationType.AUTO)
    private Long id;
    private String name;
    private Pronoun pronoun;
    private Integer maxHitPoints = 10;
    private Integer hitPoints = 10;

    @ManyToOne(cascade = CascadeType.ALL)
    private MudSpecies species;

    @ManyToOne(cascade = CascadeType.ALL)
    private MudProfession profession;

    @Convert(converter = WearSlot.Converter.class)
    private EnumSet<WearSlot> wearSlots = EnumSet.allOf(WearSlot.class);

    @OneToMany(cascade = CascadeType.ALL)
    private Map<Stat, CharacterAttribute> stats = new HashMap<>();

    @OneToMany(cascade = CascadeType.ALL)
    private Map<Effort, CharacterAttribute> efforts = new HashMap<>();

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

    public Pronoun getPronoun() {
        return pronoun;
    }

    public void setPronoun(Pronoun pronoun) {
        this.pronoun = pronoun;
    }

    public Integer getMaxHitPoints() {
        return maxHitPoints;
    }

    public void setMaxHitPoints(Integer maxHitPoints) {
        this.maxHitPoints = maxHitPoints;
    }

    public Integer getHitPoints() {
        return hitPoints;
    }

    public void setHitPoints(Integer hitPoints) {
        this.hitPoints = hitPoints;
    }

    public MudSpecies getSpecies() {
        return species;
    }

    public void setSpecies(MudSpecies species) {
        this.species = species;
    }

    public MudProfession getProfession() {
        return profession;
    }

    public void setProfession(MudProfession profession) {
        this.profession = profession;
    }

    public EnumSet<WearSlot> getWearSlots() {
        return wearSlots;
    }

    public void setWearSlots(EnumSet<WearSlot> wearSlots) {
        this.wearSlots = wearSlots;
    }

    public int getStat(Stat stat) {
        return getBaseStat(stat) + getSpeciesStat(stat) + getProfessionStat(stat);
    }

    public int getEffort(Effort effort) {
        return getBaseEffort(effort) + getSpeciesEffort(effort) + getProfessionEffort(effort);
    }

    public int getBaseStat(Stat stat) {
        return stats.getOrDefault(stat, new CharacterAttribute(0)).getBase();
    }

    public void setBaseStat(Stat stat, int value) {
        CharacterAttribute attribute = stats.getOrDefault(stat, new CharacterAttribute(value));
        attribute.setBase(value);
        stats.put(stat, attribute);
    }

    public void addBaseStat(Stat stat, int addend) {
        CharacterAttribute attribute = stats.getOrDefault(stat, new CharacterAttribute(0));
        attribute.setBase(attribute.getBase() + addend);
        stats.put(stat, attribute);
    }

    public int getSpeciesStat(Stat stat) {
        return getSpecies().getStat(stat);
    }

    public int getProfessionStat(Stat stat) {
        return getProfession().getStat(stat);
    }

    public int getDefense() {
        return getStat(Stat.CON);
    }

    public int getBaseEffort(Effort effort) {
        return efforts.getOrDefault(effort, new CharacterAttribute(0)).getBase();
    }

    public void setBaseEffort(Effort effort, int value) {
        CharacterAttribute attribute = efforts.getOrDefault(effort, new CharacterAttribute(0));
        attribute.setBase(value);
        efforts.put(effort, attribute);
    }

    public void addBaseEffort(Effort effort, int addend) {
        CharacterAttribute attribute = efforts.getOrDefault(effort, new CharacterAttribute(0));
        attribute.setBase(attribute.getBase() + addend);
        efforts.put(effort, attribute);
    }

    public int getSpeciesEffort(Effort effort) {
        return getSpecies().getEffort(effort);
    }

    public int getProfessionEffort(Effort effort) {
        return getProfession().getEffort(effort);
    }

    @Override
    public boolean equals(Object o) {
        if (o == null || getClass() != o.getClass()) return false;
        CharacterComponent that = (CharacterComponent) o;
        return Objects.equals(id, that.id);
    }

    @Override
    public int hashCode() {
        return Objects.hashCode(id);
    }
}
