package com.agonyforge.mud.demo.cli.command;

import com.agonyforge.mud.core.cli.Question;
import com.agonyforge.mud.core.cli.Tokenizer;
import com.agonyforge.mud.core.web.model.Output;
import com.agonyforge.mud.core.web.model.WebSocketContext;
import com.agonyforge.mud.demo.cli.question.CommandException;
import com.agonyforge.mud.demo.cli.RepositoryBundle;
import com.agonyforge.mud.demo.model.impl.MudCharacter;
import com.agonyforge.mud.demo.model.impl.MudItem;
import com.agonyforge.mud.demo.service.CommService;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.context.ApplicationContext;

import java.util.ArrayList;
import java.util.List;
import java.util.Locale;
import java.util.Optional;

import static com.agonyforge.mud.core.config.SessionConfiguration.MUD_CHARACTER;

public abstract class AbstractCommand implements Command {
    private static final Logger LOGGER = LoggerFactory.getLogger(AbstractCommand.class);

    private final List<List<TokenType>> syntaxes = new ArrayList<>();
    private final RepositoryBundle repositoryBundle;
    private final CommService commService;
    private final ApplicationContext applicationContext;

    public AbstractCommand(RepositoryBundle repositoryBundle,
                           CommService commService,
                           ApplicationContext applicationContext) {
        this.repositoryBundle = repositoryBundle;
        this.commService = commService;
        this.applicationContext = applicationContext;
    }

    protected void addSyntax(TokenType... tokens) {
        syntaxes.add(List.of(tokens));
    }

    public List<List<TokenType>> getSyntaxes() {
        return new ArrayList<>(syntaxes);
    }

    protected RepositoryBundle getRepositoryBundle() {
        return repositoryBundle;
    }

    protected CommService getCommService() {
        return commService;
    }

    protected ApplicationContext getApplicationContext() {
        return applicationContext;
    }

    protected Question getQuestion(String name) {
        return applicationContext.getBean(name, Question.class);
    }

    protected MudCharacter getCurrentCharacter(WebSocketContext webSocketContext, Output output) {
        Long chId = (Long) webSocketContext.getAttributes().get(MUD_CHARACTER);
        Optional<MudCharacter> chOptional = getRepositoryBundle().getCharacterRepository().findById(chId);

        if (chOptional.isEmpty()) {
            LOGGER.error("Cannot look up character by ID: {}", chId);
            output.append("[red]Unable to find your character! The error has been reported.");
            throw new CommandException("Unable to load character: " + chId);
        }

        MudCharacter ch = chOptional.get();

        if (ch.getLocation().getRoom() == null) {
            output.append("[black]You are floating aimlessly in the void.");
            throw new CommandException("Character is in the void: " + chId);
        }

        return ch;
    }

    protected Optional<MudItem> findInventoryItem(MudCharacter ch, String token) {
        List<MudItem> items = getRepositoryBundle().getItemRepository().findByLocationHeld(ch);

        return items
            .stream()
            .filter(item -> item.getLocation().getWorn() == null || item.getLocation().getWorn().isEmpty())
            .filter(item -> item.getItem().getNameList()
                .stream()
                .anyMatch(name -> name.toUpperCase(Locale.ROOT).startsWith(token.toUpperCase(Locale.ROOT))))
            .findFirst();
    }

    protected Optional<MudItem> findWornItem(MudCharacter ch, String token) {
        List<MudItem> items = getRepositoryBundle().getItemRepository().findByLocationHeld(ch);

        return items
            .stream()
            .filter(item -> item.getLocation().getWorn() != null && !item.getLocation().getWorn().isEmpty())
            .filter(item -> item.getItem().getNameList()
                .stream()
                .anyMatch(name -> name.toUpperCase(Locale.ROOT).startsWith(token.toUpperCase(Locale.ROOT))))
            .findFirst();
    }

    protected Optional<MudItem> findRoomItem(MudCharacter ch, String token) {
        List<MudItem> items = getRepositoryBundle().getItemRepository().findByLocationRoom(ch.getLocation().getRoom());

        return items
            .stream()
            .filter(item -> item.getItem().getNameList()
                .stream()
                .anyMatch(name -> name.toUpperCase(Locale.ROOT).startsWith(token.toUpperCase(Locale.ROOT))))
            .findFirst();
    }

    protected Optional<MudCharacter> findRoomCharacter(MudCharacter ch, String token) {
        List<MudCharacter> targets = getRepositoryBundle().getCharacterRepository().findByLocationRoom(ch.getLocation().getRoom());

        return targets
            .stream()
            .filter(tch -> !tch.equals(ch))
            .filter(tch -> Tokenizer.tokenize(tch.getCharacter().getName()).stream()
                .filter(nameToken -> !nameToken.equals("A") && !nameToken.equals("AN") && !nameToken.equals("THE"))
                .anyMatch(nameToken -> nameToken.startsWith(token.toUpperCase(Locale.ROOT))))
            .findFirst();
    }

    protected Optional<MudCharacter> findWorldCharacter(MudCharacter ch, String token) {
        List<MudCharacter> targets = getRepositoryBundle().getCharacterRepository().findAll();

        return targets
            .stream()
            .filter(tch -> !tch.equals(ch))
            .filter(tch -> tch.getLocation() != null)
            .filter(tch -> Tokenizer.tokenize(tch.getCharacter().getName()).stream()
                .filter(nameToken -> !nameToken.equals("A") && !nameToken.equals("AN") && !nameToken.equals("THE"))
                .anyMatch(nameToken -> nameToken.startsWith(token.toUpperCase(Locale.ROOT))))
            .findFirst();
    }
}
