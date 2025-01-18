package com.agonyforge.mud.demo.model.impl;

import jakarta.persistence.*;

import java.util.HashSet;
import java.util.Objects;
import java.util.Set;

@Entity
@Table(name = "mud_role")
public class Role extends Persistent {
    @Id
    @GeneratedValue(strategy = GenerationType.AUTO)
    private Long id;
    private String name;
    private boolean implementor = false;

    @ManyToMany
    private Set<CommandReference> commands = new HashSet<>();

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

    public boolean isImplementor() {
        return implementor;
    }

    public void setImplementor(boolean implementor) {
        this.implementor = implementor;
    }

    public Set<CommandReference> getCommands() {
        return commands;
    }

    public void setCommands(Set<CommandReference> commands) {
        this.commands = commands;
    }

    @Override
    public boolean equals(Object o) {
        if (o == null || getClass() != o.getClass()) return false;
        Role role = (Role) o;
        return Objects.equals(id, role.id);
    }

    @Override
    public int hashCode() {
        return Objects.hashCode(id);
    }
}
