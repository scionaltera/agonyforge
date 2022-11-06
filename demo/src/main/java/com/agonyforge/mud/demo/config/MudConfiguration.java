package com.agonyforge.mud.demo.config;

import com.agonyforge.mud.core.cli.Question;
import com.agonyforge.mud.demo.cli.EchoQuestion;
import com.agonyforge.mud.demo.cli.MenuQuestion;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.session.FindByIndexNameSessionRepository;
import org.springframework.session.Session;

@Configuration
public class MudConfiguration {
    private final FindByIndexNameSessionRepository<Session> sessionRepository;

    @Autowired
    public MudConfiguration(FindByIndexNameSessionRepository<Session> sessionRepository) {
        this.sessionRepository = sessionRepository;
    }

    @Bean(name = "initialQuestion")
    public Question initialQuestion() {
        MenuQuestion menuQuestion = new MenuQuestion(sessionRepository);
        EchoQuestion echoQuestion = new EchoQuestion();

        menuQuestion.setNextQuestion(echoQuestion);

        return menuQuestion;
    }
}
