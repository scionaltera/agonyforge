package com.agonyforge.mud.demo.model.impl;

import com.agonyforge.mud.demo.model.constant.WearSlot;
import jakarta.persistence.*;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.util.*;

@Entity
@Table(name = "mud_pitem")
public class MudItemPrototype extends Persistent {
    private static final Logger LOGGER = LoggerFactory.getLogger(MudItemPrototype.class);

    @Id
    private Long id;

    @Transient
    private List<String> transientNameList = new ArrayList<>();
    private String nameList;
    private String shortDescription;
    private String longDescription;

    @Convert(converter = WearSlot.Converter.class)
    private EnumSet<WearSlot> wearSlots = EnumSet.noneOf(WearSlot.class);

    public MudItem buildInstance() {
        MudItem instance = new MudItem();

        instance.setId(getId());
        instance.setNameList(getNameList());
        instance.setShortDescription(getShortDescription());
        instance.setLongDescription(getLongDescription());
        instance.setWearSlots(getWearSlots());

        return instance;
    }

    public Long getId() {
        return id;
    }

    public void setId(Long id) {
        this.id = id;
    }

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

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (!(o instanceof MudItemPrototype mudItem)) return false;
        return Objects.equals(getId(), mudItem.getId());
    }

    @Override
    public int hashCode() {
        return Objects.hash(getId());
    }
}
