package com.agonyforge.mud.demo.config;

import com.agonyforge.mud.demo.model.constant.Direction;
import com.agonyforge.mud.demo.model.constant.WearSlot;
import com.agonyforge.mud.demo.model.impl.*;
import com.agonyforge.mud.demo.model.repository.*;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

import javax.annotation.PostConstruct;
import java.util.EnumSet;
import java.util.List;
import java.util.Set;

import static com.agonyforge.mud.demo.event.WeatherListener.PROPERTY_HOUR;

@Component
public class WorldLoader {
    private static final Logger LOGGER = LoggerFactory.getLogger(WorldLoader.class);

    private final MudPropertyRepository propertyRepository;
    private final MudZoneRepository zoneRepository;
    private final MudRoomRepository roomRepository;
    private final MudItemPrototypeRepository itemPrototypeRepository;
    private final MudItemRepository itemRepository;

    @Autowired
    public WorldLoader(MudPropertyRepository propertyRepository,
                       MudZoneRepository zoneRepository,
                       MudRoomRepository roomRepository,
                       MudItemPrototypeRepository itemPrototypeRepository,
                       MudItemRepository itemRepository) {
        this.propertyRepository = propertyRepository;
        this.zoneRepository = zoneRepository;
        this.roomRepository = roomRepository;
        this.itemPrototypeRepository = itemPrototypeRepository;
        this.itemRepository = itemRepository;
    }

    @PostConstruct
    public void loadWorld() {
        if (propertyRepository.findById(PROPERTY_HOUR).isEmpty()) {
            MudProperty mudHour = new MudProperty(PROPERTY_HOUR, "0");

            LOGGER.info("Setting world time");
            propertyRepository.save(mudHour);
        }

        if (zoneRepository.findById(1L).isEmpty()) {
            MudZone zone = new MudZone();

            zone.setId(1L);
            zone.setName("Default Zone");

            LOGGER.info("Creating default zone");
            zoneRepository.save(zone);
        }

        if (roomRepository.findById(100L).isEmpty()) {
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

        if (itemRepository.getByRoomId(100L).isEmpty()) {
            MudItemPrototype item = new MudItemPrototype();
            MudItem itemInstance;

            item.setId(100L);
            item.setNameList(List.of("spoon"));
            item.setShortDescription("a spoon");
            item.setLongDescription("A spoon is floating in midair here.");

            item = itemPrototypeRepository.save(item);

            itemInstance = item.buildInstance();

            itemInstance.setRoomId(100L);

            MudItemPrototype hat = new MudItemPrototype();
            MudItem hatInstance;

            hat.setId(101L);
            hat.setNameList(List.of("hat", "floppy"));
            hat.setShortDescription("a floppy hat");
            hat.setLongDescription("A floppy hat has been dropped here.");
            hat.setWearSlots(EnumSet.of(WearSlot.HEAD));

            hat = itemPrototypeRepository.save(hat);

            hatInstance = hat.buildInstance();
            hatInstance.setRoomId(101L);

            LOGGER.info("Creating default items");
            itemRepository.saveAll(List.of(itemInstance, hatInstance));
        }
    }
}
