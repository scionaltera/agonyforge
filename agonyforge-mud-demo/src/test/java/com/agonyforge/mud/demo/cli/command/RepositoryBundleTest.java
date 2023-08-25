package com.agonyforge.mud.demo.cli.command;

import com.agonyforge.mud.demo.cli.RepositoryBundle;
import com.agonyforge.mud.demo.model.repository.MudCharacterRepository;
import com.agonyforge.mud.demo.model.repository.MudItemRepository;
import com.agonyforge.mud.demo.model.repository.MudPropertyRepository;
import com.agonyforge.mud.demo.model.repository.MudRoomRepository;
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

    @Test
    void testGetters() {
        RepositoryBundle uut = new RepositoryBundle(
            propertyRepository,
            characterRepository,
            itemRepository,
            roomRepository);

        assertEquals(propertyRepository, uut.getPropertyRepository());
        assertEquals(characterRepository, uut.getCharacterRepository());
        assertEquals(itemRepository, uut.getItemRepository());
        assertEquals(roomRepository, uut.getRoomRepository());
    }
}
