package com.agonyforge.mud.demo.model.impl;

import jakarta.persistence.*;

import java.util.Objects;

@Entity
@Table(name = "mud_fight")
public class Fight extends Persistent {
    @Id
    @GeneratedValue(strategy = GenerationType.AUTO)
    private Long id;

    @ManyToOne(fetch = FetchType.EAGER)
    private MudCharacter attacker;

    @ManyToOne(fetch = FetchType.EAGER)
    private MudCharacter defender;

    public Long getId() {
        return id;
    }

    public void setId(Long id) {
        this.id = id;
    }

    public MudCharacter getAttacker() {
        return attacker;
    }

    public void setAttacker(MudCharacter attacker) {
        this.attacker = attacker;
    }

    public MudCharacter getDefender() {
        return defender;
    }

    public void setDefender(MudCharacter defender) {
        this.defender = defender;
    }

    @Override
    public boolean equals(Object o) {
        if (o == null || getClass() != o.getClass()) return false;
        Fight fight = (Fight) o;
        return Objects.equals(id, fight.id);
    }

    @Override
    public int hashCode() {
        return Objects.hashCode(id);
    }
}
