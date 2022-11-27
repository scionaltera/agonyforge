package com.agonyforge.mud.demo.config;

import com.agonyforge.mud.models.dynamodb.impl.MudRoom;
import com.agonyforge.mud.models.dynamodb.impl.MudZone;
import com.agonyforge.mud.models.dynamodb.repository.MudRoomRepository;
import com.agonyforge.mud.models.dynamodb.repository.MudZoneRepository;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

import javax.annotation.PostConstruct;

@Component
public class WorldLoader {
    private static final Logger LOGGER = LoggerFactory.getLogger(WorldLoader.class);

    private final MudZoneRepository zoneRepository;
    private final MudRoomRepository roomRepository;

    @Autowired
    public WorldLoader(MudZoneRepository zoneRepository,
                       MudRoomRepository roomRepository) {
        this.zoneRepository = zoneRepository;
        this.roomRepository = roomRepository;
    }

    @PostConstruct
    public void loadWorld() {
        if (zoneRepository.getById(1L).isEmpty()) {
            MudZone zone = new MudZone();

            zone.setId(1L);
            zone.setName("Default Zone");

            LOGGER.info("Creating default zone");
            zoneRepository.save(zone);
        }

        if (roomRepository.getById(1L).isEmpty()) {
            MudRoom room = new MudRoom();

            room.setId(1L);
            room.setZoneId(1L);
            room.setName("Default Room");
            room.setDescription("This room was automatically generated.");

            LOGGER.info("Creating default room");
            roomRepository.save(room);
        }
    }
}
