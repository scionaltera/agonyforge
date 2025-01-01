package com.agonyforge.mud.demo.cli.question.login;

import com.agonyforge.mud.core.web.model.Output;
import com.agonyforge.mud.demo.model.constant.Effort;
import com.agonyforge.mud.demo.model.constant.Stat;
import com.agonyforge.mud.demo.model.impl.*;
import org.springframework.stereotype.Component;

@Component
public final class CharacterSheetFormatter {
    public void format(AbstractMudCharacter ch, Output output) {
        output.append("[dcyan]CHARACTER SHEET");
        output.append("[default]Name: [cyan]%s", ch.getCharacter().getName());
        output.append("[default]Pronouns: [cyan]%s/%s", ch.getCharacter().getPronoun().getSubject(), ch.getCharacter().getPronoun().getObject());
        output.append("[default]Species: [cyan]%s", ch.getCharacter().getSpecies().getName());
        output.append("[default]Profession: [cyan]%s", ch.getCharacter().getProfession().getName());
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
