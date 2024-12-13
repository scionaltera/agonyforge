package com.agonyforge.mud.demo.model.impl;

import jakarta.persistence.MappedSuperclass;
import jakarta.persistence.Temporal;
import jakarta.persistence.TemporalType;
import org.springframework.data.annotation.CreatedDate;
import org.springframework.data.annotation.LastModifiedDate;

import java.util.Date;

@MappedSuperclass
public abstract class Persistent {
    @CreatedDate
    @Temporal(TemporalType.TIMESTAMP)
    private Date created = new Date();

    @LastModifiedDate
    @Temporal(TemporalType.TIMESTAMP)
    private Date lastModified = new Date();

    public Date getCreated() {
        return created;
    }

    public void setCreated(Date created) {
        this.created = created;
    }

    public Date getLastModified() {
        return lastModified;
    }

    public void setLastModified(Date lastModified) {
        this.lastModified = lastModified;
    }
}
