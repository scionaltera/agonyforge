package com.agonyforge.mud.demo.cli.command;

import com.agonyforge.mud.core.cli.Question;
import com.agonyforge.mud.core.web.model.Input;
import com.agonyforge.mud.core.web.model.Output;
import com.agonyforge.mud.core.web.model.WebSocketContext;
import com.agonyforge.mud.demo.cli.question.CommandException;
import com.agonyforge.mud.demo.cli.RepositoryBundle;
import com.agonyforge.mud.demo.model.impl.CharacterComponent;
import com.agonyforge.mud.demo.model.impl.LocationComponent;
import com.agonyforge.mud.demo.model.impl.MudCharacter;
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

import java.util.*;

import static com.agonyforge.mud.core.config.SessionConfiguration.MUD_CHARACTER;
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
    private CharacterComponent targetCharacterComponent;

    @Mock
    private LocationComponent chLocationComponent;

    @Mock
    private MudRoom room;

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
    void testGetCharacterNotFound() {
        Long chId = random.nextLong();

        when(characterRepository.findById(eq(chId))).thenReturn(Optional.empty());
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
        Long chId = random.nextLong();

        when(ch.getLocation()).thenReturn(chLocationComponent);
        when(ch.getLocation().getRoom()).thenReturn(null);
        when(characterRepository.findById(eq(chId))).thenReturn(Optional.of(ch));
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
        Long chId = random.nextLong();

        when(ch.getLocation()).thenReturn(chLocationComponent);
        when(ch.getLocation().getRoom()).thenReturn(room);
        when(characterRepository.findById(eq(chId))).thenReturn(Optional.of(ch));
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
        when(ch.getLocation()).thenReturn(chLocationComponent);
        when(ch.getLocation().getRoom()).thenReturn(room);
        when(targetCharacterComponent.getName()).thenReturn("Morgan");
        when(target.getCharacter()).thenReturn(targetCharacterComponent);
        when(characterRepository.findByLocationRoom(eq(room))).thenReturn(List.of(ch, target));

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
        when(targetCharacterComponent.getName()).thenReturn("Morgan");
        when(target.getCharacter()).thenReturn(targetCharacterComponent);
        when(target.getCharacter().getName()).thenReturn("Morgan");
        when(characterRepository.findAll()).thenReturn(List.of(ch, target));

        AbstractCommand uut = new AbstractCommand(repositoryBundle, commService, applicationContext) {
            @Override
            public Question execute(Question question, WebSocketContext webSocketContext, List<String> tokens, Input input, Output output) {
                return question;
            }
        };

        assertTrue(uut.findWorldCharacter(ch, "Scion").isEmpty()); // can't find yourself
        assertFalse(uut.findWorldCharacter(ch, "Morgan").isEmpty());
    }
}
