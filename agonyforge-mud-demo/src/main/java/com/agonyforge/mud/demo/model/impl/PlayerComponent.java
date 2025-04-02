package com.agonyforge.mud.demo.model.impl;

import com.agonyforge.mud.demo.model.constant.AdminFlag;
import jakarta.persistence.*;

import java.util.EnumSet;
import java.util.HashSet;
import java.util.Objects;
import java.util.Set;

@Entity
public class PlayerComponent extends Persistent {
    @Id
    @GeneratedValue(strategy = GenerationType.AUTO)
    private Long id;
    private String username;
    private String webSocketSession;
    private String title;

    @Convert(converter = AdminFlag.Converter.class)
    private EnumSet<AdminFlag> adminFlags = EnumSet.noneOf(AdminFlag.class);

    @ManyToMany()
    private Set<Role> roles = new HashSet<>();

    public Long getId() {
        return id;
    }

    public void setId(Long id) {
        this.id = id;
    }

    public String getUsername() {
        return username;
    }

    public void setUsername(String username) {
        this.username = username;
    }

    public String getWebSocketSession() {
        return webSocketSession;
    }

    public void setWebSocketSession(String webSocketSession) {
        this.webSocketSession = webSocketSession;
    }

    public String getTitle() {
        return title;
    }

    public void setTitle(String title) {
        this.title = title;
    }

    public EnumSet<AdminFlag> getAdminFlags() {
        return adminFlags;
    }

    public void setAdminFlags(EnumSet<AdminFlag> flags) {
        this.adminFlags = flags;
    }

    public Set<Role> getRoles() {
        return new HashSet<>(roles);
    }

    public void setRoles(Set<Role> roles) {
        this.roles = roles;
    }

    @Override
    public boolean equals(Object o) {
        if (o == null || getClass() != o.getClass()) return false;
        PlayerComponent that = (PlayerComponent) o;
        return Objects.equals(id, that.id);
    }

    @Override
    public int hashCode() {
        return Objects.hashCode(id);
    }
}
