package com.agonyforge.mud.demo.model.impl;

import jakarta.persistence.Entity;
import jakarta.persistence.Id;
import jakarta.persistence.Table;

import java.util.Objects;

@Entity
@Table(name = "mud_user_session")
public class UserSession extends Persistent {
    @Id
    private String principalName;
    private String remoteIpAddress;

    public String getPrincipalName() {
        return principalName;
    }

    public void setPrincipalName(String principalName) {
        this.principalName = principalName;
    }

    public String getRemoteIpAddress() {
        return remoteIpAddress;
    }

    public void setRemoteIpAddress(String remoteIpAddress) {
        this.remoteIpAddress = remoteIpAddress;
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (!(o instanceof UserSession)) return false;
        UserSession that = (UserSession) o;
        return Objects.equals(getPrincipalName(), that.getPrincipalName());
    }

    @Override
    public int hashCode() {
        return Objects.hash(getPrincipalName());
    }
}
