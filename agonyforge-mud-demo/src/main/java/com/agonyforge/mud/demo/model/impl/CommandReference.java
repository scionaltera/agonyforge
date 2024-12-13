package com.agonyforge.mud.demo.model.impl;

import jakarta.persistence.Entity;
import jakarta.persistence.Id;
import jakarta.persistence.Table;

import java.util.Objects;

@Entity
@Table(name = "mud_command")
public class CommandReference extends Persistent {
    @Id
    private String name;
    private int priority;
    private String beanName;

    public CommandReference() {
        // this method intentionally left blank
    }

    public CommandReference(int priority, String name, String beanName) {
        this.name = name;
        this.priority = priority;
        this.beanName = beanName;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public int getPriority() {
        return priority;
    }

    public void setPriority(int priority) {
        this.priority = priority;
    }

    public String getBeanName() {
        return beanName;
    }

    public void setBeanName(String beanName) {
        this.beanName = beanName;
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (!(o instanceof CommandReference)) return false;
        CommandReference that = (CommandReference) o;
        return Objects.equals(getName(), that.getName());
    }

    @Override
    public int hashCode() {
        return Objects.hash(getName());
    }
}
