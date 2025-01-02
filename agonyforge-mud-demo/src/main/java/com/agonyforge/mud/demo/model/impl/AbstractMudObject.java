package com.agonyforge.mud.demo.model.impl;

import jakarta.persistence.CascadeType;
import jakarta.persistence.MappedSuperclass;
import jakarta.persistence.OneToOne;

@MappedSuperclass
public abstract class AbstractMudObject extends Persistent {
    @OneToOne(cascade = CascadeType.ALL)
    private PlayerComponent player = null;

    @OneToOne(cascade = CascadeType.ALL)
    private CharacterComponent character = null;

    @OneToOne(cascade = CascadeType.ALL)
    private ItemComponent item = null;

    @OneToOne(cascade = CascadeType.ALL)
    private LocationComponent location = null;

    public PlayerComponent getPlayer() {
        return player;
    }

    public void setPlayer(PlayerComponent player) {
        this.player = player;
    }

    public CharacterComponent getCharacter() {
        return character;
    }

    public void setCharacter(CharacterComponent character) {
        this.character = character;
    }

    public ItemComponent getItem() {
        return item;
    }

    public void setItem(ItemComponent item) {
        this.item = item;
    }

    public LocationComponent getLocation() {
        return location;
    }

    public void setLocation(LocationComponent location) {
        this.location = location;
    }
}
