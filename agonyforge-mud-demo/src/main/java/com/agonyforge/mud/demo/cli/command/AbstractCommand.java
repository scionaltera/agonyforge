package com.agonyforge.mud.demo.cli.command;

import com.agonyforge.mud.core.cli.Question;
import com.agonyforge.mud.core.web.model.Input;
import com.agonyforge.mud.core.web.model.Output;
import com.agonyforge.mud.core.web.model.WebSocketContext;
import com.agonyforge.mud.models.dynamodb.repository.MudCharacterRepository;
import com.agonyforge.mud.models.dynamodb.repository.MudItemRepository;
import com.agonyforge.mud.models.dynamodb.repository.MudRoomRepository;
import com.agonyforge.mud.models.dynamodb.service.CommService;

import java.util.List;

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

    @Override
    public Question execute(Question question, WebSocketContext webSocketContext, List<String> tokens, Input input, Output output) {
        return question;
    }
}
