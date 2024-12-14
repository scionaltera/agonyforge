package com.agonyforge.mud.demo.cli.question.ingame.olc.room;

import com.agonyforge.mud.core.cli.Question;
import com.agonyforge.mud.core.cli.Response;
import com.agonyforge.mud.core.web.model.Input;
import com.agonyforge.mud.core.web.model.Output;
import com.agonyforge.mud.core.web.model.WebSocketContext;
import com.agonyforge.mud.demo.cli.RepositoryBundle;
import com.agonyforge.mud.demo.model.constant.Direction;
import com.agonyforge.mud.demo.model.impl.MudCharacter;
import com.agonyforge.mud.demo.model.impl.MudRoom;
import com.agonyforge.mud.demo.model.repository.MudCharacterRepository;
import com.agonyforge.mud.demo.model.repository.MudRoomRepository;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.ArgumentCaptor;
import org.mockito.Captor;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.context.ApplicationContext;

import java.util.*;

import static com.agonyforge.mud.core.config.SessionConfiguration.MUD_CHARACTER;
import static com.agonyforge.mud.demo.cli.question.ingame.olc.room.RoomEditorQuestion.REDIT_STATE;
import static com.agonyforge.mud.demo.cli.question.ingame.olc.room.RoomExitsQuestion.REDIT_EXIT;
import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.ArgumentMatchers.eq;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
public class RoomExitsQuestionTest {
    private static final Random RAND = new Random();

    @Mock
    private ApplicationContext applicationContext;

    @Mock
    private RepositoryBundle repositoryBundle;

    @Mock
    private MudCharacterRepository characterRepository;

    @Mock
    private MudRoomRepository roomRepository;

    @Mock
    private WebSocketContext wsContext;

    @Mock
    private Question question;

    @Mock
    private MudCharacter ch;

    @Mock
    private MudRoom room;

    @Mock
    private MudRoom destRoom;

    @Captor
    private ArgumentCaptor<MudRoom.Exit> exitCaptor;

    @BeforeEach
    void setup() {
        lenient().when(repositoryBundle.getCharacterRepository()).thenReturn(characterRepository);
        lenient().when(repositoryBundle.getRoomRepository()).thenReturn(roomRepository);
    }

    @Test
    void testPromptMenu() {
        Long chId = RAND.nextLong();
        long roomId = RAND.nextLong(100, 200);
        Map<String, Object> attributes = new HashMap<>();

        attributes.put(MUD_CHARACTER, chId);

        when(ch.getRoomId()).thenReturn(roomId);
        when(wsContext.getAttributes()).thenReturn(attributes);

        when(characterRepository.findById(eq(chId))).thenReturn(Optional.of(ch));
        when(roomRepository.findById(eq(roomId))).thenReturn(Optional.of(room));

        RoomExitsQuestion uut = new RoomExitsQuestion(applicationContext, repositoryBundle);
        Output output = uut.prompt(wsContext);

        assertTrue(output.getOutput().get(1).contains("*****"));
    }

    @Test
    void testPromptExit() {
        Long chId = RAND.nextLong();
        long roomId = RAND.nextLong(100, 200);
        Map<String, Object> attributes = new HashMap<>();

        attributes.put(MUD_CHARACTER, chId);
        attributes.put(REDIT_STATE, "ROOM.EXITS");

        when(ch.getRoomId()).thenReturn(roomId);
        when(wsContext.getAttributes()).thenReturn(attributes);

        when(characterRepository.findById(eq(chId))).thenReturn(Optional.of(ch));
        when(roomRepository.findById(eq(roomId))).thenReturn(Optional.of(room));

        RoomExitsQuestion uut = new RoomExitsQuestion(applicationContext, repositoryBundle);
        Output output = uut.prompt(wsContext);

        assertTrue(output.getOutput().get(0).contains("Destination room ID"));
    }

    @Test
    void testAnswerExitDirection() {
        Long chId = RAND.nextLong();
        long roomId = RAND.nextLong(100, 150);
        long destId = RAND.nextLong(150, 200);
        Map<String, Object> attributes = new HashMap<>();

        attributes.put(MUD_CHARACTER, chId);
        attributes.put(REDIT_EXIT, Direction.NORTH);

        when(ch.getRoomId()).thenReturn(roomId);
        when(wsContext.getAttributes()).thenReturn(attributes);

        when(characterRepository.findById(eq(chId))).thenReturn(Optional.of(ch));
        when(roomRepository.findById(eq(roomId))).thenReturn(Optional.of(room));
        when(roomRepository.findById(eq(destId))).thenReturn(Optional.of(destRoom));
        when(applicationContext.getBean(eq("roomExitsQuestion"), eq(Question.class))).thenReturn(question);

        RoomExitsQuestion uut = new RoomExitsQuestion(applicationContext, repositoryBundle);
        Response response = uut.answer(wsContext, new Input(Long.toString(destId)));

        verify(room).setExit(eq("north"), exitCaptor.capture());

        MudRoom.Exit exit = exitCaptor.getValue();

        assertEquals(destId, exit.getDestinationId());

        assertTrue(response.getFeedback().isPresent());
        assertEquals(question, response.getNext());
    }

    @Test
    void testAnswerExitDelete() {
        Long chId = RAND.nextLong();
        long roomId = RAND.nextLong(100, 150);
        Map<String, Object> attributes = new HashMap<>();

        attributes.put(MUD_CHARACTER, chId);
        attributes.put(REDIT_EXIT, Direction.NORTH);

        when(ch.getRoomId()).thenReturn(roomId);
        when(wsContext.getAttributes()).thenReturn(attributes);

        when(characterRepository.findById(eq(chId))).thenReturn(Optional.of(ch));
        when(roomRepository.findById(eq(roomId))).thenReturn(Optional.of(room));
        when(applicationContext.getBean(eq("roomExitsQuestion"), eq(Question.class))).thenReturn(question);

        RoomExitsQuestion uut = new RoomExitsQuestion(applicationContext, repositoryBundle);
        Response response = uut.answer(wsContext, new Input("0"));

        verify(room).removeExit(eq("north"));

        assertTrue(response.getFeedback().isPresent());
        assertEquals(question, response.getNext());
    }

    @Test
    void testAnswerChooseExit() {
        Long chId = RAND.nextLong();
        long roomId = RAND.nextLong(100, 150);
        Map<String, Object> attributes = new HashMap<>();

        attributes.put(MUD_CHARACTER, chId);

        when(ch.getRoomId()).thenReturn(roomId);
        when(wsContext.getAttributes()).thenReturn(attributes);

        when(characterRepository.findById(eq(chId))).thenReturn(Optional.of(ch));
        when(roomRepository.findById(eq(roomId))).thenReturn(Optional.of(room));
        when(applicationContext.getBean(eq("roomExitsQuestion"), eq(Question.class))).thenReturn(question);

        RoomExitsQuestion uut = new RoomExitsQuestion(applicationContext, repositoryBundle);
        uut.populateMenuItems(room);
        Response response = uut.answer(wsContext, new Input("1"));

        assertEquals("ROOM.EXITS", attributes.get(REDIT_STATE));
        assertInstanceOf(Direction.class, attributes.get(REDIT_EXIT));
        assertTrue(response.getFeedback().isPresent());
        assertEquals(question, response.getNext());
    }

    @Test
    void testAnswerQuit() {
        Long chId = RAND.nextLong();
        long roomId = RAND.nextLong(100, 150);
        Map<String, Object> attributes = new HashMap<>();

        attributes.put(MUD_CHARACTER, chId);

        when(ch.getRoomId()).thenReturn(roomId);
        when(wsContext.getAttributes()).thenReturn(attributes);

        when(characterRepository.findById(eq(chId))).thenReturn(Optional.of(ch));
        when(roomRepository.findById(eq(roomId))).thenReturn(Optional.of(room));
        when(applicationContext.getBean(eq("roomEditorQuestion"), eq(Question.class))).thenReturn(question);

        RoomExitsQuestion uut = new RoomExitsQuestion(applicationContext, repositoryBundle);
        Response response = uut.answer(wsContext, new Input("q"));

        assertTrue(response.getFeedback().isPresent());
        assertEquals(question, response.getNext());
    }
}
