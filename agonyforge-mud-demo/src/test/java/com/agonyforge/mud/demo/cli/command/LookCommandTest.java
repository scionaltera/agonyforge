package com.agonyforge.mud.demo.cli.command;

import com.agonyforge.mud.core.cli.Question;
import com.agonyforge.mud.core.service.SessionAttributeService;
import com.agonyforge.mud.core.web.model.Input;
import com.agonyforge.mud.core.web.model.Output;
import com.agonyforge.mud.core.web.model.WebSocketContext;
import com.agonyforge.mud.demo.cli.RepositoryBundle;
import com.agonyforge.mud.demo.model.impl.MudCharacter;
import com.agonyforge.mud.demo.model.impl.MudItem;
import com.agonyforge.mud.demo.model.impl.MudRoom;
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
import java.util.Map;
import java.util.Optional;
import java.util.UUID;

import static com.agonyforge.mud.core.config.SessionConfiguration.MUD_CHARACTER;
import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertTrue;
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
    private MudCharacter ch;

    @Mock
    private MudCharacter target;

    @Mock
    private MudItem item;

    @Mock
    private MudRoom room;

    @Mock
    private WebSocketContext webSocketContext;

    @Mock
    private Question question;

    @BeforeEach
    void setUp() {
        lenient().when(repositoryBundle.getCharacterRepository()).thenReturn(characterRepository);
        lenient().when(repositoryBundle.getItemRepository()).thenReturn(itemRepository);
        lenient().when(repositoryBundle.getRoomRepository()).thenReturn(roomRepository);
    }

    @Test
    void testExecuteNoRoom() {
        UUID chId = UUID.randomUUID();
        long roomId = 100L;

        when(ch.getRoomId()).thenReturn(roomId);
        when(characterRepository.getById(eq(chId), eq(false))).thenReturn(Optional.of(ch));
        when(webSocketContext.getAttributes()).thenReturn(Map.of(
            MUD_CHARACTER, chId
        ));

        Output output = new Output();
        LookCommand uut = new LookCommand(repositoryBundle, commService, applicationContext, sessionAttributeService);
        Question result = uut.execute(question,
            webSocketContext,
            List.of("LOOK"),
            new Input("look"),
            output);

        assertEquals(question, result);
        assertEquals(1, output.getOutput().size());
        assertTrue(output.getOutput().get(0).contains("floating in the void"));
    }

    @Test
    void testExecute() {
        UUID chId = UUID.randomUUID();
        String wsSessionId = UUID.randomUUID().toString();
        long roomId = 100L;

        when(ch.getRoomId()).thenReturn(roomId);
        when(target.getName()).thenReturn("Target");
        when(target.getWebSocketSession()).thenReturn(wsSessionId);
        when(item.getLongDescription()).thenReturn("A test is zipping wildly around the room.");
        when(room.getId()).thenReturn(roomId);
        when(room.getName()).thenReturn("Test Room");
        when(room.getDescription()).thenReturn("This room is a test.");
        when(characterRepository.getById(eq(chId), eq(false))).thenReturn(Optional.of(ch));
        when(characterRepository.getByRoom(eq(roomId))).thenReturn(List.of(ch, target));
        when(itemRepository.getByRoom(eq(roomId))).thenReturn(List.of(item));
        when(roomRepository.getById(eq(roomId))).thenReturn(Optional.of(room));
        when(webSocketContext.getAttributes()).thenReturn(Map.of(MUD_CHARACTER, chId));
        when(sessionAttributeService.getSessionAttributes(wsSessionId)).thenReturn(Map.of("MUD.QUESTION", "commandQuestion"));

        Output output = new Output();
        LookCommand uut = new LookCommand(repositoryBundle, commService, applicationContext, sessionAttributeService);
        Question result = uut.execute(question,
            webSocketContext,
            List.of("LOOK"),
            new Input("look"),
            output);

        assertEquals(question, result);
        assertEquals(5, output.getOutput().size());
        assertTrue(output.getOutput().get(0).contains("(100) Test Room"));
        assertTrue(output.getOutput().get(1).contains("This room is a test."));
        assertTrue(output.getOutput().get(2).contains("Exits:"));
        assertTrue(output.getOutput().get(3).contains("Target is here."));
        assertTrue(output.getOutput().get(4).contains("A test is"));
    }
}
