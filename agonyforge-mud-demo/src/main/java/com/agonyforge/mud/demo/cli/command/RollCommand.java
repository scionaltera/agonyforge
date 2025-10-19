package com.agonyforge.mud.demo.cli.command;

import com.agonyforge.mud.core.cli.Question;
import com.agonyforge.mud.core.service.dice.DiceResult;
import com.agonyforge.mud.core.service.dice.DiceService;
import com.agonyforge.mud.core.web.model.Output;
import com.agonyforge.mud.core.web.model.WebSocketContext;
import com.agonyforge.mud.demo.cli.Binding;
import com.agonyforge.mud.demo.cli.RepositoryBundle;
import com.agonyforge.mud.demo.model.constant.Effort;
import com.agonyforge.mud.demo.model.constant.Stat;
import com.agonyforge.mud.demo.model.impl.MudCharacter;
import com.agonyforge.mud.demo.service.CommService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.ApplicationContext;
import org.springframework.stereotype.Component;

import java.util.List;

import static com.agonyforge.mud.demo.cli.TokenType.*;

/**
 * This is just for testing and probably won't stay in the game.
 * ROLL &lt;stat&gt; &lt;effort&gt;
 */
@Component
public class RollCommand extends AbstractCommand {
    private final DiceService diceService;

    @Autowired
    public RollCommand(RepositoryBundle repositoryBundle,
                       CommService commService,
                       ApplicationContext applicationContext,
                       DiceService diceService) {
        super(repositoryBundle, commService, applicationContext);

        this.diceService = diceService;

        addSyntax(STAT, EFFORT);
    }

    @Override
    public Question execute(Question question, WebSocketContext webSocketContext, List<Binding> bindings, Output output) {
        MudCharacter ch = getCurrentCharacter(webSocketContext, output);
        Stat stat = bindings.get(1).asStat();
        Effort effort = bindings.get(2).asEffort();
        DiceResult attemptRoll = diceService.roll(1, 20, ch.getCharacter().getStat(stat));

        output.append("[cyan]ATTEMPT: [yellow]%d [dwhite]+ [cyan]%d [dwhite]= [white]%d [dwhite]for [cyan]%s[dwhite]!",
            attemptRoll.getRoll(0), ch.getCharacter().getStat(stat), attemptRoll.getModifiedRoll(0), stat.getAbbreviation());

        DiceResult effortRoll = diceService.roll(1, effort.getDie(), ch.getCharacter().getEffort(effort));

        output.append("[magenta]EFFORT: [yellow]%d [dwhite]+ [magenta]%d [dwhite]= [white]%d [dwhite]for [magenta]%s[dwhite]!",
            effortRoll.getRoll(0), ch.getCharacter().getEffort(effort), effortRoll.getModifiedRoll(0), effort.getName());

        int total = effortRoll.getModifiedRoll(0);

        if (attemptRoll.getRoll(0) == 20) {
            DiceResult ultimateRoll = diceService.roll(1, Effort.ULTIMATE.getDie(), ch.getCharacter().getEffort(Effort.ULTIMATE));

            output.append("[yellow]ULTIMATE: [yellow]%d [dwhite]+ [yellow]%d [dwhite]= [white]%s[dwhite]!",
                ultimateRoll.getRoll(0), ch.getCharacter().getEffort(Effort.ULTIMATE), ultimateRoll.getModifiedRoll(0));

            total += ultimateRoll.getModifiedRoll(0);
        }

        output.append("[green]TOTAL: [white]%d", total);

        return question;
    }
}
