package com.agonyforge.mud.demo.config;

import com.agonyforge.mud.demo.cli.command.MoveCommand;
import com.agonyforge.mud.demo.cli.RepositoryBundle;
import com.agonyforge.mud.demo.model.constant.Direction;
import com.agonyforge.mud.demo.service.CommService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.ApplicationContext;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

@Configuration
public class MoveConfiguration {
    private final RepositoryBundle repositoryBundle;
    private final CommService commService;
    private final ApplicationContext applicationContext;

    @Autowired
    public MoveConfiguration(RepositoryBundle repositoryBundle,
                             CommService commService,
                             ApplicationContext applicationContext) {
        this.repositoryBundle = repositoryBundle;
        this.commService = commService;
        this.applicationContext = applicationContext;
    }

    @Bean
    public MoveCommand northCommand() {
        return new MoveCommand(
            repositoryBundle,
            commService,
            applicationContext,
            Direction.NORTH);
    }

    @Bean
    public MoveCommand eastCommand() {
        return new MoveCommand(
            repositoryBundle,
            commService,
            applicationContext,
            Direction.EAST);
    }

    @Bean
    public MoveCommand southCommand() {
        return new MoveCommand(
            repositoryBundle,
            commService,
            applicationContext,
            Direction.SOUTH);
    }

    @Bean
    public MoveCommand westCommand() {
        return new MoveCommand(
            repositoryBundle,
            commService,
            applicationContext,
            Direction.WEST);
    }

    @Bean
    public MoveCommand upCommand() {
        return new MoveCommand(
            repositoryBundle,
            commService,
            applicationContext,
            Direction.UP);
    }

    @Bean
    public MoveCommand downCommand() {
        return new MoveCommand(
            repositoryBundle,
            commService,
            applicationContext,
            Direction.DOWN);
    }
}
