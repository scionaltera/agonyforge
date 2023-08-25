package com.agonyforge.mud.demo.cli;

import com.agonyforge.mud.demo.model.repository.MudCharacterRepository;
import com.agonyforge.mud.demo.model.repository.MudItemRepository;
import com.agonyforge.mud.demo.model.repository.MudPropertyRepository;
import com.agonyforge.mud.demo.model.repository.MudRoomRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

@Component
public class RepositoryBundle {
    private final MudPropertyRepository propertyRepository;
    private final MudCharacterRepository characterRepository;
    private final MudItemRepository itemRepository;
    private final MudRoomRepository roomRepository;

    @Autowired
    public RepositoryBundle(MudPropertyRepository propertyRepository,
                            MudCharacterRepository characterRepository,
                            MudItemRepository itemRepository,
                            MudRoomRepository roomRepository) {
        this.propertyRepository = propertyRepository;
        this.characterRepository = characterRepository;
        this.itemRepository = itemRepository;
        this.roomRepository = roomRepository;
    }

    public MudPropertyRepository getPropertyRepository() {
        return propertyRepository;
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
