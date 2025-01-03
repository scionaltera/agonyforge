package com.agonyforge.mud.demo.model.impl;

import com.agonyforge.mud.demo.model.constant.WearMode;
import com.agonyforge.mud.demo.model.constant.WearSlot;
import jakarta.persistence.*;

import java.util.EnumSet;
import java.util.HashSet;
import java.util.Objects;
import java.util.Set;

@Entity
public class ItemComponent extends Persistent {
    @Id
    @GeneratedValue(strategy = GenerationType.AUTO)
    private Long id;

    @ElementCollection
    @CollectionTable(joinColumns = {@JoinColumn(name = "item_id", referencedColumnName = "id")})
    private Set<String> nameList = new HashSet<>();

    private String shortDescription;
    private String longDescription;

    @Convert(converter = WearSlot.Converter.class)
    private EnumSet<WearSlot> wearSlots = EnumSet.noneOf(WearSlot.class);
    private WearMode wearMode = WearMode.ALL;

    public Long getId() {
        return id;
    }

    public void setId(Long id) {
        this.id = id;
    }

    public Set<String> getNameList() {
        return nameList;
    }

    public void setNameList(Set<String> nameList) {
        this.nameList = new HashSet<>(nameList);
    }

    public String getShortDescription() {
        return shortDescription;
    }

    public void setShortDescription(String shortDescription) {
        this.shortDescription = shortDescription;
    }

    public String getLongDescription() {
        return longDescription;
    }

    public void setLongDescription(String longDescription) {
        this.longDescription = longDescription;
    }

    public EnumSet<WearSlot> getWearSlots() {
        return wearSlots;
    }

    public void setWearSlots(EnumSet<WearSlot> wearSlots) {
        this.wearSlots = wearSlots;
    }

    public WearMode getWearMode() {
        return wearMode;
    }

    public void setWearMode(WearMode wearMode) {
        this.wearMode = wearMode;
    }

    @Override
    public boolean equals(Object o) {
        if (o == null || getClass() != o.getClass()) return false;
        ItemComponent that = (ItemComponent) o;
        return Objects.equals(id, that.id);
    }

    @Override
    public int hashCode() {
        return Objects.hashCode(id);
    }
}
