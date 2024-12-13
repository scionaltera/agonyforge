package com.agonyforge.mud.demo.model.impl;

import jakarta.persistence.*;

import java.util.Objects;

@Entity
@Table(name = "mud_zone")
public class MudZone extends Persistent {
    @Id
    private Long id;
    private String name;

    public Long getId() {
        return id;
    }

    public void setId(Long id) {
        this.id = id;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (!(o instanceof MudZone)) return false;
        MudZone mudZone = (MudZone) o;
        return Objects.equals(getId(), mudZone.getId());
    }

    @Override
    public int hashCode() {
        return Objects.hash(getId());
    }
}
