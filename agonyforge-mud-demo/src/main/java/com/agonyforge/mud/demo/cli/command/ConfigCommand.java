package com.agonyforge.mud.demo.cli.command;

import com.agonyforge.mud.core.cli.Question;
import com.agonyforge.mud.core.web.model.Input;
import com.agonyforge.mud.core.web.model.Output;
import com.agonyforge.mud.core.web.model.WebSocketContext;
import com.agonyforge.mud.demo.cli.RepositoryBundle;
import com.agonyforge.mud.demo.model.constant.PlayerFlag;
import com.agonyforge.mud.demo.model.impl.MudCharacter;
import com.agonyforge.mud.demo.service.CommService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.ApplicationContext;
import org.springframework.stereotype.Component;

import java.util.Arrays;
import java.util.List;

@Component
public class ConfigCommand extends AbstractCommand {
    @Autowired
    public ConfigCommand(RepositoryBundle repositoryBundle, CommService commService, ApplicationContext applicationContext) {
        super(repositoryBundle, commService, applicationContext);
    }

    @Override
    public Question execute(Question question, WebSocketContext webSocketContext, List<String> tokens, Input input, Output output) {
        MudCharacter ch = getCurrentCharacter(webSocketContext, output);

        if (tokens.size() == 1) {
            output.append("[yellow]Admin Configuration Flags:");
            Arrays.stream(PlayerFlag.values()).forEachOrdered(flag -> {
                boolean isEnabled = ch.getPlayer().getAdminFlags().contains(flag);

                output.append("[yellow]%s: %s", flag.name(), isEnabled);
            });
        } else if (tokens.size() == 2) {
            PlayerFlag flag = PlayerFlag.valueOf(tokens.get(1));

            if (ch.getPlayer().getAdminFlags().contains(flag)) {
                ch.getPlayer().getAdminFlags().remove(flag);
                output.append("[yellow]%s disabled.", flag.name());
            } else {
                ch.getPlayer().getAdminFlags().add(flag);
                output.append("[yellow]%s enabled.", flag.name());
            }
        }

        return question;
    }
}
