package com.agonyforge.mud.demo.model.impl;

import com.agonyforge.mud.demo.model.constant.WearSlot;
import jakarta.persistence.*;

import java.util.*;

@Entity
@Table(name = "mud_pitem")
public class MudItemPrototype extends Persistent {
    @Id
    private Long id;
    private String containerType;
    private String containerId;

    @ElementCollection
    @CollectionTable(name = "mud_pitem_namelist_mapping",
    joinColumns = {@JoinColumn(name = "item_id", referencedColumnName = "id")})
    private List<String> nameList = new ArrayList<>();
    private String shortDescription;
    private String longDescription;

    @ElementCollection
    @CollectionTable(name = "mud_pitem_wearslot_mapping",
    joinColumns = {@JoinColumn(name = "item_id", referencedColumnName = "id")})
    private Set<WearSlot> wearSlots = new HashSet<>();
    private WearSlot worn;

    public MudItem buildInstance() {
        MudItem instance = new MudItem();

        instance.setId(getId());
        instance.setNameList(new ArrayList<>(getNameList()));
        instance.setShortDescription(getShortDescription());
        instance.setLongDescription(getLongDescription());
        instance.setWearSlots(getWearSlots());
        instance.setWorn(getWorn());

        return instance;
    }

    public Long getId() {
        return id;
    }

    public void setId(Long id) {
        this.id = id;
    }

    public List<String> getNameList() {
        return nameList;
    }

    public void setNameList(List<String> nameList) {
        this.nameList = nameList;
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

    public Set<WearSlot> getWearSlots() {
        return new HashSet<>(wearSlots);
    }

    public void setWearSlots(Set<WearSlot> wearSlots) {
        this.wearSlots = wearSlots;
    }

    public WearSlot getWorn() {
        return worn;
    }

    public void setWorn(WearSlot worn) {
        this.worn = worn;
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
