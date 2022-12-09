package com.agonyforge.mud.demo.config;

import com.agonyforge.mud.models.dynamodb.constant.Direction;
import com.agonyforge.mud.models.dynamodb.impl.MudItem;
import com.agonyforge.mud.models.dynamodb.impl.MudRoom;
import com.agonyforge.mud.models.dynamodb.impl.MudZone;
import com.agonyforge.mud.models.dynamodb.repository.MudItemRepository;
import com.agonyforge.mud.models.dynamodb.repository.MudRoomRepository;
import com.agonyforge.mud.models.dynamodb.repository.MudZoneRepository;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

import javax.annotation.PostConstruct;
import java.util.List;
import java.util.UUID;

@Component
public class WorldLoader {
    private static final Logger LOGGER = LoggerFactory.getLogger(WorldLoader.class);

    private final MudZoneRepository zoneRepository;
    private final MudRoomRepository roomRepository;
    private final MudItemRepository itemRepository;

    @Autowired
    public WorldLoader(MudZoneRepository zoneRepository,
                       MudRoomRepository roomRepository,
                       MudItemRepository itemRepository) {
        this.zoneRepository = zoneRepository;
        this.roomRepository = roomRepository;
        this.itemRepository = itemRepository;
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

        if (roomRepository.getById(100L).isEmpty()) {
            MudRoom room100 = new MudRoom();

            room100.setId(100L);
            room100.setZoneId(1L);
            room100.setName("Default Room");
            room100.setDescription("This room was automatically generated.");
            room100.setExit(Direction.WEST.getName(), new MudRoom.Exit(101L));

            MudRoom room101 = new MudRoom();

            room101.setId(101L);
            room101.setZoneId(1L);
            room101.setName("Adjacent Room");
            room101.setDescription("This room was automatically generated.");
            room101.setExit(Direction.EAST.getName(), new MudRoom.Exit(100L));

            LOGGER.info("Creating default rooms");
            roomRepository.saveAll(List.of(room100, room101));
        }

        if (itemRepository.getByRoom(100L).isEmpty()) {
            MudItem item = new MudItem();
            MudItem itemInstance;

            item.setId(UUID.randomUUID());
            item.setNameList(List.of("spoon"));
            item.setShortDescription("a spoon");
            item.setLongDescription("A spoon is floating in midair here.");

            itemInstance = item.buildInstance();

            itemInstance.setRoomId(100L);

            LOGGER.info("Creating default item");
            itemRepository.saveAll(List.of(item, itemInstance));
        }
    }
}
