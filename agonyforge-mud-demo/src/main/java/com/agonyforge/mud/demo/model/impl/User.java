package com.agonyforge.mud.demo.model.impl;

import jakarta.persistence.Entity;
import jakarta.persistence.Id;
import jakarta.persistence.Table;

import java.util.Objects;

@Entity
@Table(name = "mud_user")
public class User extends Persistent {
    @Id
    private String principalName; // Principal name
    private String givenName; // real life name
    private String emailAddress;

    public String getPrincipalName() {
        return principalName;
    }

    public void setPrincipalName(String principalName) {
        this.principalName = principalName;
    }

    public String getGivenName() {
        return givenName;
    }

    public void setGivenName(String givenName) {
        this.givenName = givenName;
    }

    public String getEmailAddress() {
        return emailAddress;
    }

    public void setEmailAddress(String emailAddress) {
        this.emailAddress = emailAddress;
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (!(o instanceof User)) return false;
        User user = (User) o;
        return Objects.equals(getPrincipalName(), user.getPrincipalName());
    }

    @Override
    public int hashCode() {
        return Objects.hash(getPrincipalName());
    }
}
