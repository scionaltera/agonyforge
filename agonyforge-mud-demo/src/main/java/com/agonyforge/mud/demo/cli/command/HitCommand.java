package com.agonyforge.mud.demo.cli.command;

import com.agonyforge.mud.core.cli.Question;
import com.agonyforge.mud.core.web.model.Input;
import com.agonyforge.mud.core.web.model.Output;
import com.agonyforge.mud.core.web.model.WebSocketContext;
import com.agonyforge.mud.demo.cli.RepositoryBundle;
import com.agonyforge.mud.demo.model.impl.MudCharacter;
import com.agonyforge.mud.demo.model.impl.MudRoom;
import com.agonyforge.mud.demo.service.CommService;
import org.springframework.context.ApplicationContext;
import org.springframework.stereotype.Component;

import java.util.List;
import java.util.Optional;

@Component
public class HitCommand extends AbstractCommand {
    public HitCommand(RepositoryBundle repositoryBundle, CommService commService, ApplicationContext applicationContext) {
        super(repositoryBundle, commService, applicationContext);
    }

    @Override
    public Question execute(Question question, WebSocketContext webSocketContext, List<String> tokens, Input input, Output output) {
        if (tokens.size() <= 1) {
            output.append("[default]Who do you want to hit?");
            return question;
        }

        MudCharacter ch = getCurrentCharacter(webSocketContext, output);
        Optional<MudCharacter> targetOptional = findRoomCharacter(ch, tokens.get(1));

        if (targetOptional.isEmpty()) {
            output.append("[default]You don't see anyone like that here.");
            return question;
        }

        MudCharacter target = targetOptional.get();
        MudRoom room = ch.getLocation().getRoom();

        output.append("[default]You hit %s!", target.getCharacter().getName());
        getCommService().sendTo(target, new Output("[default]%s hits you!", ch.getCharacter().getName()));
        getCommService().sendToRoom(
            room.getId(),
            new Output("[default]%s hits %s!",
                ch.getCharacter().getName(),
                target.getCharacter().getName()),
            ch, target);

        return question;
    }
}
