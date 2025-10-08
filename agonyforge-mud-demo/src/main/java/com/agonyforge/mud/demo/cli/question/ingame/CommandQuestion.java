package com.agonyforge.mud.demo.cli.question.ingame;

import com.agonyforge.mud.core.cli.Question;
import com.agonyforge.mud.core.cli.Response;
import com.agonyforge.mud.core.cli.Tokenizer;
import com.agonyforge.mud.core.web.model.Input;
import com.agonyforge.mud.core.web.model.Output;
import com.agonyforge.mud.core.web.model.WebSocketContext;
import com.agonyforge.mud.demo.cli.RepositoryBundle;
import com.agonyforge.mud.demo.cli.command.AbstractCommand;
import com.agonyforge.mud.demo.cli.question.BaseQuestion;
import com.agonyforge.mud.demo.cli.question.CommandException;
import com.agonyforge.mud.demo.model.impl.CommandReference;
import com.agonyforge.mud.demo.model.impl.MudCharacter;
import com.agonyforge.mud.demo.model.impl.Role;
import com.agonyforge.mud.demo.model.repository.CommandRepository;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.NoSuchBeanDefinitionException;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.ApplicationContext;
import org.springframework.data.domain.Sort;
import org.springframework.stereotype.Component;

import java.util.*;

@Component
public class CommandQuestion extends BaseQuestion {
    private static final Logger LOGGER = LoggerFactory.getLogger(CommandQuestion.class);

    private final ApplicationContext applicationContext;
    private final CommandRepository commandRepository;

    @Autowired
    public CommandQuestion(ApplicationContext applicationContext,
                           RepositoryBundle repositoryBundle,
                           CommandRepository commandRepository) {
        super(applicationContext, repositoryBundle);
        this.applicationContext = applicationContext;
        this.commandRepository = commandRepository;
    }

    @Override
    public Output prompt(WebSocketContext wsContext) {
        Output output = new Output();
        Optional<MudCharacter> chOptional = getCharacter(wsContext, output);

        if (chOptional.isPresent()) {
            output
                .append("")
                .append("[green]%s [red]%d[dred]/[red]%d[default]> ",
                    chOptional.get().getCharacter().getName(),
                    chOptional.get().getCharacter().getHitPoints(),
                    chOptional.get().getCharacter().getMaxHitPoints());
        } else {
            output
                .append("")
                .append("[default]> ");
        }

        return output;
    }

    @Override
    public Response answer(WebSocketContext webSocketContext, Input input) {
        Output output = new Output();

        if (input.getInput().isBlank()) {
            return new Response(this, output);
        }

        List<String> tokens = Tokenizer.tokenize(input.getInput());
        Optional<CommandReference> refOptional = commandRepository.findFirstByNameStartingWith(tokens.get(0).toUpperCase(Locale.ROOT), Sort.by(Sort.Order.asc("priority")));

        if (refOptional.isEmpty()) {
            output.append("[default]Huh?");
            return new Response(this, output);
        }

        CommandReference ref = refOptional.get();

        try {
            AbstractCommand command = applicationContext.getBean(ref.getBeanName(), AbstractCommand.class);
            MudCharacter ch = getCharacter(webSocketContext, output).orElseThrow();

            if (ch.getPlayer() != null && (ch.getPlayer().getRoles().stream().anyMatch(role -> role.getCommands().contains(ref)) || ch.getPlayer().getRoles().stream().anyMatch(Role::isImplementor))) {
                /* TODO command refactor
                 * 1. [DONE] tokenize() should return unmodified (not capitalized) strings broken into tokens.
                 * 2. For each syntax of matching length, attempt to bind objects by type.
                 *    If a bind fails and the token is on the lookahead list (e.g. "a", "an", "the", "some"), try moving to the next token.
                 *      What if an NPC is named "Theodore" and the tokens are ["THE", "GOBLIN"] and there is also a "goblin" in the room?
                 *      The syntax would be "<character in room>" so we would have to look ahead and see if we could bind the next token too.
                 *      If so we could throw "the" away. If not we have a syntax error.
                 *    If a bind still fails, try the next syntax.
                 *    If no more syntaxes, bail out with an error.
                 * 3. Pass only the array of bound objects to execute().
                 */

                Question next = command.execute(this, webSocketContext, tokens, input, output);
                return new Response(next, output);
            }

            LOGGER.warn("Request by {} ({}) to use command {} denied due to missing role", ch.getPlayer().getUsername(), ch.getCharacter().getName(), ref.getName());
        } catch (CommandException e) {
            LOGGER.warn("Command failed: {}", e.getMessage());
            output.append("[red]Oops! Something went wrong... the error has been reported!");
            return new Response(this, output);
        } catch (NoSuchBeanDefinitionException e) {
            LOGGER.warn("No Command found for beanName: {}", ref.getBeanName());
        }

        output.append("[default]Huh?");
        return new Response(this, output);
    }
}
