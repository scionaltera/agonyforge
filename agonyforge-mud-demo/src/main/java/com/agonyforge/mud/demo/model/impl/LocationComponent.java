package com.agonyforge.mud.demo.model.impl;

import com.agonyforge.mud.demo.model.constant.WearSlot;
import jakarta.persistence.*;

import java.util.Objects;

@Entity
public class LocationComponent extends Persistent {
    @Id
    @GeneratedValue(strategy = GenerationType.AUTO)
    private Long id;

    @ManyToOne
    private MudCharacter held = null;

    private WearSlot worn = null;

    @ManyToOne
    private MudRoom room = null;

    public Long getId() {
        return id;
    }

    public void setId(Long id) {
        this.id = id;
    }

    public MudCharacter getHeld() {
        return held;
    }

    public void setHeld(MudCharacter heldBy) {
        this.held = heldBy;
    }

    public WearSlot getWorn() {
        return worn;
    }

    public void setWorn(WearSlot wornOn) {
        this.worn = wornOn;
    }

    public MudRoom getRoom() {
        return room;
    }

    public void setRoom(MudRoom inRoom) {
        this.room = inRoom;
    }

    @Override
    public boolean equals(Object o) {
        if (o == null || getClass() != o.getClass()) return false;
        LocationComponent that = (LocationComponent) o;
        return Objects.equals(id, that.id);
    }

    @Override
    public int hashCode() {
        return Objects.hashCode(id);
    }
}
