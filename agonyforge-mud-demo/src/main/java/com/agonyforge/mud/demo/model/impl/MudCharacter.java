package com.agonyforge.mud.demo.model.impl;

import jakarta.persistence.*;

import java.util.*;

@Entity
public class MudCharacter extends AbstractMudCharacter {
    @Id
    @GeneratedValue(strategy = GenerationType.AUTO)
    private Long id;
    private Long prototypeId;
    private String webSocketSession;
    private Long roomId;

    public Long getId() {
        return id;
    }

    public void setId(Long id) {
        this.id = id;
    }

    public Long getPrototypeId() {
        return prototypeId;
    }

    public void setPrototypeId(Long prototypeId) {
        this.prototypeId = prototypeId;
    }

    public String getWebSocketSession() {
        return webSocketSession;
    }

    public void setWebSocketSession(String webSocketSession) {
        this.webSocketSession = webSocketSession;
    }

    public Long getZoneId() {
        String roomIdString = getRoomId().toString();

        return Long.valueOf(roomIdString.substring(0, roomIdString.length() - 2));
    }

    public Long getRoomId() {
        return roomId;
    }

    public void setRoomId(Long roomId) {
        this.roomId = roomId;
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (!(o instanceof MudCharacter)) return false;
        MudCharacter that = (MudCharacter) o;
        return Objects.equals(getId(), that.getId());
    }

    @Override
    public int hashCode() {
        return Objects.hash(getId());
    }
}
