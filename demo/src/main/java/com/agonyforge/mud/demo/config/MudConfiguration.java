package com.agonyforge.mud.demo.config;

import com.agonyforge.mud.core.cli.Question;
import com.agonyforge.mud.core.service.EchoService;
import com.agonyforge.mud.demo.cli.EchoQuestion;
import com.agonyforge.mud.demo.cli.MenuQuestion;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

@Configuration
public class MudConfiguration {
    private final EchoService echoService;

    @Autowired
    public MudConfiguration(EchoService echoService) {
        this.echoService = echoService;
    }

    @Bean(name = "initialQuestion")
    public Question initialQuestion() {
        MenuQuestion menuQuestion = new MenuQuestion();
        EchoQuestion echoQuestion = new EchoQuestion(echoService);

        menuQuestion.setNextQuestion(echoQuestion);

        return menuQuestion;
    }
}
