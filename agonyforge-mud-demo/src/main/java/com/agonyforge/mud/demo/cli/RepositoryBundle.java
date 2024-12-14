package com.agonyforge.mud.demo.cli;

import com.agonyforge.mud.demo.model.repository.*;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

@Component
public class RepositoryBundle {
    private final MudPropertyRepository propertyRepository;
    private final MudCharacterRepository characterRepository;
    private final MudCharacterPrototypeRepository characterPrototypeRepository;
    private final MudItemRepository itemRepository;
    private final MudRoomRepository roomRepository;
    private final MudSpeciesRepository speciesRepository;
    private final MudProfessionRepository professionRepository;

    @Autowired
    public RepositoryBundle(MudPropertyRepository propertyRepository,
                            MudCharacterRepository characterRepository,
                            MudCharacterPrototypeRepository characterPrototypeRepository,
                            MudItemRepository itemRepository,
                            MudRoomRepository roomRepository,
                            MudSpeciesRepository speciesRepository,
                            MudProfessionRepository professionRepository) {
        this.propertyRepository = propertyRepository;
        this.characterRepository = characterRepository;
        this.characterPrototypeRepository = characterPrototypeRepository;
        this.itemRepository = itemRepository;
        this.roomRepository = roomRepository;
        this.speciesRepository = speciesRepository;
        this.professionRepository = professionRepository;
    }

    public MudPropertyRepository getPropertyRepository() {
        return propertyRepository;
    }

    public MudCharacterRepository getCharacterRepository() {
        return characterRepository;
    }

    public MudCharacterPrototypeRepository getCharacterPrototypeRepository() {
        return characterPrototypeRepository;
    }

    public MudItemRepository getItemRepository() {
        return itemRepository;
    }

    public MudRoomRepository getRoomRepository() {
        return roomRepository;
    }

    public MudSpeciesRepository getSpeciesRepository() {
        return speciesRepository;
    }

    public MudProfessionRepository getProfessionRepository() {
        return professionRepository;
    }
}
