package com.agonyforge.mud.demo.cli.command;

import com.agonyforge.mud.core.cli.Question;
import com.agonyforge.mud.core.service.dice.DiceResult;
import com.agonyforge.mud.core.service.dice.DiceService;
import com.agonyforge.mud.core.web.model.Input;
import com.agonyforge.mud.core.web.model.Output;
import com.agonyforge.mud.core.web.model.WebSocketContext;
import com.agonyforge.mud.demo.cli.RepositoryBundle;
import com.agonyforge.mud.demo.model.constant.Effort;
import com.agonyforge.mud.demo.model.constant.Stat;
import com.agonyforge.mud.demo.model.impl.MudCharacter;
import com.agonyforge.mud.demo.service.CommService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

import java.util.Arrays;
import java.util.List;

/**
 * This is just for testing and probably won't stay in the game.
 * ROLL &lt;stat&gt; &lt;effort&gt;
 */
@Component
public class RollCommand extends AbstractCommand {
    private final DiceService diceService;

    @Autowired
    public RollCommand(RepositoryBundle repositoryBundle, DiceService diceService, CommService commService) {
        super(repositoryBundle, commService);

        this.diceService = diceService;
    }

    @Override
    public Question execute(Question question, WebSocketContext webSocketContext, List<String> tokens, Input input, Output output) {
        MudCharacter ch = getCurrentCharacter(webSocketContext, output);

        if (tokens.size() <= 2) {
            output.append("[default]ROLL &lt;stat&gt; &lt;effort&gt;");
            return question;
        }

        Stat stat;
        Effort effort;

        try {
            stat = Stat.valueOf(tokens.get(1));
        } catch (IllegalArgumentException e) {
            output.append("[red]No such Stat.");
            return question;
        }

        try {
            effort = Arrays
                .stream(Effort.values())
                .filter(eff -> tokens.get(2).equalsIgnoreCase(eff.getName()))
                .findFirst()
                .orElseThrow(() -> new IllegalArgumentException("No such Effort."));
        } catch (IllegalArgumentException ee) {
            output.append("[red]%s", ee.getMessage());
            return question;
        }

        DiceResult attemptRoll = diceService.roll(1, 20, ch.getBaseStat(stat));

        output.append("[cyan]ATTEMPT: [yellow]%d [dwhite]+ [cyan]%d [dwhite]= [white]%d [dwhite]for [cyan]%s[dwhite]!",
            attemptRoll.getRoll(0), ch.getBaseStat(stat), attemptRoll.getModifiedRoll(0), stat.getAbbreviation());

        DiceResult effortRoll = diceService.roll(1, effort.getDie(), ch.getBaseEffort(effort));

        output.append("[magenta]EFFORT: [yellow]%d [dwhite]+ [magenta]%d [dwhite]= [white]%d [dwhite]for [magenta]%s[dwhite]!",
            effortRoll.getRoll(0), ch.getBaseEffort(effort), effortRoll.getModifiedRoll(0), effort.getName());

        int total = effortRoll.getModifiedRoll(0);

        if (attemptRoll.getRoll(0) == 20) {
            DiceResult ultimateRoll = diceService.roll(1, Effort.ULTIMATE.getDie(), ch.getBaseEffort(Effort.ULTIMATE));

            output.append("[yellow]ULTIMATE: [yellow]%d [dwhite]+ [yellow]%d [dwhite]= [white]%s[dwhite]!",
                ultimateRoll.getRoll(0), ch.getBaseEffort(Effort.ULTIMATE), ultimateRoll.getModifiedRoll(0));

            total += ultimateRoll.getModifiedRoll(0);
        }

        output.append("[green]TOTAL: [white]%d", total);

        return question;
    }
}
