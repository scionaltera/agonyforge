package com.agonyforge.mud.demo.config;

import com.agonyforge.mud.core.cli.Question;
import com.agonyforge.mud.demo.cli.MenuQuestion;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

@Configuration
public class MudConfiguration {
    private final Question initialQuestion;

    @Autowired
    public MudConfiguration(@Qualifier("menuQuestion") MenuQuestion initialQuestion) {
        initialQuestion.setNextQuestion("echoQuestion");
        this.initialQuestion = initialQuestion;
    }

    @Bean(name = "initialQuestion")
    public Question initialQuestion() {
        return initialQuestion;
    }
}
