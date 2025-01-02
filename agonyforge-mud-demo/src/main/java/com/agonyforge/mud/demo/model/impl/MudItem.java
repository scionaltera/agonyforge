package com.agonyforge.mud.demo.model.impl;

import jakarta.persistence.*;

import java.util.Objects;

@Entity
public class MudItem extends AbstractMudObject {
    @Id
    @GeneratedValue(strategy = GenerationType.AUTO)
    private Long id;

    @ManyToOne
    private MudItemPrototype template;

    public Long getId() {
        return id;
    }

    public void setId(Long id) {
        this.id = id;
    }

    public MudItemPrototype getTemplate() {
        return template;
    }

    public void setTemplate(MudItemPrototype template) {
        this.template = template;
    }

    @Override
    public boolean equals(Object o) {
        if (o == null || getClass() != o.getClass()) return false;
        MudItem mudItem = (MudItem) o;
        return Objects.equals(id, mudItem.id);
    }

    @Override
    public int hashCode() {
        return Objects.hashCode(id);
    }
}
