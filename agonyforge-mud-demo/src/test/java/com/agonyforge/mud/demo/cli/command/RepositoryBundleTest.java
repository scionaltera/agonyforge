package com.agonyforge.mud.demo.cli.command;

import com.agonyforge.mud.demo.cli.RepositoryBundle;
import com.agonyforge.mud.demo.model.repository.MudCharacterRepository;
import com.agonyforge.mud.demo.model.repository.MudItemRepository;
import com.agonyforge.mud.demo.model.repository.MudProfessionRepository;
import com.agonyforge.mud.demo.model.repository.MudPropertyRepository;
import com.agonyforge.mud.demo.model.repository.MudRoomRepository;
import com.agonyforge.mud.demo.model.repository.MudSpeciesRepository;
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
    private MudItemRepository itemRepository;

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
            itemRepository,
            roomRepository,
            speciesRepository,
            professionRepository);

        assertEquals(propertyRepository, uut.getPropertyRepository());
        assertEquals(characterRepository, uut.getCharacterRepository());
        assertEquals(itemRepository, uut.getItemRepository());
        assertEquals(roomRepository, uut.getRoomRepository());
        assertEquals(speciesRepository, uut.getSpeciesRepository());
        assertEquals(professionRepository, uut.getProfessionRepository());
    }
}
