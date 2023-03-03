package com.agonyforge.mud.demo.cli.command;

import com.agonyforge.mud.core.cli.Question;
import com.agonyforge.mud.core.web.model.Input;
import com.agonyforge.mud.core.web.model.Output;
import com.agonyforge.mud.core.web.model.WebSocketContext;
import com.agonyforge.mud.models.dynamodb.impl.MudCharacter;
import com.agonyforge.mud.models.dynamodb.impl.MudRoom;
import com.agonyforge.mud.models.dynamodb.service.CommService;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;
import org.springframework.util.StringUtils;

import java.util.List;
import java.util.Optional;

@Component
public class LookCommand extends AbstractCommand {
    private static final Logger LOGGER = LoggerFactory.getLogger(LookCommand.class);

    public static Output doLook(RepositoryBundle repositoryBundle,
                                MudCharacter ch,
                                MudRoom room) {

        Output output = new Output();

        output
            .append("[yellow](%d) %s", room.getId(), room.getName())
            .append("[dwhite]%s", room.getDescription())
            .append("[dcyan]Exits: %s", String.join(", ", room.getExits()));

        repositoryBundle.getCharacterRepository().getByRoom(room.getId())
            .stream()
            .filter(target -> !target.equals(ch))
            .forEach(target -> output.append("[green]%s is here.", target.getName()));

        repositoryBundle.getItemRepository().getByRoom(room.getId())
            .forEach(target -> output.append("[green]%s",
                StringUtils.capitalize(target.getLongDescription())));

        return output;
    }

    @Autowired
    public LookCommand(RepositoryBundle repositoryBundle, CommService commService) {
        super(repositoryBundle, commService);
    }

    @Override
    public Question execute(Question question,
                            WebSocketContext webSocketContext,
                            List<String> tokens,
                            Input input,
                            Output output) {
        MudCharacter ch = getCurrentCharacter(webSocketContext, output);
        Optional<MudRoom> roomOptional = getRepositoryBundle().getRoomRepository().getById(ch.getRoomId());

        if (roomOptional.isEmpty()) {
            output.append("[black]You are floating in the void...");
            LOGGER.error("{} is floating in the void!", ch.getName());

            return question;
        }

        MudRoom room = roomOptional.get();

        output.append(doLook(getRepositoryBundle(), ch, room));

        return question;
    }
}
