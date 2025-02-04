package com.agonyforge.mud.demo.cli.question.ingame.olc.room;

import com.agonyforge.mud.core.cli.Question;
import com.agonyforge.mud.core.cli.Response;
import com.agonyforge.mud.core.web.model.Input;
import com.agonyforge.mud.core.web.model.Output;
import com.agonyforge.mud.core.web.model.WebSocketContext;
import com.agonyforge.mud.demo.cli.RepositoryBundle;
import com.agonyforge.mud.demo.model.constant.RoomFlag;
import com.agonyforge.mud.demo.model.impl.CharacterComponent;
import com.agonyforge.mud.demo.model.impl.LocationComponent;
import com.agonyforge.mud.demo.model.impl.MudCharacter;
import com.agonyforge.mud.demo.model.impl.MudRoom;
import com.agonyforge.mud.demo.model.repository.MudCharacterRepository;
import com.agonyforge.mud.demo.model.repository.MudRoomRepository;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.junit.jupiter.params.ParameterizedTest;
import org.junit.jupiter.params.provider.ValueSource;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.context.ApplicationContext;

import java.util.EnumSet;
import java.util.HashMap;
import java.util.Map;
import java.util.Optional;

import static com.agonyforge.mud.core.config.SessionConfiguration.MUD_CHARACTER;
import static com.agonyforge.mud.demo.cli.question.ingame.olc.room.RoomEditorQuestion.REDIT_MODEL;
import static com.agonyforge.mud.demo.cli.question.ingame.olc.room.RoomEditorQuestion.REDIT_STATE;
import static com.agonyforge.mud.demo.cli.question.ingame.olc.room.RoomFlagsEditorQuestion.REDIT_FLAG;
import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.ArgumentMatchers.eq;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
public class RoomFlagsEditorQuestionTest {
    @Mock
    private RepositoryBundle repositoryBundle;

    @Mock
    private MudRoomRepository mudRoomRepository;

    @Mock
    private MudCharacterRepository mudCharacterRepository;

    @Mock
    private ApplicationContext applicationContext;

    @Mock
    private WebSocketContext webSocketContext;

    @Mock
    private MudRoom room;

    @Mock
    private MudCharacter ch;

    @Mock
    private CharacterComponent chComponent;

    @Mock
    private LocationComponent chLocation;

    @Mock
    private Question question, roomEditorQuestion;

    @BeforeEach
    void setUp() {
        lenient().when(repositoryBundle.getRoomRepository()).thenReturn(mudRoomRepository);
        lenient().when(repositoryBundle.getCharacterRepository()).thenReturn(mudCharacterRepository);

        lenient().when(mudRoomRepository.save(eq(room))).thenReturn(room);
        lenient().when(mudCharacterRepository.findById(eq(65L))).thenReturn(Optional.of(ch));

        lenient().when(applicationContext.getBean(eq("roomFlagsEditorQuestion"), eq(Question.class))).thenReturn(question);
        lenient().when(applicationContext.getBean(eq("roomEditorQuestion"), eq(Question.class))).thenReturn(roomEditorQuestion);

        lenient().when(ch.getCharacter()).thenReturn(chComponent);
        lenient().when(ch.getLocation()).thenReturn(chLocation);
        lenient().when(chLocation.getRoom()).thenReturn(room);
    }

    @Test
    void testPrompt() {
        when(room.getFlags()).thenReturn(EnumSet.noneOf(RoomFlag.class));
        when(webSocketContext.getAttributes()).thenReturn(Map.of(
            MUD_CHARACTER, 65L,
            REDIT_MODEL, room
        ));

        RoomFlagsEditorQuestion uut = new RoomFlagsEditorQuestion(applicationContext, repositoryBundle);
        Output result = uut.prompt(webSocketContext);

        assertTrue(result.getOutput().stream().anyMatch(line -> line.contains("Room Flags")));
    }

    @Test
    void testPromptToggleOn() {
        Map<String, Object> attributes = new HashMap<>();
        EnumSet<RoomFlag> flags = EnumSet.noneOf(RoomFlag.class);

        attributes.put(REDIT_MODEL, room);
        attributes.put(REDIT_STATE, REDIT_FLAG);
        attributes.put(REDIT_FLAG, RoomFlag.INDOORS);

        when(webSocketContext.getAttributes()).thenReturn(attributes);
        when(room.getFlags()).thenReturn(flags);

        RoomFlagsEditorQuestion uut = new RoomFlagsEditorQuestion(applicationContext, repositoryBundle);
        Output result = uut.prompt(webSocketContext);

        assertTrue(result.getOutput().stream().anyMatch(line -> line.contains("Room Flags")));
        assertTrue(flags.contains(RoomFlag.INDOORS));

        verify(mudRoomRepository).save(room);
    }

    @Test
    void testPromptToggleOff() {
        Map<String, Object> attributes = new HashMap<>();
        EnumSet<RoomFlag> flags = EnumSet.of(RoomFlag.INDOORS);

        attributes.put(REDIT_MODEL, room);
        attributes.put(REDIT_STATE, REDIT_FLAG);
        attributes.put(REDIT_FLAG, RoomFlag.INDOORS);

        when(webSocketContext.getAttributes()).thenReturn(attributes);
        when(room.getFlags()).thenReturn(flags);

        RoomFlagsEditorQuestion uut = new RoomFlagsEditorQuestion(applicationContext, repositoryBundle);
        Output result = uut.prompt(webSocketContext);

        assertTrue(result.getOutput().stream().anyMatch(line -> line.contains("Room Flags")));
        assertFalse(flags.contains(RoomFlag.INDOORS));

        verify(mudRoomRepository).save(room);
    }

    @ParameterizedTest
    @ValueSource(strings = { "1" })
    void testAnswer(String input) {
        Map<String, Object> attributes = new HashMap<>();

        attributes.put(REDIT_MODEL, room);

        when(webSocketContext.getAttributes()).thenReturn(attributes);
        when(room.getFlags()).thenReturn(EnumSet.noneOf(RoomFlag.class));

        RoomFlagsEditorQuestion uut = new RoomFlagsEditorQuestion(applicationContext, repositoryBundle);
        uut.prompt(webSocketContext); // TODO - design flaw: required to populate menu items
        Response result = uut.answer(webSocketContext, new Input(input));

        assertEquals(question, result.getNext());
        assertTrue(attributes.containsKey(REDIT_MODEL));
        assertEquals(REDIT_FLAG, attributes.get(REDIT_STATE));
        assertTrue(attributes.containsKey(REDIT_FLAG));
    }

    @ParameterizedTest
    @ValueSource(strings = { "0", "T", "💀" })
    void testAnswerInvalid(String input) {
        Map<String, Object> attributes = new HashMap<>();

        attributes.put(REDIT_MODEL, room);

        when(webSocketContext.getAttributes()).thenReturn(attributes);
        when(room.getFlags()).thenReturn(EnumSet.noneOf(RoomFlag.class));

        RoomFlagsEditorQuestion uut = new RoomFlagsEditorQuestion(applicationContext, repositoryBundle);
        uut.prompt(webSocketContext); // TODO - design flaw: required to populate menu items
        Response result = uut.answer(webSocketContext, new Input(input));

        assertEquals(question, result.getNext());
        assertTrue(result.getFeedback().orElseThrow().getOutput().stream().anyMatch(line -> line.contains("Please choose a menu item.")));
        assertTrue(attributes.containsKey(REDIT_MODEL));
        assertFalse(attributes.containsKey(REDIT_STATE));
        assertFalse(attributes.containsKey(REDIT_FLAG));
    }

    @Test
    void testAnswerExit() {
        Map<String, Object> attributes = new HashMap<>();

        attributes.put(REDIT_MODEL, room);
        attributes.put(REDIT_FLAG, RoomFlag.INDOORS);

        when(webSocketContext.getAttributes()).thenReturn(attributes);

        RoomFlagsEditorQuestion uut = new RoomFlagsEditorQuestion(applicationContext, repositoryBundle);
        Response result = uut.answer(webSocketContext, new Input("x"));

        assertEquals(roomEditorQuestion, result.getNext());
        assertTrue(attributes.containsKey(REDIT_MODEL));
        assertFalse(attributes.containsKey(REDIT_STATE));
        assertFalse(attributes.containsKey(REDIT_FLAG));
    }
}
