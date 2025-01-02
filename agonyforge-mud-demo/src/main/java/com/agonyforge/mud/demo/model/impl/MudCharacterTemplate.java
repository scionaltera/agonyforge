package com.agonyforge.mud.demo.model.impl;

import com.agonyforge.mud.demo.model.constant.Effort;
import com.agonyforge.mud.demo.model.constant.Stat;
import jakarta.persistence.*;

import java.util.*;

@Entity
public class MudCharacterTemplate extends AbstractMudObject {
    @Id
    @GeneratedValue(strategy = GenerationType.AUTO)
    private Long id;
    private Boolean isComplete = false;

    public MudCharacter buildInstance() {
        if (getComplete() == null || !getComplete()) {
            throw new IllegalStateException("Cannot build an incomplete instance of a character.");
        }

        MudCharacter instance = new MudCharacter();
        instance.setPlayer(new PlayerComponent());
        instance.setCharacter(new CharacterComponent());
        instance.setLocation(new LocationComponent());

        instance.setTemplate(this);
        instance.getCharacter().setName(getCharacter().getName());
        instance.getCharacter().setPronoun(getCharacter().getPronoun());
        instance.getPlayer().setUsername(getPlayer().getUsername());
        instance.getPlayer().setRoles(getPlayer().getRoles());
        instance.getCharacter().setWearSlots(getCharacter().getWearSlots());
        instance.getCharacter().setSpecies(getCharacter().getSpecies());
        instance.getCharacter().setProfession(getCharacter().getProfession());

        Arrays.stream(Stat.values())
            .forEach(stat -> {
                instance.getCharacter().setBaseStat(stat, getCharacter().getBaseStat(stat));
            });
        Arrays.stream(Effort.values())
            .forEach(effort -> {
                instance.getCharacter().setBaseEffort(effort, getCharacter().getBaseEffort(effort));
            });

        return instance;
    }

    public Long getId() {
        return id;
    }

    public void setId(Long id) {
        this.id = id;
    }

    public Boolean getComplete() {
        return isComplete;
    }

    public void setComplete(Boolean complete) {
        isComplete = complete;
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (!(o instanceof MudCharacterTemplate)) return false;
        MudCharacterTemplate that = (MudCharacterTemplate) o;
        return Objects.equals(getId(), that.getId());
    }

    @Override
    public int hashCode() {
        return Objects.hash(getId());
    }
}
