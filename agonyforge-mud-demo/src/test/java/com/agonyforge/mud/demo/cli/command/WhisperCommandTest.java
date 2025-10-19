package com.agonyforge.mud.demo.cli.command;

import com.agonyforge.mud.core.cli.Question;
import com.agonyforge.mud.core.web.model.Output;
import com.agonyforge.mud.core.web.model.WebSocketContext;
import com.agonyforge.mud.demo.cli.Binding;
import com.agonyforge.mud.demo.cli.RepositoryBundle;
import com.agonyforge.mud.demo.cli.SyntaxAwareTokenizer;
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
import org.junit.jupiter.params.ParameterizedTest;
import org.junit.jupiter.params.provider.ValueSource;
import org.mockito.ArgumentCaptor;
import org.mockito.Captor;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.context.ApplicationContext;

import java.util.*;

import static com.agonyforge.mud.core.config.SessionConfiguration.MUD_CHARACTER;
import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertTrue;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.eq;
import static org.mockito.Mockito.lenient;
import static org.mockito.Mockito.never;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.verifyNoMoreInteractions;
import static org.mockito.Mockito.when;

@ExtendWith(MockitoExtension.class)
public class WhisperCommandTest {
    @Mock
    private ApplicationContext applicationContext;

    @Mock
    private RepositoryBundle repositoryBundle;

    @Mock
    private MudCharacterRepository characterRepository;

    @Mock
    private MudItemRepository itemRepository;

    @Mock
    private MudRoomRepository roomRepository;

    @Mock
    private CommService commService;

    @Mock
    private MudRoom room;

    @Mock
    private MudCharacter ch;

    @Mock
    private MudCharacter target;

    @Mock
    private MudCharacter other;

    @Mock
    private CharacterComponent characterComponent, targetCharacterComponent;

    @Mock
    private LocationComponent chLocationComponent;

    @Mock
    private WebSocketContext webSocketContext;

    @Mock
    private Question question;

    @Mock
    private Binding commandBinding;

    @Mock
    private Binding targetBinding;

    @Mock
    private Binding messageBinding;

    @Captor
    private ArgumentCaptor<Output> outputCaptor;

    private final Random random = new Random();

    @BeforeEach
    void setUp() {
        lenient().when(repositoryBundle.getCharacterRepository()).thenReturn(characterRepository);
        lenient().when(repositoryBundle.getItemRepository()).thenReturn(itemRepository);
        lenient().when(repositoryBundle.getRoomRepository()).thenReturn(roomRepository);

        when(targetBinding.asCharacter()).thenReturn(target);
    }

    @ParameterizedTest
    @ValueSource(strings = {
        "whisper t test",
        "whisper t  test",
        "whisper t   test",
        "whisper t test ",
        "whisper t test  ",
        "whisper t test test",
        "whisper t test test test"
    })
    void testExecute(String val) {
        WhisperCommand uut = new WhisperCommand(repositoryBundle, commService, applicationContext);

        String match = new Output(val.substring(10)).getOutput().get(0); // Output adds non-breaking spaces
        List<String> tokens = SyntaxAwareTokenizer.tokenize(val, uut.getSyntaxes().get(0));
        Long chId = random.nextLong();

        when(messageBinding.asString()).thenReturn(tokens.get(2));
        when(webSocketContext.getAttributes()).thenReturn(Map.of(
            MUD_CHARACTER, chId
        ));
        when(ch.getCharacter()).thenReturn(characterComponent);
        when(characterRepository.findById(eq(chId))).thenReturn(Optional.of(ch));

        when(characterComponent.getName()).thenReturn("Scion");
        when(room.getId()).thenReturn(100L);
        when(ch.getLocation()).thenReturn(chLocationComponent);
        when(ch.getLocation().getRoom()).thenReturn(room);
        when(target.getCharacter()).thenReturn(targetCharacterComponent);
        when(targetCharacterComponent.getName()).thenReturn("Target");

        Output output = new Output();
        Question response = uut.execute(question, webSocketContext, List.of(commandBinding, targetBinding, messageBinding), output);

        assertEquals(question, response);
        assertEquals(1, output.getOutput().size());
        assertEquals("[red]You whisper to Target, '" + match + "[red]'", output.getOutput().get(0));

        verify(characterRepository).findById(eq(chId));
        verify(commService).sendTo(eq(target), outputCaptor.capture());
        verify(commService).sendToRoom(eq(100L), outputCaptor.capture(), eq(ch), eq(target));
        verifyNoMoreInteractions(commService);

        List<Output> captured = outputCaptor.getAllValues();

        Output toTarget = captured.get(0);
        assertTrue(toTarget.getOutput()
            .stream()
            .anyMatch(line -> line.equals("[red]Scion whispers to you, '" + match + "[red]'")));

        Output toOther = captured.get(1);
        assertTrue(toOther.getOutput()
            .stream()
            .anyMatch(line -> line.equals("[red]Scion whispers something to Target.")));
    }

    @Test
    void testExecuteTargetNotFound() {
        WhisperCommand uut = new WhisperCommand(repositoryBundle, commService, applicationContext);
        List<String> tokens = SyntaxAwareTokenizer.tokenize("whisper t foo", uut.getSyntaxes().get(0));
        Output output = new Output();
        Long chId = random.nextLong();

        when(messageBinding.asString()).thenReturn(tokens.get(2));
        when(ch.getLocation()).thenReturn(chLocationComponent);
        when(ch.getLocation().getRoom()).thenReturn(room);
        when(webSocketContext.getAttributes()).thenReturn(Map.of(
            MUD_CHARACTER, chId
        ));
        when(characterRepository.findById(eq(chId))).thenReturn(Optional.of(ch));

        Question response = uut.execute(question, webSocketContext, List.of(commandBinding, targetBinding, messageBinding), output);

        assertEquals(question, response);
        assertEquals(1, output.getOutput().size());
        assertEquals("[default]There isn't anyone by that name.", output.getOutput().get(0));

        verify(commService, never()).sendTo(any(MudCharacter.class), any(Output.class));
    }
}
