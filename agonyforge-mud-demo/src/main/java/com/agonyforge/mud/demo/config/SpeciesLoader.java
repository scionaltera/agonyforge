package com.agonyforge.mud.demo.config;

import com.agonyforge.mud.demo.model.constant.Stat;
import com.agonyforge.mud.demo.model.impl.MudSpecies;
import com.agonyforge.mud.demo.model.repository.MudSpeciesRepository;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

import javax.annotation.PostConstruct;
import java.util.List;
import java.util.UUID;

@Component
public class SpeciesLoader {
    private static final Logger LOGGER = LoggerFactory.getLogger(SpeciesLoader.class);

    private final MudSpeciesRepository speciesRepository;

    @Autowired
    public SpeciesLoader(MudSpeciesRepository speciesRepository) {
        this.speciesRepository = speciesRepository;
    }

    @PostConstruct
    public void loadSpecies() {
        MudSpecies human = new MudSpecies();

        human.setId(UUID.randomUUID());
        human.setName("Human");
        human.setStat(Stat.INT, 1);
        human.setStat(Stat.CHA, 1);

        MudSpecies elf = new MudSpecies();

        elf.setId(UUID.randomUUID());
        elf.setName("Elf");
        elf.setStat(Stat.DEX, 1);
        elf.setStat(Stat.CHA, 1);

        MudSpecies dwarf = new MudSpecies();

        dwarf.setId(UUID.randomUUID());
        dwarf.setName("Dwarf");
        dwarf.setStat(Stat.STR, 1);
        dwarf.setStat(Stat.CON, 1);

        LOGGER.info("Creating default species");
        speciesRepository.saveAll(List.of(human, elf, dwarf));
    }
}
