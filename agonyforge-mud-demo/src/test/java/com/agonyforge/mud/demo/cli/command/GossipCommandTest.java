package com.agonyforge.mud.demo.cli.command;

import com.agonyforge.mud.core.cli.Question;
import com.agonyforge.mud.core.web.model.Output;
import com.agonyforge.mud.demo.cli.Binding;
import com.agonyforge.mud.demo.cli.SyntaxAwareTokenizer;
import com.agonyforge.mud.demo.model.impl.CharacterComponent;
import com.agonyforge.mud.demo.model.impl.LocationComponent;
import com.agonyforge.mud.demo.model.impl.MudRoom;
import org.junit.jupiter.api.extension.ExtendWith;
import org.junit.jupiter.params.ParameterizedTest;
import org.junit.jupiter.params.provider.ValueSource;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import java.util.*;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.eq;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

@ExtendWith(MockitoExtension.class)
public class GossipCommandTest extends CommandTestBoilerplate {
    @Mock
    private MudRoom room;

    @Mock
    private CharacterComponent characterComponent;

    @Mock
    private LocationComponent chLocationComponent;

    @Mock
    private Binding messageBinding;

    @ParameterizedTest
    @ValueSource(strings = {
        "gossip test",
        "gossip  test",
        "gossip   test",
        "gossip test ",
        "gossip test test",
        "gossip test test test"
    })
    void testExecute(String val) {
        GossipCommand uut = new GossipCommand(repositoryBundle, commService, applicationContext);
        String match = new Output(val.substring(7)).getOutput().get(0);
        List<String> tokens = SyntaxAwareTokenizer.tokenize(val, uut.getSyntaxes().get(0));

        when(messageBinding.asString()).thenReturn(tokens.get(1));
        when(ch.getLocation()).thenReturn(chLocationComponent);
        when(ch.getLocation().getRoom()).thenReturn(room);
        when(ch.getCharacter()).thenReturn(characterComponent);

        Output output = new Output();

        Question response = uut.execute(question, webSocketContext, List.of(commandBinding, messageBinding), output);

        assertEquals(question, response);
        assertEquals(1, output.getOutput().size());
        assertEquals("[green]You gossip, '" + match + "[green]'", output.getOutput().get(0));

        verify(characterRepository).findById(eq(CH_ID));
        verify(commService).sendToAll(eq(webSocketContext), any(Output.class));
    }
}
