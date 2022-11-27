package com.agonyforge.mud.demo.config;

import com.agonyforge.mud.models.dynamodb.impl.MudZone;
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

    @Autowired
    public WorldLoader(MudZoneRepository zoneRepository) {
        this.zoneRepository = zoneRepository;
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
    }
}
