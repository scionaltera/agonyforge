package com.agonyforge.mud.demo.cli.command;

import com.agonyforge.mud.core.cli.Question;
import com.agonyforge.mud.core.service.SessionAttributeService;
import com.agonyforge.mud.core.web.model.Input;
import com.agonyforge.mud.core.web.model.Output;
import com.agonyforge.mud.core.web.model.WebSocketContext;
import com.agonyforge.mud.demo.cli.RepositoryBundle;
import com.agonyforge.mud.demo.model.constant.AdminFlag;
import com.agonyforge.mud.demo.model.impl.MudCharacter;
import com.agonyforge.mud.demo.model.impl.MudRoom;
import com.agonyforge.mud.demo.service.CommService;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.ApplicationContext;
import org.springframework.stereotype.Component;
import org.springframework.util.StringUtils;

import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Optional;

@Component
public class LookCommand extends AbstractCommand {
    private static final Logger LOGGER = LoggerFactory.getLogger(LookCommand.class);

    public static Output doLook(RepositoryBundle repositoryBundle,
                                SessionAttributeService sessionAttributeService,
                                MudCharacter ch,
                                MudRoom room) {

        Output output = new Output();

        if (ch.getPlayer() != null && ch.getPlayer().getAdminFlags().contains(AdminFlag.HOLYLIGHT)) {
            output.append("[yellow](%d) %s %s", room.getId(), room.getName(), room.getFlags());
        } else {
            output.append("[yellow]%s", room.getName());
        }

        output
            .append("[default]%s", room.getDescription())
            .append("[dcyan]Exits: %s", String.join(" ", room.getExits()));

        repositoryBundle.getCharacterRepository().findByLocationRoom(room)
            .stream()
            .filter(target -> !target.equals(ch))
            .forEach(target -> {
                Map<String, Object> attributes;

                if (target.getPlayer() != null) {
                    attributes = sessionAttributeService.getSessionAttributes(target.getPlayer().getWebSocketSession());
                } else {
                    attributes = new HashMap<>();
                }

                String question = (String)attributes.get("MUD.QUESTION");
                String action;
                String flags = "";

                if (target.getPlayer() != null && attributes.isEmpty()) {
                    flags += "[dred]([red]LINKDEAD[dred])";
                }

                if (question != null && question.endsWith("EditorQuestion")) {
                    action = "busy altering the threads of time and space";
                } else {
                    action = "here";
                }

                if (target.getPlayer() != null || (ch.getPlayer() != null && !ch.getPlayer().getAdminFlags().contains(AdminFlag.HOLYLIGHT))) {
                    output.append("[green]%s is %s. %s", StringUtils.capitalize(target.getCharacter().getName()), action, flags);
                } else {
                    output.append("[green](%d) %s is %s.", target.getTemplate().getId(), StringUtils.capitalize(target.getCharacter().getName()), action, flags);
                }
            });

        repositoryBundle.getItemRepository().findByLocationRoom(room)
            .forEach(target -> {
                if (ch.getPlayer() != null && ch.getPlayer().getAdminFlags().contains(AdminFlag.HOLYLIGHT)) {
                    output.append("[green](%d) %s",
                        target.getTemplate().getId(),
                        StringUtils.capitalize(target.getItem().getLongDescription()));
                } else {
                    output.append("[green]%s", StringUtils.capitalize(target.getItem().getLongDescription()));
                }
            });

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

        addSyntax();
    }

    @Override
    public Question execute(Question question,
                            WebSocketContext webSocketContext,
                            List<String> tokens,
                            Input input,
                            Output output) {
        MudCharacter ch = getCurrentCharacter(webSocketContext, output);
        Optional<MudRoom> roomOptional = Optional.ofNullable(ch.getLocation().getRoom());

        if (roomOptional.isEmpty()) {
            output.append("[black]You are floating in the void...");
            LOGGER.error("{} is floating in the void!", ch.getCharacter().getName());

            return question;
        }

        MudRoom room = roomOptional.get();

        output.append(doLook(getRepositoryBundle(), sessionAttributeService, ch, room));

        return question;
    }
}
