package com.agonyforge.mud.demo.model.impl;

import com.agonyforge.mud.demo.model.constant.WearSlot;
import jakarta.persistence.*;
import org.hibernate.annotations.Cascade;

import java.util.*;

@Entity
@Table(name = "mud_item")
public class MudItem extends Persistent {
    @Id
    @GeneratedValue(strategy = GenerationType.AUTO)
    @Column(name = "instance_id")
    private Long instanceId = null;
    private Long id;
    private Long roomId;
    private Long chId;

    @ElementCollection
    @CollectionTable(name = "mud_item_namelist_mapping",
    joinColumns = {@JoinColumn(name = "item_id", referencedColumnName = "instance_id")})
    @Cascade(org.hibernate.annotations.CascadeType.ALL)
    private List<String> nameList = new ArrayList<>();
    private String shortDescription;
    private String longDescription;

    @ElementCollection
    @CollectionTable(name = "mud_item_wearslot_mapping",
    joinColumns = {@JoinColumn(name = "item_id", referencedColumnName = "instance_id")})
    @Cascade(org.hibernate.annotations.CascadeType.ALL)
    private Set<WearSlot> wearSlots = new HashSet<>();
    private WearSlot worn;

    public Long getId() {
        return id;
    }

    public void setId(Long id) {
        this.id = id;
    }

    public Long getInstanceId() {
        return instanceId;
    }

    public void setInstanceId(Long instanceId) {
        this.instanceId = instanceId;
    }

    public Long getCharacterId() {
        return chId;
    }

    public void setCharacterId(Long characterId) {
        if (roomId != null) {
            roomId = null;
        }

        chId = characterId;
    }

    public Long getRoomId() {
        return roomId;
    }

    public void setRoomId(Long roomId) {
        if (chId != null) {
            chId = null;
        }

        this.roomId = roomId;
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
        if (!(o instanceof MudItem mudItem)) return false;
        return Objects.equals(getId(), mudItem.getId());
    }

    @Override
    public int hashCode() {
        return Objects.hash(getId());
    }
}
