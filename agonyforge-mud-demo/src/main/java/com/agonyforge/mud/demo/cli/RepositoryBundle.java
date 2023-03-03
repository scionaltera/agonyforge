package com.agonyforge.mud.demo.cli;

import com.agonyforge.mud.models.dynamodb.repository.MudCharacterRepository;
import com.agonyforge.mud.models.dynamodb.repository.MudItemRepository;
import com.agonyforge.mud.models.dynamodb.repository.MudRoomRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

@Component
public class RepositoryBundle {
    private final MudCharacterRepository characterRepository;
    private final MudItemRepository itemRepository;
    private final MudRoomRepository roomRepository;

    @Autowired
    public RepositoryBundle(MudCharacterRepository characterRepository,
                            MudItemRepository itemRepository,
                            MudRoomRepository roomRepository) {
        this.characterRepository = characterRepository;
        this.itemRepository = itemRepository;
        this.roomRepository = roomRepository;
    }

    public MudCharacterRepository getCharacterRepository() {
        return characterRepository;
    }

    public MudItemRepository getItemRepository() {
        return itemRepository;
    }

    public MudRoomRepository getRoomRepository() {
        return roomRepository;
    }
}
