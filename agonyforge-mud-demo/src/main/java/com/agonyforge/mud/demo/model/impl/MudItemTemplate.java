package com.agonyforge.mud.demo.model.impl;

import jakarta.persistence.*;

import java.util.*;

@Entity
public class MudItemTemplate extends AbstractMudObject {
    @Id
    private Long id;

    public MudItem buildInstance() {
        MudItem instance = new MudItem();
        instance.setItem(new ItemComponent());
        instance.setLocation(new LocationComponent());

        instance.setTemplate(this);
        instance.getItem().setNameList(getItem().getNameList());
        instance.getItem().setShortDescription(getItem().getShortDescription());
        instance.getItem().setLongDescription(getItem().getLongDescription());
        instance.getItem().setWearSlots(getItem().getWearSlots());
        instance.getItem().setWearMode(getItem().getWearMode());

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
        if (!(o instanceof MudItemTemplate mudItem)) return false;
        return Objects.equals(getId(), mudItem.getId());
    }

    @Override
    public int hashCode() {
        return Objects.hash(getId());
    }
}
