package com.agonyforge.mud.demo.model.impl;

import com.agonyforge.mud.demo.model.constant.WearSlot;
import jakarta.persistence.*;

import java.util.*;

@Entity
@Table(name = "mud_item")
public class MudItem extends AbstractMudItem {
    @Id
    @GeneratedValue(strategy = GenerationType.AUTO)
    @Column(name = "instance_id")
    private Long instanceId;
    private Long id;
    private Long roomId;
    private Long chId;

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
        return Objects.equals(getInstanceId(), mudItem.getInstanceId());
    }

    @Override
    public int hashCode() {
        return Objects.hash(getInstanceId());
    }
}
