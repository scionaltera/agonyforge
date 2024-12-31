package com.agonyforge.mud.demo.model.impl;

import jakarta.persistence.CascadeType;
import jakarta.persistence.MappedSuperclass;
import jakarta.persistence.OneToOne;

@MappedSuperclass
public class AbstractMudObject extends Persistent {
    @OneToOne(cascade = CascadeType.ALL)
    private PlayerComponent player = null;

    public PlayerComponent getPlayer() {
        return player;
    }

    public void setPlayer(PlayerComponent player) {
        this.player = player;
    }
}
