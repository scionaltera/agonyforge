package com.agonyforge.mud.demo.cli;

import com.agonyforge.mud.demo.model.repository.*;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import static org.junit.jupiter.api.Assertions.assertEquals;

@ExtendWith(MockitoExtension.class)
public class RepositoryBundleTest {
    @Mock
    private MudPropertyRepository propertyRepository;

    @Mock
    private MudCharacterRepository characterRepository;

    @Mock
    private MudCharacterPrototypeRepository characterPrototypeRepository;

    @Mock
    private MudItemRepository itemRepository;

    @Mock
    private MudItemPrototypeRepository itemPrototypeRepository;

    @Mock
    private MudRoomRepository roomRepository;

    @Mock
    private MudSpeciesRepository speciesRepository;

    @Mock
    private MudProfessionRepository professionRepository;

    @Test
    void testGetters() {
        RepositoryBundle uut = new RepositoryBundle(
            propertyRepository,
            characterRepository,
            characterPrototypeRepository,
            itemRepository,
            itemPrototypeRepository,
            roomRepository,
            speciesRepository,
            professionRepository);

        assertEquals(propertyRepository, uut.getPropertyRepository());
        assertEquals(characterRepository, uut.getCharacterRepository());
        assertEquals(characterPrototypeRepository, uut.getCharacterPrototypeRepository());
        assertEquals(itemRepository, uut.getItemRepository());
        assertEquals(itemPrototypeRepository, uut.getItemPrototypeRepository());
        assertEquals(roomRepository, uut.getRoomRepository());
        assertEquals(speciesRepository, uut.getSpeciesRepository());
        assertEquals(professionRepository, uut.getProfessionRepository());
    }
}
