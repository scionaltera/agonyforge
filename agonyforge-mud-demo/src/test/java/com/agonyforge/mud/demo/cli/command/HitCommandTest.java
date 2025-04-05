package com.agonyforge.mud.demo.cli.command;

import com.agonyforge.mud.core.cli.Question;
import com.agonyforge.mud.core.web.model.Input;
import com.agonyforge.mud.core.web.model.Output;
import com.agonyforge.mud.core.web.model.WebSocketContext;
import com.agonyforge.mud.demo.cli.RepositoryBundle;
import com.agonyforge.mud.demo.model.impl.CharacterComponent;
import com.agonyforge.mud.demo.model.impl.LocationComponent;
import com.agonyforge.mud.demo.model.impl.MudCharacter;
import com.agonyforge.mud.demo.model.impl.MudRoom;
import com.agonyforge.mud.demo.model.repository.MudCharacterRepository;
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
import java.util.Random;

import static com.agonyforge.mud.core.config.SessionConfiguration.MUD_CHARACTER;
import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertTrue;
import static org.mockito.ArgumentMatchers.eq;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
public class HitCommandTest {
    private static final Random RANDOM = new Random();

    @Mock
    private RepositoryBundle repositoryBundle;

    @Mock
    private CommService commService;

    @Mock
    private ApplicationContext applicationContext;

    @Mock
    private Question question;

    @Mock
    private WebSocketContext webSocketContext;

    @Mock
    private MudCharacterRepository characterRepository;

    @Mock
    private MudCharacter ch, target;

    @Mock
    private LocationComponent chLocationComponent;

    @Mock
    private CharacterComponent chCharacter, targetCharacter;

    @Mock
    private MudRoom room;

    @BeforeEach
    void setUp() {
        lenient().when(repositoryBundle.getCharacterRepository()).thenReturn(characterRepository);
        lenient().when(ch.getLocation()).thenReturn(chLocationComponent);
        lenient().when(chLocationComponent.getRoom()).thenReturn(room);
        lenient().when(ch.getCharacter()).thenReturn(chCharacter);
        lenient().when(chCharacter.getName()).thenReturn("Scion");
        lenient().when(target.getCharacter()).thenReturn(targetCharacter);
        lenient().when(targetCharacter.getName()).thenReturn("Frodo");
    }

    @Test
    void testHitNoArg() {
        Output output = new Output();
        HitCommand uut = new HitCommand(repositoryBundle, commService, applicationContext);
        Question result = uut.execute(
            question,
            webSocketContext,
            List.of("HIT"),
            new Input("hit"),
            output);

        assertEquals(question, result);
        assertTrue(output.getOutput().get(0).contains("Who do you want to hit?"));

        verifyNoInteractions(commService);
    }

    @Test
    void testHitNoTarget() {
        Long chId = RANDOM.nextLong();

        when(webSocketContext.getAttributes()).thenReturn(Map.of(
            MUD_CHARACTER, chId
        ));
        when(characterRepository.findById(eq(chId))).thenReturn(Optional.of(ch));

        Output output = new Output();
        HitCommand uut = new HitCommand(repositoryBundle, commService, applicationContext);
        Question result = uut.execute(
            question,
            webSocketContext,
            List.of("HIT", "FRODO"),
            new Input("hit frodo"),
            output);

        assertEquals(question, result);
        assertTrue(output.getOutput().get(0).contains("You don't see anyone like that here."));

        verifyNoInteractions(commService);
    }

    @Test
    void testHitTarget() {
        Long chId = RANDOM.nextLong();
        Long roomId = RANDOM.nextLong();

        when(webSocketContext.getAttributes()).thenReturn(Map.of(
            MUD_CHARACTER, chId
        ));
        when(room.getId()).thenReturn(roomId);
        when(characterRepository.findById(eq(chId))).thenReturn(Optional.of(ch));
        when(characterRepository.findByLocationRoom(eq(room))).thenReturn(List.of(ch, target));

        Output output = new Output();
        HitCommand uut = new HitCommand(repositoryBundle, commService, applicationContext);
        Question result = uut.execute(
            question,
            webSocketContext,
            List.of("HIT", "FRODO"),
            new Input("hit frodo"),
            output);

        assertEquals(question, result);
        assertTrue(output.getOutput().get(0).contains("You hit Frodo!"));

        verify(commService).sendTo(eq(target), any(Output.class));
        verify(commService).sendToRoom(eq(roomId), any(Output.class), eq(ch), eq(target));
    }
}
