package com.agonyforge.mud.demo.model.impl;

import com.agonyforge.mud.demo.model.constant.WearSlot;
import jakarta.persistence.*;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.EnumSet;
import java.util.List;

@MappedSuperclass
public abstract class AbstractMudItem extends AbstractMudObject {
    @Transient
    private List<String> transientNameList = new ArrayList<>();

    @Column(name = "namelist")
    private String nameList;
    private String shortDescription;
    private String longDescription;

    @Convert(converter = WearSlot.Converter.class)
    private EnumSet<WearSlot> wearSlots = EnumSet.noneOf(WearSlot.class);

    public List<String> getNameList() {
        if (!transientNameList.isEmpty()) {
            return new ArrayList<>(transientNameList);
        }

        return nameList == null ? List.of() : Arrays.stream(nameList.split(",")).toList();
    }

    public void setNameList(List<String> names) {
        transientNameList = new ArrayList<>(names);
        nameList = String.join(",", transientNameList);
    }

    @PostLoad
    private void loadNameList() {
        transientNameList = nameList == null ? List.of() : Arrays.stream(nameList.split(",")).toList();
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
}
