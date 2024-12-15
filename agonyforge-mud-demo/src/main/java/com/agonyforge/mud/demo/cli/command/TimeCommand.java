package com.agonyforge.mud.demo.cli.command;

import com.agonyforge.mud.core.cli.Question;
import com.agonyforge.mud.core.web.model.Input;
import com.agonyforge.mud.core.web.model.Output;
import com.agonyforge.mud.core.web.model.WebSocketContext;
import com.agonyforge.mud.demo.cli.RepositoryBundle;
import com.agonyforge.mud.demo.model.impl.MudProperty;
import com.agonyforge.mud.demo.service.CommService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.ApplicationContext;
import org.springframework.stereotype.Component;

import java.util.List;

import static com.agonyforge.mud.demo.event.WeatherListener.PROPERTY_HOUR;

@Component
public class TimeCommand extends AbstractCommand {
    @Autowired
    public TimeCommand(RepositoryBundle repositoryBundle, CommService commService, ApplicationContext applicationContext) {
        super(repositoryBundle, commService, applicationContext);
    }

    @Override
    public Question execute(Question question, WebSocketContext webSocketContext, List<String> tokens, Input input, Output output) {
        MudProperty mudHour = getRepositoryBundle()
            .getPropertyRepository()
            .findById(PROPERTY_HOUR)
            .orElseThrow();

        int hour = Integer.parseInt(mudHour.getValue());
        String timeOfDay;

        if (hour < 6) {
            timeOfDay = "at night";
        } else if (hour <= 12) {
            timeOfDay = "in the morning";
        } else if (hour < 18) {
            timeOfDay = "in the afternoon";
            hour -= 12;
        } else {
            timeOfDay = "in the evening";
            hour -= 12;
        }

        switch (hour) {
            case 0 -> output.append("[dblue]It is midnight.");
            case 12 -> output.append("[yellow]It is noon.");
            default -> output.append("[default]The hour is %d o'clock %s.", hour, timeOfDay);
        }

        return question;
    }
}
