package com.agonyforge.mud.demo.model.impl;

import com.agonyforge.mud.demo.model.constant.WearSlot;
import jakarta.persistence.*;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.util.*;

@Entity
@Table(name = "mud_item")
public class MudItem extends Persistent {
    private static final Logger LOGGER = LoggerFactory.getLogger(MudItem.class);

    @Id
    @GeneratedValue(strategy = GenerationType.AUTO)
    @Column(name = "instance_id")
    private Long instanceId = null;
    private Long id;
    private Long roomId;
    private Long chId;

    @Transient
    private List<String> transientNameList = new ArrayList<>();
    private String nameList;
    private String shortDescription;
    private String longDescription;

    @Convert(converter = WearSlot.Converter.class)
    private EnumSet<WearSlot> wearSlots = EnumSet.noneOf(WearSlot.class);

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
