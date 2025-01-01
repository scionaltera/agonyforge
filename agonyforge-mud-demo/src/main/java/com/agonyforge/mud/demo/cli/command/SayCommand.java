package com.agonyforge.mud.demo.cli.command;

import com.agonyforge.mud.core.cli.Question;
import com.agonyforge.mud.core.web.model.Input;
import com.agonyforge.mud.core.web.model.Output;
import com.agonyforge.mud.core.web.model.WebSocketContext;
import com.agonyforge.mud.demo.cli.RepositoryBundle;
import com.agonyforge.mud.demo.service.CommService;
import com.agonyforge.mud.demo.model.impl.MudCharacter;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.ApplicationContext;
import org.springframework.stereotype.Component;

import java.util.List;

@Component
public class SayCommand extends AbstractCommand {

    @Autowired
    public SayCommand(RepositoryBundle repositoryBundle, CommService commService, ApplicationContext applicationContext) {

        super(repositoryBundle, commService, applicationContext);
    }

    @Override
    public Question execute(Question question,
                            WebSocketContext webSocketContext,
                            List<String> tokens,
                            Input input,
                            Output output) {
        String message = Command.stripFirstWord(input.getInput());

        if (message.isBlank()) {
            output.append("[default]What would you like to say?");
            return question;
        }

        MudCharacter ch = getCurrentCharacter(webSocketContext, output);

        output.append("[cyan]You say, '%s[cyan]'", message);
        getCommService().sendToRoom(webSocketContext, ch.getRoomId(), new Output("[cyan]%s says, '%s[cyan]'", ch.getCharacter().getName(), message));

        return question;
    }
}
