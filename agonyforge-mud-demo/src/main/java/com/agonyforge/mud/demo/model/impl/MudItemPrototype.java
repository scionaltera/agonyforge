package com.agonyforge.mud.demo.model.impl;

import jakarta.persistence.*;

import java.util.*;

@Entity
@Table(name = "mud_pitem")
public class MudItemPrototype extends AbstractMudObject {
    @Id
    private Long id;

    public MudItem buildInstance() {
        MudItem instance = new MudItem();
        instance.setItem(new ItemComponent());
        instance.setLocation(new LocationComponent());

        instance.setId(getId());
        instance.getItem().setNameList(getItem().getNameList());
        instance.getItem().setShortDescription(getItem().getShortDescription());
        instance.getItem().setLongDescription(getItem().getLongDescription());
        instance.getItem().setWearSlots(getItem().getWearSlots());

        return instance;
    }

    public Long getId() {
        return id;
    }

    public void setId(Long id) {
        this.id = id;
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
