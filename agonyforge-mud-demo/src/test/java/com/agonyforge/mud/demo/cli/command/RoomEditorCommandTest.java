package com.agonyforge.mud.demo.cli.command;

import com.agonyforge.mud.core.cli.Question;
import com.agonyforge.mud.core.web.model.Output;
import com.agonyforge.mud.demo.cli.Binding;
import com.agonyforge.mud.demo.cli.TokenType;
import com.agonyforge.mud.demo.model.impl.CharacterComponent;
import com.agonyforge.mud.demo.model.impl.LocationComponent;
import com.agonyforge.mud.demo.model.impl.MudRoom;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import java.util.List;
import java.util.Map;

import static com.agonyforge.mud.demo.cli.question.ingame.olc.room.RoomEditorQuestion.REDIT_MODEL;
import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertNotNull;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.eq;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

@ExtendWith(MockitoExtension.class)
public class RoomEditorCommandTest extends CommandTestBoilerplate {
    @Mock
    private Question originalQuestion, reditQuestion;

    @Mock
    private CharacterComponent characterComponent;

    @Mock
    private LocationComponent chLocationComponent;

    @Mock
    private MudRoom room;

    @Mock
    private Binding roomBinding, numberBinding;

    @Test
    void testExecuteNoArgs() {
        long roomId = getRandom().nextLong(100, 200);

        when(applicationContext.getBean(eq("roomEditorQuestion"), eq(Question.class))).thenReturn(reditQuestion);
        when(room.getId()).thenReturn(roomId);
        when(ch.getLocation()).thenReturn(chLocationComponent);
        when(ch.getCharacter()).thenReturn(characterComponent);
        when(ch.getLocation().getRoom()).thenReturn(room);

        RoomEditorCommand uut = new RoomEditorCommand(repositoryBundle, commService, applicationContext);
        Output output = new Output();

        Question result = uut.execute(originalQuestion, webSocketContext, List.of(commandBinding), output);

        Map<String, Object> attributes = webSocketContext.getAttributes();
        assertEquals(roomId, attributes.get(REDIT_MODEL));
        assertEquals(reditQuestion, result);
    }

    @Test
    void testExecuteExistingRoom() {
        long roomId = getRandom().nextLong(100, 200);

        when(roomBinding.getType()).thenReturn(TokenType.ROOM_ID);
        when(roomBinding.asRoom()).thenReturn(room);
        when(applicationContext.getBean(eq("roomEditorQuestion"), eq(Question.class))).thenReturn(reditQuestion);
        when(ch.getCharacter()).thenReturn(characterComponent);
        when(ch.getLocation()).thenReturn(chLocationComponent);
        when(ch.getLocation().getRoom()).thenReturn(room);
        when(room.getId()).thenReturn(roomId);

        RoomEditorCommand uut = new RoomEditorCommand(repositoryBundle, commService, applicationContext);
        Output output = new Output();

        Question result = uut.execute(originalQuestion, webSocketContext, List.of(commandBinding, roomBinding), output);

        Map<String, Object> attributes = webSocketContext.getAttributes();
        assertEquals(roomId, attributes.get(REDIT_MODEL));
        assertEquals(reditQuestion, result);
    }

    @Test
    void testExecuteRoomId() {
        long roomId = getRandom().nextLong(100, 200);

        when(numberBinding.getType()).thenReturn(TokenType.NUMBER);
        when(numberBinding.asNumber()).thenReturn(roomId);
        when(applicationContext.getBean(eq("roomEditorQuestion"), eq(Question.class))).thenReturn(reditQuestion);
        when(ch.getCharacter()).thenReturn(characterComponent);
        when(ch.getLocation()).thenReturn(chLocationComponent);
        when(ch.getLocation().getRoom()).thenReturn(room);

        RoomEditorCommand uut = new RoomEditorCommand(repositoryBundle, commService, applicationContext);
        Output output = new Output();

        Question result = uut.execute(originalQuestion, webSocketContext, List.of(commandBinding, numberBinding), output);
        Map<String, Object> attributes = webSocketContext.getAttributes();

        verify(roomRepository).save(any(MudRoom.class));
        assertNotNull(attributes.get(REDIT_MODEL));
        assertEquals(reditQuestion, result);
    }
}
