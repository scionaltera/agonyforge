package com.agonyforge.mud.demo.cli.command;

import com.agonyforge.mud.core.cli.Question;
import com.agonyforge.mud.core.web.model.Input;
import com.agonyforge.mud.core.web.model.Output;
import com.agonyforge.mud.core.web.model.WebSocketContext;
import com.agonyforge.mud.demo.cli.RepositoryBundle;
import com.agonyforge.mud.demo.model.impl.CharacterComponent;
import com.agonyforge.mud.demo.model.impl.LocationComponent;
import com.agonyforge.mud.demo.model.impl.MudCharacter;
import com.agonyforge.mud.demo.model.impl.MudCharacterTemplate;
import com.agonyforge.mud.demo.model.repository.MudCharacterRepository;
import com.agonyforge.mud.demo.model.repository.MudItemRepository;
import com.agonyforge.mud.demo.model.repository.MudRoomRepository;
import com.agonyforge.mud.demo.service.CommService;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.context.ApplicationContext;

import java.util.List;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertTrue;
import static org.mockito.Mockito.lenient;
import static org.mockito.Mockito.when;

@ExtendWith(MockitoExtension.class)
public class WhoCommandTest {
    @Mock
    private ApplicationContext applicationContext;

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
    private MudCharacterTemplate chProto, oProto;

    @Mock
    private MudCharacter ch;

    @Mock
    private MudCharacter other;

    @Mock
    private CharacterComponent chCharacterComponent, otherCharacterComponent;

    @Mock
    private LocationComponent chLocationComponent, otherLocationComponent;

    @Mock
    private Question question;

    @Mock
    private WebSocketContext webSocketContext;

    @BeforeEach
    void setUp() {
        lenient().when(repositoryBundle.getCharacterRepository()).thenReturn(characterRepository);
        lenient().when(repositoryBundle.getItemRepository()).thenReturn(itemRepository);
        lenient().when(repositoryBundle.getRoomRepository()).thenReturn(roomRepository);
    }

    @Test
    void testExecuteOnePlayer() {
        List<MudCharacter> characters = List.of(ch);
        Output output = new Output();

        when(chCharacterComponent.getName()).thenReturn("Scion");
        when(ch.getTemplate()).thenReturn(chProto);
        when(ch.getCharacter()).thenReturn(chCharacterComponent);
        when(ch.getLocation()).thenReturn(chLocationComponent);
        when(characterRepository.findAll()).thenReturn(characters);

        WhoCommand uut = new WhoCommand(repositoryBundle, commService, applicationContext);
        Question result = uut.execute(question, webSocketContext, List.of("WHO"), new Input("who"), output);

        assertEquals(question, result);

        assertTrue(output.getOutput().get(0).contains("Who is Playing"));
        assertTrue(output.getOutput().get(2).contains("Scion"));
        assertTrue(output.getOutput().get(4).contains("1 player online"));
    }

    @Test
    void testExecuteTwoPlayer() {
        List<MudCharacter> characters = List.of(ch, other);
        Output output = new Output();

        when(chCharacterComponent.getName()).thenReturn("Scion");
        when(otherCharacterComponent.getName()).thenReturn("Spook");
        when(ch.getTemplate()).thenReturn(chProto);
        when(ch.getCharacter()).thenReturn(chCharacterComponent);
        when(ch.getLocation()).thenReturn(chLocationComponent);
        when(other.getTemplate()).thenReturn(oProto);
        when(other.getCharacter()).thenReturn(otherCharacterComponent);
        when(other.getLocation()).thenReturn(otherLocationComponent);
        when(characterRepository.findAll()).thenReturn(characters);

        WhoCommand uut = new WhoCommand(repositoryBundle, commService, applicationContext);
        Question result = uut.execute(question, webSocketContext, List.of("WHO"), new Input("who"), output);

        assertEquals(question, result);

        assertTrue(output.getOutput().get(0).contains("Who is Playing"));
        assertTrue(output.getOutput().get(2).contains("Scion"));
        assertTrue(output.getOutput().get(3).contains("Spook"));
        assertTrue(output.getOutput().get(5).contains("2 players online"));
    }
}
