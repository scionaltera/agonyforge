package com.agonyforge.mud.demo.config;

import com.agonyforge.mud.demo.cli.command.MoveCommand;
import com.agonyforge.mud.models.dynamodb.constant.Direction;
import com.agonyforge.mud.models.dynamodb.repository.MudCharacterRepository;
import com.agonyforge.mud.models.dynamodb.repository.MudItemRepository;
import com.agonyforge.mud.models.dynamodb.repository.MudRoomRepository;
import com.agonyforge.mud.models.dynamodb.service.CommService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

@Configuration
public class MoveConfiguration {
    private final MudCharacterRepository characterRepository;
    private final MudItemRepository itemRepository;
    private final MudRoomRepository roomRepository;
    private final CommService commService;

    @Autowired
    public MoveConfiguration(MudCharacterRepository characterRepository,
                             MudItemRepository itemRepository,
                             MudRoomRepository roomRepository,
                             CommService commService) {
        this.characterRepository = characterRepository;
        this.itemRepository = itemRepository;
        this.roomRepository = roomRepository;
        this.commService = commService;
    }

    @Bean
    public MoveCommand northCommand() {
        return new MoveCommand(
            characterRepository,
            itemRepository,
            roomRepository,
            commService,
            Direction.NORTH);
    }

    @Bean
    public MoveCommand eastCommand() {
        return new MoveCommand(
            characterRepository,
            itemRepository,
            roomRepository,
            commService,
            Direction.EAST);
    }

    @Bean
    public MoveCommand southCommand() {
        return new MoveCommand(
            characterRepository,
            itemRepository,
            roomRepository,
            commService,
            Direction.SOUTH);
    }

    @Bean
    public MoveCommand westCommand() {
        return new MoveCommand(
            characterRepository,
            itemRepository,
            roomRepository,
            commService,
            Direction.WEST);
    }

    @Bean
    public MoveCommand upCommand() {
        return new MoveCommand(
            characterRepository,
            itemRepository,
            roomRepository,
            commService,
            Direction.UP);
    }

    @Bean
    public MoveCommand downCommand() {
        return new MoveCommand(
            characterRepository,
            itemRepository,
            roomRepository,
            commService,
            Direction.DOWN);
    }
}
