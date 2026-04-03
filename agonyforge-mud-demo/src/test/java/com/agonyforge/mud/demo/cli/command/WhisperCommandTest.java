package com.agonyforge.mud.demo.cli.command;

import com.agonyforge.mud.core.cli.Question;
import com.agonyforge.mud.core.web.model.Output;
import com.agonyforge.mud.demo.cli.Binding;
import com.agonyforge.mud.demo.cli.SyntaxAwareTokenizer;
import com.agonyforge.mud.demo.model.impl.CharacterComponent;
import com.agonyforge.mud.demo.model.impl.LocationComponent;
import com.agonyforge.mud.demo.model.impl.MudCharacter;
import com.agonyforge.mud.demo.model.impl.MudRoom;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.extension.ExtendWith;
import org.junit.jupiter.params.ParameterizedTest;
import org.junit.jupiter.params.provider.ValueSource;
import org.mockito.ArgumentCaptor;
import org.mockito.Captor;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import java.util.*;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertTrue;
import static org.mockito.ArgumentMatchers.eq;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.verifyNoMoreInteractions;
import static org.mockito.Mockito.when;

@ExtendWith(MockitoExtension.class)
public class WhisperCommandTest extends CommandTestBoilerplate {
    @Mock
    private MudRoom room;

    @Mock
    private MudCharacter target;

    @Mock
    private CharacterComponent characterComponent, targetCharacterComponent;

    @Mock
    private LocationComponent chLocationComponent;

    @Mock
    private Binding targetBinding, messageBinding;

    @Captor
    private ArgumentCaptor<Output> outputCaptor;

    @BeforeEach
    void setUp() {
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

        when(messageBinding.asString()).thenReturn(tokens.get(2));
        when(ch.getCharacter()).thenReturn(characterComponent);

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

        verify(characterRepository).findById(eq(CH_ID));
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
}
