package com.agonyforge.mud.demo.cli.command;

import com.agonyforge.mud.core.cli.Question;
import com.agonyforge.mud.core.web.model.Input;
import com.agonyforge.mud.core.web.model.Output;
import com.agonyforge.mud.core.web.model.WebSocketContext;
import com.agonyforge.mud.models.dynamodb.impl.MudCharacter;
import com.agonyforge.mud.models.dynamodb.repository.MudCharacterRepository;
import com.agonyforge.mud.models.dynamodb.repository.MudItemRepository;
import com.agonyforge.mud.models.dynamodb.repository.MudRoomRepository;
import com.agonyforge.mud.models.dynamodb.service.CommService;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import java.util.List;

import static com.agonyforge.mud.models.dynamodb.impl.Constants.TYPE_PC;
import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertTrue;
import static org.mockito.ArgumentMatchers.eq;
import static org.mockito.Mockito.when;

@ExtendWith(MockitoExtension.class)
public class WhoCommandTest {
    @Mock
    private RepositoryBundle repositoryBundle;

    @Mock
    private MudCharacterRepository characterRepository;

    @Mock
    private MudItemRepository itemRepository;

    @Mock
    private MudRoomRepository roomRepository;

    @Mock
    private CommService commService;

    @Mock
    private MudCharacter ch;

    @Mock
    private MudCharacter other;

    @Mock
    private MudCharacter chInstance;

    @Mock
    private MudCharacter otherInstance;

    @Mock
    private Question question;

    @Mock
    private WebSocketContext webSocketContext;

    @BeforeEach
    void setUp() {
        when(repositoryBundle.getCharacterRepository()).thenReturn(characterRepository);
        when(repositoryBundle.getItemRepository()).thenReturn(itemRepository);
        when(repositoryBundle.getRoomRepository()).thenReturn(roomRepository);
    }

    @Test
    void testExecuteOnePlayer() {
        List<MudCharacter> characters = List.of(ch, chInstance);
        Output output = new Output();

        when(ch.isPrototype()).thenReturn(true);
        when(chInstance.isPrototype()).thenReturn(false);
        when(chInstance.getName()).thenReturn("Scion");
        when(characterRepository.getByType(eq(TYPE_PC))).thenReturn(characters);

        WhoCommand uut = new WhoCommand(repositoryBundle, commService);
        Question result = uut.execute(question, webSocketContext, List.of("WHO"), new Input("who"), output);

        assertEquals(question, result);

        assertTrue(output.getOutput().get(0).contains("Who is Playing"));
        assertTrue(output.getOutput().get(2).contains("Scion"));
        assertTrue(output.getOutput().get(4).contains("1 player online"));
    }

    @Test
    void testExecuteTwoPlayer() {
        List<MudCharacter> characters = List.of(ch, chInstance, other, otherInstance);
        Output output = new Output();

        when(ch.isPrototype()).thenReturn(true);
        when(chInstance.isPrototype()).thenReturn(false);
        when(chInstance.getName()).thenReturn("Scion");
        when(other.isPrototype()).thenReturn(true);
        when(otherInstance.isPrototype()).thenReturn(false);
        when(otherInstance.getName()).thenReturn("Spook");
        when(characterRepository.getByType(eq(TYPE_PC))).thenReturn(characters);

        WhoCommand uut = new WhoCommand(repositoryBundle, commService);
        Question result = uut.execute(question, webSocketContext, List.of("WHO"), new Input("who"), output);

        assertEquals(question, result);

        assertTrue(output.getOutput().get(0).contains("Who is Playing"));
        assertTrue(output.getOutput().get(2).contains("Scion"));
        assertTrue(output.getOutput().get(3).contains("Spook"));
        assertTrue(output.getOutput().get(5).contains("2 players online"));
    }
}
