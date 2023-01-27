package com.agonyforge.mud.demo.cli;

import com.agonyforge.mud.core.cli.Question;
import com.agonyforge.mud.core.cli.Response;
import com.agonyforge.mud.core.web.model.Input;
import com.agonyforge.mud.core.web.model.Output;
import com.agonyforge.mud.core.web.model.WebSocketContext;
import com.agonyforge.mud.demo.cli.command.LookCommand;
import com.agonyforge.mud.models.dynamodb.impl.MudCharacter;
import com.agonyforge.mud.models.dynamodb.impl.MudRoom;
import com.agonyforge.mud.models.dynamodb.repository.MudCharacterRepository;
import com.agonyforge.mud.models.dynamodb.repository.MudItemRepository;
import com.agonyforge.mud.models.dynamodb.repository.MudRoomRepository;
import com.agonyforge.mud.models.dynamodb.service.CommService;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.ApplicationContext;
import org.springframework.stereotype.Component;

import java.util.Optional;

@Component
public class CharacterViewQuestion extends AbstractQuestion {
    static final Long START_ROOM = 100L;

    private static final Logger LOGGER = LoggerFactory.getLogger(CharacterViewQuestion.class);

    private final MudRoomRepository roomRepository;
    private final CommService commService;

    @Autowired
    public CharacterViewQuestion(ApplicationContext applicationContext,
                                 MudCharacterRepository characterRepository,
                                 MudItemRepository itemRepository,
                                 MudRoomRepository roomRepository,
                                 CommService commService) {
        super(applicationContext, characterRepository, itemRepository);
        this.roomRepository = roomRepository;
        this.commService = commService;
    }

    @Override
    public Output prompt(WebSocketContext wsContext) {
        Output output = new Output();
        Optional<MudCharacter> chOptional = getCharacter(wsContext, output);

        if (chOptional.isPresent()) {
            MudCharacter ch = chOptional.get();

            output.append("[dcyan]Character Sheet");
            output.append(String.format("[default]Name: [cyan]%s", ch.getName()));
            output.append(String.format("[default]Pronouns: [cyan]%s/%s", ch.getPronoun().getSubject(), ch.getPronoun().getObject()));
            output.append("");
            output.append("[green]P[black]) Play as this character");
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

        if ("P".equalsIgnoreCase(input.getInput())) {
            Optional<MudCharacter> chOptional = getCharacter(wsContext, output);
            Optional<MudRoom> roomOptional = roomRepository.getById(START_ROOM);

            if (chOptional.isPresent() && roomOptional.isPresent()) {
                MudCharacter chPrototype = chOptional.get();
                MudCharacter ch = chPrototype.buildInstance();
                MudRoom room = roomOptional.get();

                ch.setRoomId(START_ROOM); // TODO configurable start room
                ch.setWebSocketSession(wsContext.getSessionId());

                getCharacterRepository().save(ch);

                LOGGER.info("{} has entered the game.", ch.getName());
                commService.sendToAll(wsContext, new Output(String.format("[yellow]%s has entered the game!", ch.getName())), ch);

                output.append(LookCommand.doLook(getCharacterRepository(), getItemRepository(), ch, room));

                next = getQuestion("commandQuestion");
            }
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
