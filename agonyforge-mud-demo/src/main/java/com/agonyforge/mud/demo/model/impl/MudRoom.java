package com.agonyforge.mud.demo.model.impl;

import com.agonyforge.mud.demo.model.constant.RoomFlag;
import jakarta.persistence.*;

import java.util.*;
import java.util.stream.Collectors;

@Entity
@Table(name="mud_room")
public class MudRoom extends Persistent {
    @Id
    private Long id;
    private Long zoneId;
    private String name;

    @Lob
    private String description;

    @Convert(converter = RoomFlag.Converter.class)
    private EnumSet<RoomFlag> flags = EnumSet.noneOf(RoomFlag.class);

    @OneToMany(cascade = CascadeType.ALL)
    @JoinTable(name = "mud_room_exit_mapping",
        joinColumns = {@JoinColumn(name = "room_id", referencedColumnName = "id")},
        inverseJoinColumns = {@JoinColumn(name = "exit_id", referencedColumnName = "id")})
    @MapKeyColumn(name = "direction")
    private final Map<String, Exit> exits = new HashMap<>();

    @Entity
    @Table(name="mud_exit")
    public static class Exit extends Persistent {
        @Id
        @GeneratedValue(strategy = GenerationType.AUTO)
        private Long id;
        private Long destinationId;

        public Exit(Long destinationId) {
            this.destinationId = destinationId;
        }

        public Exit() {}

        public Long getDestinationId() {
            return destinationId;
        }

        public void setDestinationId(Long destinationId) {
            this.destinationId = destinationId;
        }
    }

    public Long getId() {
        return id;
    }

    public void setId(Long id) {
        this.id = id;
    }

    public Long getZoneId() {
        return zoneId;
    }

    public void setZoneId(Long zoneId) {
        this.zoneId = zoneId;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public String getDescription() {
        return description;
    }

    public void setDescription(String description) {
        this.description = description;
    }

    public EnumSet<RoomFlag> getFlags() {
        return flags;
    }

    public void setFlags(EnumSet<RoomFlag> flags) {
        this.flags = flags;
    }

    public List<String> getExits() {
        return exits.keySet()
            .stream()
            .sorted()
            .collect(Collectors.toList());
    }

    public Exit getExit(String direction) {
        return exits.get(direction);
    }

    public void setExit(String direction, Exit exit) {
        exits.put(direction, exit);
    }

    public void removeExit(String direction) {
        exits.remove(direction);
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (!(o instanceof MudRoom)) return false;
        MudRoom mudRoom = (MudRoom) o;
        return Objects.equals(getId(), mudRoom.getId());
    }

    @Override
    public int hashCode() {
        return Objects.hash(getId());
    }
}
