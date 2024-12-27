package com.agonyforge.mud.demo.cli.question.login;

import com.agonyforge.mud.core.web.model.Output;
import com.agonyforge.mud.demo.model.constant.Effort;
import com.agonyforge.mud.demo.model.constant.Stat;
import com.agonyforge.mud.demo.model.impl.MudCharacter;
import com.agonyforge.mud.demo.model.impl.MudCharacterPrototype;
import com.agonyforge.mud.demo.model.impl.MudProfession;
import com.agonyforge.mud.demo.model.impl.MudSpecies;
import com.agonyforge.mud.demo.model.repository.MudProfessionRepository;
import com.agonyforge.mud.demo.model.repository.MudSpeciesRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

import static com.agonyforge.mud.demo.config.ProfessionLoader.DEFAULT_PROFESSION_ID;
import static com.agonyforge.mud.demo.config.SpeciesLoader.DEFAULT_SPECIES_ID;

@Component
public final class CharacterSheetFormatter {
    private final MudSpeciesRepository speciesRepository;
    private final MudProfessionRepository professionRepository;

    @Autowired
    public CharacterSheetFormatter(MudSpeciesRepository speciesRepository, MudProfessionRepository professionRepository) {
        this.speciesRepository = speciesRepository;
        this.professionRepository = professionRepository;
    }

    public void format(MudCharacterPrototype ch, Output output) {
        // TODO this is only used to get the species name, should denormalize that too maybe?
        MudSpecies species = speciesRepository.findById(ch.getSpeciesId() != null ? ch.getSpeciesId() : DEFAULT_SPECIES_ID).orElseThrow();
        MudProfession profession = professionRepository.findById(ch.getProfessionId() != null ? ch.getProfessionId() : DEFAULT_PROFESSION_ID).orElseThrow();

        output.append("[dcyan]CHARACTER SHEET");
        output.append("[default]Name: [%s]%s", ch.getId() == 1L ? "yellow" : "cyan", ch.getName());
        output.append("[default]Pronouns: [cyan]%s/%s", ch.getPronoun().getSubject(), ch.getPronoun().getObject());
        output.append("[default]Species: [cyan]%s", species.getName());
        output.append("[default]Profession: [cyan]%s", profession.getName());
        output.append("");
        output.append("[cyan]Stats  [magenta]Efforts");
        for (int i = 0; i < Math.max(Stat.values().length, Effort.values().length); i++) {
            Stat stat = Stat.values().length > i ? Stat.values()[i] : null;
            Effort effort = Effort.values().length > i ? Effort.values()[i] : null;

            String statString = stat != null ? String.format("[default]%s: [cyan]%d", stat.getAbbreviation(), ch.getStat(stat)) : "";
            String effortString = effort != null ? String.format("[default](d%-2d) %-15s: [magenta]%d", effort.getDie(), effort.getName(), ch.getEffort(effort)) : "";

            output.append("%15s\t%15s", statString, effortString);
        }

        output.append("");
        output.append("[default]Health: [red]❤");
        output.append("[default]DEF: [green]%d", ch.getDefense());
    }

    public void format(MudCharacter ch, Output output) {
        // TODO this is only used to get the species name, should denormalize that too maybe?
        MudSpecies species = speciesRepository.findById(ch.getSpeciesId() != null ? ch.getSpeciesId() : DEFAULT_SPECIES_ID).orElseThrow();
        MudProfession profession = professionRepository.findById(ch.getProfessionId() != null ? ch.getProfessionId() : DEFAULT_PROFESSION_ID).orElseThrow();

        output.append("[dcyan]CHARACTER SHEET");
        output.append("[default]Name: [%s]%s", ch.getPrototypeId() == 1L ? "yellow" : "cyan", ch.getName());
        output.append("[default]Pronouns: [cyan]%s/%s", ch.getPronoun().getSubject(), ch.getPronoun().getObject());
        output.append("[default]Species: [cyan]%s", species.getName());
        output.append("[default]Profession: [cyan]%s", profession.getName());
        output.append("");
        output.append("[cyan]Stats  [magenta]Efforts");
        for (int i = 0; i < Math.max(Stat.values().length, Effort.values().length); i++) {
            Stat stat = Stat.values().length > i ? Stat.values()[i] : null;
            Effort effort = Effort.values().length > i ? Effort.values()[i] : null;

            String statString = stat != null ? String.format("[default]%s: [cyan]%d", stat.getAbbreviation(), ch.getStat(stat)) : "";
            String effortString = effort != null ? String.format("[default](d%-2d) %-15s: [magenta]%d", effort.getDie(), effort.getName(), ch.getEffort(effort)) : "";

            output.append("%15s\t%15s", statString, effortString);
        }

        output.append("");
        output.append("[default]Health: [red]❤");
        output.append("[default]DEF: [green]%d", ch.getDefense());
    }
}
