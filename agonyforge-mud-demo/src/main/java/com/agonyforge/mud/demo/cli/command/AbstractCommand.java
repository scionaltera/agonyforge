package com.agonyforge.mud.demo.cli.command;

import com.agonyforge.mud.models.dynamodb.repository.MudCharacterRepository;
import com.agonyforge.mud.models.dynamodb.repository.MudItemRepository;
import com.agonyforge.mud.models.dynamodb.repository.MudRoomRepository;
import com.agonyforge.mud.models.dynamodb.service.CommService;

public abstract class AbstractCommand implements Command {
    protected final MudCharacterRepository characterRepository;
    protected final MudItemRepository itemRepository;
    protected final MudRoomRepository roomRepository;
    protected final CommService commService;

    public AbstractCommand(MudCharacterRepository characterRepository,
                           MudItemRepository itemRepository,
                           MudRoomRepository roomRepository,
                           CommService commService) {
        this.characterRepository = characterRepository;
        this.itemRepository = itemRepository;
        this.roomRepository = roomRepository;
        this.commService = commService;
    }
}
