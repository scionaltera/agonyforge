package com.agonyforge.mud.demo.cli.command;

import com.agonyforge.mud.core.cli.Question;
import com.agonyforge.mud.core.service.SessionAttributeService;
import com.agonyforge.mud.core.web.model.Output;
import com.agonyforge.mud.core.web.model.WebSocketContext;
import com.agonyforge.mud.demo.cli.RepositoryBundle;
import com.agonyforge.mud.demo.cli.question.CommandException;
import com.agonyforge.mud.demo.model.impl.*;
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

import java.util.*;

import static com.agonyforge.mud.core.config.SessionConfiguration.MUD_CHARACTER;
import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.ArgumentMatchers.eq;
import static org.mockito.Mockito.lenient;
import static org.mockito.Mockito.when;

@ExtendWith(MockitoExtension.class)
public class LookCommandTest {
    @Mock
    private RepositoryBundle repositoryBundle;

    @Mock
    private ApplicationContext applicationContext;

    @Mock
    private MudCharacterRepository characterRepository;

    @Mock
    private MudItemRepository itemRepository;

    @Mock
    private MudRoomRepository roomRepository;

    @Mock
    private CommService commService;

    @Mock
    private SessionAttributeService sessionAttributeService;

    @Mock
    private PlayerComponent playerComponent;

    @Mock
    private MudCharacter ch;

    @Mock
    private MudCharacter target;

    @Mock
    private CharacterComponent targetCharacterComponent;

    @Mock
    private MudItem item;

    @Mock
    private ItemComponent itemComponent;

    @Mock
    private LocationComponent chLocationComponent;

    @Mock
    private MudRoom room;

    @Mock
    private WebSocketContext webSocketContext;

    @Mock
    private Question question;

    private final Random random = new Random();

    @BeforeEach
    void setUp() {
        lenient().when(repositoryBundle.getCharacterRepository()).thenReturn(characterRepository);
        lenient().when(repositoryBundle.getItemRepository()).thenReturn(itemRepository);
        lenient().when(repositoryBundle.getRoomRepository()).thenReturn(roomRepository);
    }

    @Test
    void testExecuteNoRoom() {
        Long chId = random.nextLong();

        when(ch.getLocation()).thenReturn(chLocationComponent);
        when(ch.getLocation().getRoom()).thenReturn(null);
        when(characterRepository.findById(eq(chId))).thenReturn(Optional.of(ch));
        when(webSocketContext.getAttributes()).thenReturn(Map.of(
            MUD_CHARACTER, chId
        ));

        Output output = new Output();
        LookCommand uut = new LookCommand(repositoryBundle, commService, applicationContext, sessionAttributeService);

        try {
            Question result = uut.execute(question,
                webSocketContext,
                List.of("look"),
                output);

            assertEquals(question, result);
            assertEquals(1, output.getOutput().size());
            assertTrue(output.getOutput().get(0).contains("floating aimlessly in the void"));
        } catch (CommandException e) {
            return;
        }

        fail();
    }

    @Test
    void testExecute() {
        Long chId = random.nextLong();
        String wsSessionId = UUID.randomUUID().toString();

        when(ch.getLocation()).thenReturn(chLocationComponent);
        when(ch.getLocation().getRoom()).thenReturn(room);
        when(playerComponent.getWebSocketSession()).thenReturn(wsSessionId);
        when(target.getPlayer()).thenReturn(playerComponent);
        when(target.getCharacter()).thenReturn(targetCharacterComponent);
        when(target.getCharacter().getName()).thenReturn("Target");
        when(item.getItem()).thenReturn(itemComponent);
        when(itemComponent.getLongDescription()).thenReturn("A test is zipping wildly around the room.");
        when(room.getName()).thenReturn("Test Room");
        when(room.getDescription()).thenReturn("This room is a test.");
        when(characterRepository.findById(eq(chId))).thenReturn(Optional.of(ch));
        when(characterRepository.findByLocationRoom(eq(room))).thenReturn(List.of(ch, target));
        when(itemRepository.findByLocationRoom(eq(room))).thenReturn(List.of(item));
        when(webSocketContext.getAttributes()).thenReturn(Map.of(MUD_CHARACTER, chId));
        when(sessionAttributeService.getSessionAttributes(wsSessionId)).thenReturn(Map.of("MUD.QUESTION", "commandQuestion"));

        Output output = new Output();
        LookCommand uut = new LookCommand(repositoryBundle, commService, applicationContext, sessionAttributeService);
        Question result = uut.execute(question,
            webSocketContext,
            List.of("look"),
            output);

        assertEquals(question, result);
        assertTrue(output.getOutput().get(0).contains("Test Room"));
        assertTrue(output.getOutput().get(1).contains("This room is a test."));
        assertTrue(output.getOutput().get(2).contains("Exits:"));
        assertTrue(output.getOutput().get(3).contains("Target is here."));
        assertTrue(output.getOutput().get(4).contains("A test is"));
    }
}
