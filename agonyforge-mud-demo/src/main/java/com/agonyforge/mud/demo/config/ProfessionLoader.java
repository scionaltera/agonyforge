package com.agonyforge.mud.demo.config;

import com.agonyforge.mud.demo.model.constant.Effort;
import com.agonyforge.mud.demo.model.constant.Stat;
import com.agonyforge.mud.demo.model.impl.MudProfession;
import com.agonyforge.mud.demo.model.repository.MudProfessionRepository;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

import javax.annotation.PostConstruct;
import java.util.List;
import java.util.UUID;

import static com.agonyforge.mud.demo.model.impl.ModelConstants.TYPE_PROFESSION;

@Component
public class ProfessionLoader {
    public static final UUID DEFAULT_PROFESSION_ID = UUID.fromString("cdd02a3e-ddf3-436d-9c22-77d5a8ecd4af");

    private static final Logger LOGGER = LoggerFactory.getLogger(ProfessionLoader.class);

    private final MudProfessionRepository professionRepository;

    @Autowired
    public ProfessionLoader(MudProfessionRepository professionRepository) {
        this.professionRepository = professionRepository;
    }

    @PostConstruct
    public void loadProfessions() {
        if (professionRepository.getByType(TYPE_PROFESSION).isEmpty()) {
            MudProfession fighter = new MudProfession();

            fighter.setId(DEFAULT_PROFESSION_ID);
            fighter.setName("Fighter");
            fighter.setStat(Stat.STR, 1);
            fighter.setEffort(Effort.WEAPONS_N_TOOLS, 1);

            MudProfession rogue = new MudProfession();

            rogue.setId(UUID.randomUUID());
            rogue.setName("Rogue");
            rogue.setStat(Stat.DEX, 1);
            rogue.setEffort(Effort.WEAPONS_N_TOOLS, 1);

            MudProfession wizard = new MudProfession();

            wizard.setId(UUID.randomUUID());
            wizard.setName("Wizard");
            wizard.setStat(Stat.INT, 1);
            wizard.setEffort(Effort.ENERGY_N_MAGIC, 1);

            MudProfession cleric = new MudProfession();

            cleric.setId(UUID.randomUUID());
            cleric.setName("Cleric");
            cleric.setStat(Stat.WIS, 1);
            cleric.setEffort(Effort.ENERGY_N_MAGIC, 1);

            MudProfession bard = new MudProfession();

            bard.setId(UUID.randomUUID());
            bard.setName("Bard");
            bard.setStat(Stat.CHA, 1);
            bard.setStat(Stat.INT, 1);

            LOGGER.info("Creating default professions");
            professionRepository.saveAll(List.of(fighter, rogue, wizard, cleric, bard));
        }
    }
}
