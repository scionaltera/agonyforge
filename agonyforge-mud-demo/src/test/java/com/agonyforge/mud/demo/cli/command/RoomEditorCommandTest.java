package com.agonyforge.mud.demo.cli.command;

import com.agonyforge.mud.core.cli.Question;
import com.agonyforge.mud.core.web.model.Output;
import com.agonyforge.mud.demo.cli.Binding;
import com.agonyforge.mud.demo.cli.TokenType;
import com.agonyforge.mud.demo.model.impl.CharacterComponent;
import com.agonyforge.mud.demo.model.impl.LocationComponent;
import com.agonyforge.mud.demo.model.impl.MudCharacter;
import com.agonyforge.mud.demo.model.impl.MudRoom;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Optional;
import java.util.Random;

import static com.agonyforge.mud.core.config.SessionConfiguration.MUD_CHARACTER;
import static com.agonyforge.mud.demo.cli.question.ingame.olc.room.RoomEditorQuestion.REDIT_MODEL;
import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertNotNull;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.eq;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

@ExtendWith(MockitoExtension.class)
public class RoomEditorCommandTest extends CommandTestBoilerplate {
    private static final Random RAND = new Random();

    @Mock
    private Question originalQuestion;

    @Mock
    private Question reditQuestion;

    @Mock
    private MudCharacter ch;

    @Mock
    private CharacterComponent characterComponent;

    @Mock
    private LocationComponent chLocationComponent;

    @Mock
    private MudRoom room;

    @Mock
    private Binding commandBinding, roomBinding, numberBinding;

    private final Random random = new Random();

    @Test
    void testExecuteNoArgs() {
        Long chId = random.nextLong();
        long roomId = RAND.nextLong(100, 200);
        Map<String, Object> attributes = new HashMap<>();

        attributes.put(MUD_CHARACTER, chId);

        when(applicationContext.getBean(eq("roomEditorQuestion"), eq(Question.class))).thenReturn(reditQuestion);
        when(characterRepository.findById(eq(chId))).thenReturn(Optional.of(ch));
        when(webSocketContext.getAttributes()).thenReturn(attributes);
        when(room.getId()).thenReturn(roomId);
        when(ch.getLocation()).thenReturn(chLocationComponent);
        when(ch.getCharacter()).thenReturn(characterComponent);
        when(ch.getLocation().getRoom()).thenReturn(room);

        RoomEditorCommand uut = new RoomEditorCommand(repositoryBundle, commService, applicationContext);
        Output output = new Output();

        Question result = uut.execute(originalQuestion, webSocketContext, List.of(commandBinding), output);

        assertEquals(roomId, attributes.get(REDIT_MODEL));
        assertEquals(reditQuestion, result);
    }

    @Test
    void testExecuteExistingRoom() {
        Long chId = random.nextLong();
        long roomId = RAND.nextLong(100, 200);
        Map<String, Object> attributes = new HashMap<>();

        attributes.put(MUD_CHARACTER, chId);

        when(roomBinding.getType()).thenReturn(TokenType.ROOM_ID);
        when(roomBinding.asRoom()).thenReturn(room);
        when(applicationContext.getBean(eq("roomEditorQuestion"), eq(Question.class))).thenReturn(reditQuestion);
        when(characterRepository.findById(eq(chId))).thenReturn(Optional.of(ch));
        when(webSocketContext.getAttributes()).thenReturn(attributes);
        when(ch.getCharacter()).thenReturn(characterComponent);
        when(ch.getLocation()).thenReturn(chLocationComponent);
        when(ch.getLocation().getRoom()).thenReturn(room);
        when(room.getId()).thenReturn(roomId);

        RoomEditorCommand uut = new RoomEditorCommand(repositoryBundle, commService, applicationContext);
        Output output = new Output();

        Question result = uut.execute(originalQuestion, webSocketContext, List.of(commandBinding, roomBinding), output);

        assertEquals(roomId, attributes.get(REDIT_MODEL));
        assertEquals(reditQuestion, result);
    }

    @Test
    void testExecuteRoomId() {
        Long chId = random.nextLong();
        long roomId = RAND.nextLong(100, 200);
        Map<String, Object> attributes = new HashMap<>();

        attributes.put(MUD_CHARACTER, chId);

        when(numberBinding.getType()).thenReturn(TokenType.NUMBER);
        when(numberBinding.asNumber()).thenReturn(roomId);
        when(applicationContext.getBean(eq("roomEditorQuestion"), eq(Question.class))).thenReturn(reditQuestion);
        when(characterRepository.findById(eq(chId))).thenReturn(Optional.of(ch));
        when(webSocketContext.getAttributes()).thenReturn(attributes);
        when(ch.getCharacter()).thenReturn(characterComponent);
        when(ch.getLocation()).thenReturn(chLocationComponent);
        when(ch.getLocation().getRoom()).thenReturn(room);

        RoomEditorCommand uut = new RoomEditorCommand(repositoryBundle, commService, applicationContext);
        Output output = new Output();

        Question result = uut.execute(originalQuestion, webSocketContext, List.of(commandBinding, numberBinding), output);

        verify(roomRepository).save(any(MudRoom.class));
        assertNotNull(attributes.get(REDIT_MODEL));
        assertEquals(reditQuestion, result);
    }
}
