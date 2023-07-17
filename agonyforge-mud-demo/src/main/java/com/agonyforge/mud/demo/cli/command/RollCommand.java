package com.agonyforge.mud.demo.cli.command;

import com.agonyforge.mud.core.cli.Question;
import com.agonyforge.mud.core.web.model.Input;
import com.agonyforge.mud.core.web.model.Output;
import com.agonyforge.mud.core.web.model.WebSocketContext;
import com.agonyforge.mud.demo.cli.RepositoryBundle;
import com.agonyforge.mud.models.dynamodb.constant.Effort;
import com.agonyforge.mud.models.dynamodb.constant.Stat;
import com.agonyforge.mud.models.dynamodb.impl.MudCharacter;
import com.agonyforge.mud.models.dynamodb.service.CommService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

import java.util.Arrays;
import java.util.List;
import java.util.Random;

/**
 * This is just for testing and probably won't stay in the game.
 * ROLL &lt;stat&gt; &lt;effort&gt;
 */
@Component
public class RollCommand extends AbstractCommand {
    private static final Random RANDOM = new Random();

    @Autowired
    public RollCommand(RepositoryBundle repositoryBundle, CommService commService) {
        super(repositoryBundle, commService);
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

        int attemptRoll = RANDOM.nextInt(1, 21);
        int attemptResult = ch.getStat(stat) + attemptRoll;

        output.append("[cyan]ATTEMPT: [yellow]%d [dwhite]+ [cyan]%d [dwhite]= [white]%d [dwhite]for [cyan]%s[dwhite]!",
            attemptRoll, ch.getStat(stat), attemptResult, stat.getAbbreviation());

        int effortRoll = RANDOM.nextInt(1, effort.getDie() + 1);
        int effortResult = ch.getEffort(effort) + effortRoll;

        output.append("[magenta]EFFORT: [yellow]%d [dwhite]+ [magenta]%d [dwhite]= [white]%d [dwhite]for [magenta]%s[dwhite]!",
            effortRoll, ch.getEffort(effort), effortResult, effort.getName());

        if (attemptRoll == 20) {
            int ultimateRoll = RANDOM.nextInt(1, Effort.ULTIMATE.getDie() + 1);
            int ultimateResult = ch.getEffort(Effort.ULTIMATE) + ultimateRoll;

            output.append("[yellow]ULTIMATE: [yellow]%d [dwhite]+ [yellow]%d [dwhite]= [white]%s[dwhite]!",
                ultimateRoll, ch.getEffort(Effort.ULTIMATE), ultimateResult);

            effortResult += ultimateResult;
        }

        output.append("[green]TOTAL: [white]%d", effortResult);

        return question;
    }
}
