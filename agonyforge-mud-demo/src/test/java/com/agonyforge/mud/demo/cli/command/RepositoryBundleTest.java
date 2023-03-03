package com.agonyforge.mud.demo.cli.command;

import com.agonyforge.mud.models.dynamodb.repository.MudCharacterRepository;
import com.agonyforge.mud.models.dynamodb.repository.MudItemRepository;
import com.agonyforge.mud.models.dynamodb.repository.MudRoomRepository;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import static org.junit.jupiter.api.Assertions.assertEquals;

@ExtendWith(MockitoExtension.class)
public class RepositoryBundleTest {
    @Mock
    private MudCharacterRepository characterRepository;

    @Mock
    private MudItemRepository itemRepository;

    @Mock
    private MudRoomRepository roomRepository;

    @Test
    void testGetters() {
        RepositoryBundle uut = new RepositoryBundle(
            characterRepository,
            itemRepository,
            roomRepository);

        assertEquals(characterRepository, uut.getCharacterRepository());
        assertEquals(itemRepository, uut.getItemRepository());
        assertEquals(roomRepository, uut.getRoomRepository());
    }
}
