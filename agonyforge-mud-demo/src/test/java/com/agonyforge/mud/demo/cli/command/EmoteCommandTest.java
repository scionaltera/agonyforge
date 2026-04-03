package com.agonyforge.mud.demo.cli.command;

import com.agonyforge.mud.core.cli.Question;
import com.agonyforge.mud.core.web.model.Output;
import com.agonyforge.mud.demo.cli.Binding;
import com.agonyforge.mud.demo.cli.SyntaxAwareTokenizer;
import com.agonyforge.mud.demo.model.impl.CharacterComponent;
import com.agonyforge.mud.demo.model.impl.LocationComponent;
import com.agonyforge.mud.demo.model.impl.MudCharacter;
import com.agonyforge.mud.demo.model.impl.MudRoom;
import org.junit.jupiter.api.extension.ExtendWith;
import org.junit.jupiter.params.ParameterizedTest;
import org.junit.jupiter.params.provider.ValueSource;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import java.util.*;

import static com.agonyforge.mud.core.config.SessionConfiguration.MUD_CHARACTER;
import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.mockito.ArgumentMatchers.*;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
public class EmoteCommandTest extends CommandTestBoilerplate {
    @Mock
    private MudRoom room;

    @Mock
    private MudCharacter ch;

    @Mock
    private CharacterComponent characterComponent;

    @Mock
    private LocationComponent chLocationComponent;

    @Mock
    private Question question;

    @Mock
    private Binding commandBinding, messageBinding;

    private final Random random = new Random();

    @ParameterizedTest
    @ValueSource(strings = {
        "emote test",
        "emote  test",
        "emote   test",
        "emote test ",
        "emote test test",
        "emote test test test"
    })
    void testExecute(String val) {
        EmoteCommand uut = new EmoteCommand(repositoryBundle, commService, applicationContext);
        String match = new Output(" " + val.substring(6)).getOutput().get(0);
        List<String> tokens = SyntaxAwareTokenizer.tokenize(val, uut.getSyntaxes().get(0));
        Long chId = random.nextLong();

        when(messageBinding.asString()).thenReturn(tokens.get(1));
        when(webSocketContext.getAttributes()).thenReturn(Map.of(
            MUD_CHARACTER, chId
        ));
        when(room.getId()).thenReturn(100L);
        when(characterComponent.getName()).thenReturn("Name");
        when(characterRepository.findById(eq(chId))).thenReturn(Optional.of(ch));
        when(ch.getCharacter()).thenReturn(characterComponent);
        when(ch.getLocation()).thenReturn(chLocationComponent);
        when(ch.getLocation().getRoom()).thenReturn(room);

        Output output = new Output();

        Question response = uut.execute(question, webSocketContext, List.of(commandBinding, messageBinding), output);

        assertEquals(question, response);
        assertEquals(1, output.getOutput().size());
        assertEquals("[dcyan]" + ch.getCharacter().getName() + match, output.getOutput().get(0));

        verify(characterRepository).findById(eq(chId));
        verify(commService).sendToRoom(eq(100L), any(Output.class), eq(ch));
    }
}
