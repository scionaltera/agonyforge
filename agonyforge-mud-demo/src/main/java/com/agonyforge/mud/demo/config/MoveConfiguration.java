package com.agonyforge.mud.demo.config;

import com.agonyforge.mud.demo.cli.command.MoveCommand;
import com.agonyforge.mud.demo.cli.command.RepositoryBundle;
import com.agonyforge.mud.models.dynamodb.constant.Direction;
import com.agonyforge.mud.models.dynamodb.service.CommService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

@Configuration
public class MoveConfiguration {
    private final RepositoryBundle repositoryBundle;
    private final CommService commService;

    @Autowired
    public MoveConfiguration(RepositoryBundle repositoryBundle,
                             CommService commService) {
        this.repositoryBundle = repositoryBundle;
        this.commService = commService;
    }

    @Bean
    public MoveCommand northCommand() {
        return new MoveCommand(
            repositoryBundle,
            commService,
            Direction.NORTH);
    }

    @Bean
    public MoveCommand eastCommand() {
        return new MoveCommand(
            repositoryBundle,
            commService,
            Direction.EAST);
    }

    @Bean
    public MoveCommand southCommand() {
        return new MoveCommand(
            repositoryBundle,
            commService,
            Direction.SOUTH);
    }

    @Bean
    public MoveCommand westCommand() {
        return new MoveCommand(
            repositoryBundle,
            commService,
            Direction.WEST);
    }

    @Bean
    public MoveCommand upCommand() {
        return new MoveCommand(
            repositoryBundle,
            commService,
            Direction.UP);
    }

    @Bean
    public MoveCommand downCommand() {
        return new MoveCommand(
            repositoryBundle,
            commService,
            Direction.DOWN);
    }
}
