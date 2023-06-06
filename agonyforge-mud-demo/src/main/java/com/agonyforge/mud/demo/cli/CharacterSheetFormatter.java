package com.agonyforge.mud.demo.cli;

import com.agonyforge.mud.core.web.model.Output;
import com.agonyforge.mud.models.dynamodb.constant.Effort;
import com.agonyforge.mud.models.dynamodb.constant.Stat;
import com.agonyforge.mud.models.dynamodb.impl.MudCharacter;

import java.util.Arrays;

public final class CharacterSheetFormatter {
    private CharacterSheetFormatter() {
        // this method intentionally left blank
    }

    public static void format(MudCharacter ch, Output output) {
        output.append("[dcyan]CHARACTER SHEET");
        output.append("[default]Name: [cyan]%s", ch.getName());
        output.append("[default]Pronouns: [cyan]%s/%s", ch.getPronoun().getSubject(), ch.getPronoun().getObject());
        output.append("");
        output.append("[cyan]Stats   [magenta]Efforts");
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
