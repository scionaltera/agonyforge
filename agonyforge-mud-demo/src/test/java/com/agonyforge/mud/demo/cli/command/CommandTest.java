package com.agonyforge.mud.demo.cli.command;

import com.agonyforge.mud.core.cli.Question;
import com.agonyforge.mud.core.web.model.Input;
import com.agonyforge.mud.core.web.model.Output;
import com.agonyforge.mud.core.web.model.WebSocketContext;
import com.agonyforge.mud.demo.cli.CommandException;
import com.agonyforge.mud.models.dynamodb.impl.MudCharacter;
import com.agonyforge.mud.models.dynamodb.repository.MudCharacterRepository;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import java.util.List;
import java.util.Map;
import java.util.Optional;
import java.util.UUID;

import static com.agonyforge.mud.core.config.SessionConfiguration.MUD_CHARACTER;
import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.mockito.ArgumentMatchers.eq;
import static org.mockito.Mockito.when;

@ExtendWith(MockitoExtension.class)
public class CommandTest {
    @Mock
    private MudCharacterRepository characterRepository;

    @Mock
    private WebSocketContext webSocketContext;

    @Mock
    private MudCharacter ch;

    @Mock
    private Question question;

    @Test
    void testGetCharacterNotFound() {
        UUID chId = UUID.randomUUID();

        when(characterRepository.getById(eq(chId), eq(false))).thenReturn(Optional.empty());
        when(webSocketContext.getAttributes()).thenReturn(Map.of(
            MUD_CHARACTER, chId
        ));

        Output output = new Output();
        Command uut = (question, webSocketContext, tokens, input, output1) -> {
            Command.getCharacter(characterRepository, webSocketContext, output1);

            return question;
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
        Command uut = (question, webSocketContext, tokens, input, output1) -> {
            Command.getCharacter(characterRepository, webSocketContext, output1);

            return question;
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
        Command uut = (question, webSocketContext, tokens, input, output1) -> {
            Command.getCharacter(characterRepository, webSocketContext, output1);

            return question;
        };

        assertEquals(question, uut.execute(
            question,
            webSocketContext,
            List.of("TEST"),
            new Input("test"),
            output));
    }
}
