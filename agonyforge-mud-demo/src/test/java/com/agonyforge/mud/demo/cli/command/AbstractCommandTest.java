package com.agonyforge.mud.demo.cli.command;

import com.agonyforge.mud.core.cli.Question;
import com.agonyforge.mud.core.web.model.Input;
import com.agonyforge.mud.core.web.model.Output;
import com.agonyforge.mud.core.web.model.WebSocketContext;
import com.agonyforge.mud.demo.cli.question.CommandException;
import com.agonyforge.mud.demo.cli.RepositoryBundle;
import com.agonyforge.mud.demo.model.impl.MudCharacter;
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
import static com.agonyforge.mud.demo.model.impl.ModelConstants.TYPE_PC;
import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertFalse;
import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.junit.jupiter.api.Assertions.assertTrue;
import static org.mockito.ArgumentMatchers.eq;
import static org.mockito.Mockito.lenient;
import static org.mockito.Mockito.when;

@ExtendWith(MockitoExtension.class)
public class AbstractCommandTest {
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
    private WebSocketContext webSocketContext;

    @Mock
    private MudCharacter ch;

    @Mock
    private MudCharacter target;

    @Mock
    private MudCharacter proto;

    @Mock
    private Question question;

    @BeforeEach
    void setUp() {
        lenient().when(repositoryBundle.getCharacterRepository()).thenReturn(characterRepository);
        lenient().when(repositoryBundle.getItemRepository()).thenReturn(itemRepository);
        lenient().when(repositoryBundle.getRoomRepository()).thenReturn(roomRepository);
    }

    @Test
    void testGetCharacterNotFound() {
        UUID chId = UUID.randomUUID();

        when(characterRepository.getById(eq(chId), eq(false))).thenReturn(Optional.empty());
        when(webSocketContext.getAttributes()).thenReturn(Map.of(
            MUD_CHARACTER, chId
        ));

        Output output = new Output();
        Command uut = new AbstractCommand(repositoryBundle, commService, applicationContext) {
            @Override
            public Question execute(Question question, WebSocketContext webSocketContext, List<String> tokens, Input input, Output output) {
                getCurrentCharacter(webSocketContext, output);
                return question;
            }
        };

        assertThrows(CommandException.class, () -> uut.execute(
            question,
            webSocketContext,
            List.of("TEST"),
            new Input("test"),
            output));
    }

    @Test
    void testGetCharacterInVoid() {
        UUID chId = UUID.randomUUID();

        when(ch.getRoomId()).thenReturn(null);
        when(characterRepository.getById(eq(chId), eq(false))).thenReturn(Optional.of(ch));
        when(webSocketContext.getAttributes()).thenReturn(Map.of(
            MUD_CHARACTER, chId
        ));

        Output output = new Output();
        Command uut = new AbstractCommand(repositoryBundle, commService, applicationContext) {
            @Override
            public Question execute(Question question, WebSocketContext webSocketContext, List<String> tokens, Input input, Output output) {
                getCurrentCharacter(webSocketContext, output);
                return question;
            }
        };

        assertThrows(CommandException.class, () -> uut.execute(
            question,
            webSocketContext,
            List.of("TEST"),
            new Input("test"),
            output));
    }

    @Test
    void testGetCharacterValid() {
        UUID chId = UUID.randomUUID();

        when(ch.getRoomId()).thenReturn(100L);
        when(characterRepository.getById(eq(chId), eq(false))).thenReturn(Optional.of(ch));
        when(webSocketContext.getAttributes()).thenReturn(Map.of(
            MUD_CHARACTER, chId
        ));

        Output output = new Output();
        Command uut = new AbstractCommand(repositoryBundle, commService, applicationContext) {
            @Override
            public Question execute(Question question, WebSocketContext webSocketContext, List<String> tokens, Input input, Output output) {
                getCurrentCharacter(webSocketContext, output);
                return question;
            }
        };

        assertEquals(question, uut.execute(
            question,
            webSocketContext,
            List.of("TEST"),
            new Input("test"),
            output));
    }

    @Test
    void testFindRoomCharacter() {
        Long roomId = 100L;

        lenient().when(ch.getName()).thenReturn("Scion");
        when(ch.getRoomId()).thenReturn(roomId);
        when(target.getName()).thenReturn("Morgan");
        when(characterRepository.getByRoom(eq(roomId))).thenReturn(List.of(ch, target));

        AbstractCommand uut = new AbstractCommand(repositoryBundle, commService, applicationContext) {
            @Override
            public Question execute(Question question, WebSocketContext webSocketContext, List<String> tokens, Input input, Output output) {
                return question;
            }
        };

        assertTrue(uut.findRoomCharacter(ch, "Scion").isEmpty()); // can't find yourself
        assertTrue(uut.findRoomCharacter(ch, "Morgan").isPresent()); // case-insensitive match
    }

    @Test
    void testFindWorldCharacter() {
        lenient().when(ch.getName()).thenReturn("Scion");
        lenient().when(ch.isPrototype()).thenReturn(false);
        when(proto.isPrototype()).thenReturn(true);
        when(target.getName()).thenReturn("Morgan");
        when(target.isPrototype()).thenReturn(false);
        when(characterRepository.getByType(eq(TYPE_PC))).thenReturn(List.of(ch, proto, target));

        AbstractCommand uut = new AbstractCommand(repositoryBundle, commService, applicationContext) {
            @Override
            public Question execute(Question question, WebSocketContext webSocketContext, List<String> tokens, Input input, Output output) {
                return question;
            }
        };

        assertTrue(uut.findWorldCharacter(ch, "Scion").isEmpty()); // can't find yourself
        assertFalse(uut.findWorldCharacter(ch, "Morgan")
            .orElseThrow()   // case-insensitive match
            .isPrototype()); // is not prototype
    }
}
