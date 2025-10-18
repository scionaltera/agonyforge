package com.agonyforge.mud.demo.cli.command;

import com.agonyforge.mud.core.cli.Question;
import com.agonyforge.mud.core.web.model.Output;
import com.agonyforge.mud.core.web.model.WebSocketContext;
import com.agonyforge.mud.demo.cli.Binding;
import com.agonyforge.mud.demo.cli.RepositoryBundle;
import com.agonyforge.mud.demo.model.constant.AdminFlag;
import com.agonyforge.mud.demo.model.impl.MudCharacter;
import com.agonyforge.mud.demo.service.CommService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.ApplicationContext;
import org.springframework.stereotype.Component;

import java.util.Arrays;
import java.util.List;

import static com.agonyforge.mud.demo.cli.TokenType.WORD;

@Component
public class ConfigCommand extends AbstractCommand {
    @Autowired
    public ConfigCommand(RepositoryBundle repositoryBundle, CommService commService, ApplicationContext applicationContext) {
        super(repositoryBundle, commService, applicationContext);

        addSyntax();
        addSyntax(WORD);
    }

    @Override
    public Question execute(Question question, WebSocketContext webSocketContext, List<String> tokens, Output output) {
        MudCharacter ch = getCurrentCharacter(webSocketContext, output);

        if (tokens.size() == 1) {
            output.append("[yellow]Admin Configuration Flags:");
            Arrays.stream(AdminFlag.values()).forEachOrdered(flag -> {
                boolean isEnabled = ch.getPlayer().getAdminFlags().contains(flag);

                output.append("[yellow]%s: %s", flag.name(), isEnabled);
            });
        } else if (tokens.size() == 2) {
            try {
                AdminFlag flag = AdminFlag.valueOf(tokens.get(1).toUpperCase());

                if (ch.getPlayer().getAdminFlags().contains(flag)) {
                    ch.getPlayer().getAdminFlags().remove(flag);
                    output.append("[yellow]%s disabled.", flag.name());
                } else {
                    ch.getPlayer().getAdminFlags().add(flag);
                    output.append("[yellow]%s enabled.", flag.name());
                }
            } catch (IllegalArgumentException e) {
                output.append("[red]No such config flag exists.");
            }
        }

        return question;
    }

    @Override
    public Question executeBinding(Question question, WebSocketContext webSocketContext, List<Binding> bindings, Output output) {
        MudCharacter ch = getCurrentCharacter(webSocketContext, output);

        if (bindings.size() == 1) {
            output.append("[yellow]Admin Configuration Flags:");
            Arrays.stream(AdminFlag.values()).forEachOrdered(flag -> {
                boolean isEnabled = ch.getPlayer().getAdminFlags().contains(flag);

                output.append("[yellow]%s: %s", flag.name(), isEnabled);
            });
        } else if (bindings.size() == 2) {
            try {
                AdminFlag flag = bindings.get(1).asAdminFlag();

                if (ch.getPlayer().getAdminFlags().contains(flag)) {
                    ch.getPlayer().getAdminFlags().remove(flag);
                    output.append("[yellow]%s disabled.", flag.name());
                } else {
                    ch.getPlayer().getAdminFlags().add(flag);
                    output.append("[yellow]%s enabled.", flag.name());
                }
            } catch (IllegalArgumentException e) {
                output.append("[red]No such config flag exists.");
            }
        }

        return question;
    }
}
