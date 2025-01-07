package com.agonyforge.mud.demo.cli.question.login;

import com.agonyforge.mud.core.cli.Question;
import com.agonyforge.mud.core.cli.Response;
import com.agonyforge.mud.core.service.SessionAttributeService;
import com.agonyforge.mud.core.web.model.Input;
import com.agonyforge.mud.core.web.model.Output;
import com.agonyforge.mud.core.web.model.WebSocketContext;
import com.agonyforge.mud.demo.cli.RepositoryBundle;
import com.agonyforge.mud.demo.cli.command.LookCommand;
import com.agonyforge.mud.demo.cli.question.BaseQuestion;
import com.agonyforge.mud.demo.model.impl.LocationComponent;
import com.agonyforge.mud.demo.model.impl.MudCharacter;
import com.agonyforge.mud.demo.model.impl.MudCharacterTemplate;
import com.agonyforge.mud.demo.model.impl.MudRoom;
import com.agonyforge.mud.demo.service.CommService;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.ApplicationContext;
import org.springframework.stereotype.Component;

import java.util.Optional;

import static com.agonyforge.mud.core.config.SessionConfiguration.MUD_CHARACTER;

@Component
public class CharacterViewQuestion extends BaseQuestion {
    static final Long START_ROOM = 100L;

    private static final Logger LOGGER = LoggerFactory.getLogger(CharacterViewQuestion.class);

    private final CommService commService;
    private final SessionAttributeService sessionAttributeService;
    private final CharacterSheetFormatter characterSheetFormatter;

    @Autowired
    public CharacterViewQuestion(ApplicationContext applicationContext,
                                 RepositoryBundle repositoryBundle,
                                 CommService commService,
                                 SessionAttributeService sessionAttributeService,
                                 CharacterSheetFormatter characterSheetFormatter) {
        super(applicationContext, repositoryBundle);
        this.commService = commService;
        this.sessionAttributeService = sessionAttributeService;
        this.characterSheetFormatter = characterSheetFormatter;
    }

    @Override
    public Output prompt(WebSocketContext wsContext) {
        Output output = new Output();
        Optional<MudCharacterTemplate> chOptional = getCharacterPrototype(wsContext, output);

        if (chOptional.isPresent()) {
            MudCharacterTemplate ch = chOptional.get();

            characterSheetFormatter.format(ch, output);

            output.append("");

            if (!ch.getComplete()) {
                output.append("[blue]E[black]) Edit this character");
            } else {
                output.append("[green]P[black]) Play as this character");
            }

            output.append("[red]D[black]) Delete this character");
            output.append("[dwhite]B[black]) Go back");
            output.append("[black]Please [white]make your selection[black]: ");
        }

        return output;
    }

    @Override
    public Response answer(WebSocketContext wsContext, Input input) {
        Output output = new Output();
        Question next = this;
        Optional<MudCharacterTemplate> chOptional = getCharacterPrototype(wsContext, output);

        if ("P".equalsIgnoreCase(input.getInput())) {
            Optional<MudRoom> roomOptional = getRepositoryBundle().getRoomRepository().findById(START_ROOM);

            if (chOptional.isPresent() && roomOptional.isPresent()) {
                MudCharacterTemplate chPrototype = chOptional.get();

                if (!chPrototype.getComplete()) {
                    output.append("[red]This character is not finished yet. You must go through character creation first.");
                    return new Response(next, output);
                }

                Optional<MudCharacter> chOp = getRepositoryBundle().getCharacterRepository().findByCharacterName(chPrototype.getCharacter().getName());
                MudCharacter ch = chOp.orElseGet(chPrototype::buildInstance);
                MudRoom startRoom = roomOptional.get();

                if (ch.getLocation() == null) {
                    ch.setLocation(new LocationComponent());
                } else if (ch.getLocation().getRoom() != null) {
                    output.append("[red]This character is already playing. Try a different one, or create a new one.");
                    return new Response(next, output);
                }

                ch.getPlayer().setWebSocketSession(wsContext.getSessionId());
                ch.getLocation().setRoom(startRoom);

                ch = getRepositoryBundle().getCharacterRepository().save(ch);
                wsContext.getAttributes().put(MUD_CHARACTER, ch.getId());

                output.append(LookCommand.doLook(getRepositoryBundle(), sessionAttributeService, ch, startRoom));

                LOGGER.info("{} has entered the game", ch.getCharacter().getName());
                commService.sendToAll(wsContext, new Output("[yellow]%s has entered the game!", ch.getCharacter().getName()), ch);

                next = getQuestion("commandQuestion");
            } else {
                if (roomOptional.isEmpty()) {
                    LOGGER.error("Start room with ID {} was empty!", START_ROOM);
                }
            }
        } else if ("E".equalsIgnoreCase(input.getInput())) {
            next = getQuestion("characterPronounQuestion");
        } else if ("D".equalsIgnoreCase(input.getInput())) {
            next = getQuestion("characterDeleteQuestion");
        } else if ("B".equalsIgnoreCase(input.getInput())) {
            next = getQuestion("characterMenuQuestion");
        } else {
            output.append("[red]Unknown selection. Please try again.");
        }

        return new Response(next, output);
    }
}
