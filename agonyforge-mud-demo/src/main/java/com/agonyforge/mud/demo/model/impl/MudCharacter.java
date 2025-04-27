package com.agonyforge.mud.demo.model.impl;

import java.util.Objects;

import jakarta.persistence.Entity;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.GenerationType;
import jakarta.persistence.Id;
import jakarta.persistence.ManyToOne;

@Entity
public class MudCharacter extends AbstractMudObject {
    @Id
    @GeneratedValue(strategy = GenerationType.AUTO)
    private Long id;

    @ManyToOne
    private MudCharacterTemplate template;

    public Long getId() {
        return id;
    }

    public void setId(Long id) {
        this.id = id;
    }

    public Long getZoneId() {
        String roomIdString = getLocation().getRoom().getId().toString();

        return Long.valueOf(roomIdString.substring(0, roomIdString.length() - 2));
    }

    public MudCharacterTemplate getTemplate() {
        return template;
    }

    public void setTemplate(MudCharacterTemplate template) {
        this.template = template;
    }

    @Override
    public boolean equals(Object o) {
        if (this == o)
            return true;
        if (!(o instanceof MudCharacter that))
            return false;
        return Objects.equals(getId(), that.getId());
    }

    @Override
    public int hashCode() {
        return Objects.hash(getId());
    }

    @Override
    public String getName() {
        return super.getName();
    }

    public boolean isImplementor() {
        if (getPlayer() == null || getPlayer().getRoles() == null) {
            return false;
        }

        return getPlayer().getRoles().stream()
                .anyMatch(Role::isImplementor);
    }
}
