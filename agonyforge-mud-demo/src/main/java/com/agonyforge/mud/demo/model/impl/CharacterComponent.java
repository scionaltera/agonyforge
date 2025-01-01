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
