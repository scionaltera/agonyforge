package com.agonyforge.mud.demo.cli;

import com.agonyforge.mud.core.web.model.Output;
import com.agonyforge.mud.models.dynamodb.constant.Stat;
import com.agonyforge.mud.models.dynamodb.impl.MudCharacter;

import java.util.Arrays;

public final class CharacterSheetFormatter {
    private CharacterSheetFormatter() {
        // this method intentionally left blank
    }

    public static void format(MudCharacter ch, Output output) {
        output.append("[dcyan]Character Sheet");
        output.append("[default]Name: [cyan]%s", ch.getName());
        output.append("[default]Pronouns: [cyan]%s/%s", ch.getPronoun().getSubject(), ch.getPronoun().getObject());

        Arrays.stream(Stat.values())
            .forEachOrdered(stat -> output.append("[default]%s: [cyan]%d", stat.getAbbreviation(), ch.getStat(stat)));

        output.append("[default]DEF: [cyan]%d", ch.getDefense());
    }
}
