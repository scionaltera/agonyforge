package com.agonyforge.mud.demo.cli.command;

import com.agonyforge.mud.core.cli.Question;
import com.agonyforge.mud.core.service.SessionAttributeService;
import com.agonyforge.mud.core.web.model.Input;
import com.agonyforge.mud.core.web.model.Output;
import com.agonyforge.mud.core.web.model.WebSocketContext;
import com.agonyforge.mud.demo.cli.RepositoryBundle;
import com.agonyforge.mud.demo.model.impl.MudCharacter;
import com.agonyforge.mud.demo.model.impl.MudRoom;
import com.agonyforge.mud.demo.service.CommService;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.ApplicationContext;
import org.springframework.stereotype.Component;
import org.springframework.util.StringUtils;

import java.util.List;
import java.util.Optional;

@Component
public class LookCommand extends AbstractCommand {
    private static final Logger LOGGER = LoggerFactory.getLogger(LookCommand.class);

    public static Output doLook(RepositoryBundle repositoryBundle,
                                SessionAttributeService sessionAttributeService,
                                MudCharacter ch,
                                MudRoom room) {

        Output output = new Output();

        output
            .append("[yellow](%d) %s", room.getId(), room.getName())
            .append("[dwhite]%s", room.getDescription())
            .append("[dcyan]Exits: %s", String.join(" ", room.getExits()));

        repositoryBundle.getCharacterRepository().findByRoomId(room.getId())
            .stream()
            .filter(target -> !target.equals(ch))
            .forEach(target -> {
                String question = (String)sessionAttributeService.getSessionAttributes(target.getWebSocketSession()).get("MUD.QUESTION");
                String action;

                switch (question) {
                    case "roomEditorQuestion" -> action = "busy editing something";
                    default -> action = "here";
                }

                output.append("[green]%s is %s.", target.getName(), action);
            });

        repositoryBundle.getItemRepository().getByRoom(room.getId())
            .forEach(target -> output.append("[green]%s",
                StringUtils.capitalize(target.getLongDescription())));

        return output;
    }

    private final SessionAttributeService sessionAttributeService;

    @Autowired
    public LookCommand(RepositoryBundle repositoryBundle,
                       CommService commService,
                       ApplicationContext applicationContext,
                       SessionAttributeService sessionAttributeService) {
        super(repositoryBundle, commService, applicationContext);

        this.sessionAttributeService = sessionAttributeService;
    }

    @Override
    public Question execute(Question question,
                            WebSocketContext webSocketContext,
                            List<String> tokens,
                            Input input,
                            Output output) {
        MudCharacter ch = getCurrentCharacter(webSocketContext, output);
        Optional<MudRoom> roomOptional = getRepositoryBundle().getRoomRepository().findById(ch.getRoomId());

        if (roomOptional.isEmpty()) {
            output.append("[black]You are floating in the void...");
            LOGGER.error("{} is floating in the void!", ch.getName());

            return question;
        }

        MudRoom room = roomOptional.get();

        output.append(doLook(getRepositoryBundle(), sessionAttributeService, ch, room));

        return question;
    }
}
