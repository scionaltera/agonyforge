package com.agonyforge.mud.demo.model.impl;

import jakarta.persistence.CascadeType;
import jakarta.persistence.MappedSuperclass;
import jakarta.persistence.OneToOne;

@MappedSuperclass
public abstract class AbstractMudObject extends Persistent {
    @OneToOne(cascade = CascadeType.ALL)
    private PlayerComponent player = null;

    @OneToOne(cascade = CascadeType.ALL)
    private NonPlayerComponent nonPlayer = null;

    @OneToOne(cascade = CascadeType.ALL)
    private CharacterComponent character = null;

    @OneToOne(cascade = CascadeType.ALL)
    private ItemComponent item = null;

    @OneToOne(cascade = CascadeType.ALL, orphanRemoval = true)
    private LocationComponent location = null;

    public PlayerComponent getPlayer() {
        return player;
    }

    public void setPlayer(PlayerComponent player) {
        this.player = player;
    }

    public NonPlayerComponent getNonPlayer() {
        return nonPlayer;
    }

    public void setNonPlayer(NonPlayerComponent nonPlayer) {
        this.nonPlayer = nonPlayer;
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

    public String getName() {
        return character != null ? character.getName() : null;
    }

    public void setName(String name) {
        if (character != null) {
            character.setName(name);
        }
    }
}
