package com.agonyforge.mud.demo.cli.command;

import com.agonyforge.mud.core.cli.Question;
import com.agonyforge.mud.core.web.model.Input;
import com.agonyforge.mud.core.web.model.Output;
import com.agonyforge.mud.core.web.model.WebSocketContext;
import com.agonyforge.mud.demo.cli.RepositoryBundle;
import com.agonyforge.mud.demo.model.constant.AdminFlag;
import com.agonyforge.mud.demo.model.impl.LocationComponent;
import com.agonyforge.mud.demo.model.impl.MudCharacter;
import com.agonyforge.mud.demo.model.impl.MudRoom;
import com.agonyforge.mud.demo.model.impl.PlayerComponent;
import com.agonyforge.mud.demo.model.repository.MudCharacterRepository;
import com.agonyforge.mud.demo.service.CommService;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.context.ApplicationContext;

import java.util.*;

import static com.agonyforge.mud.core.config.SessionConfiguration.MUD_CHARACTER;
import static org.junit.jupiter.api.Assertions.assertFalse;
import static org.junit.jupiter.api.Assertions.assertTrue;
import static org.mockito.ArgumentMatchers.eq;
import static org.mockito.Mockito.lenient;
import static org.mockito.Mockito.when;

@ExtendWith(MockitoExtension.class)
public class ConfigCommandTest {
    private final Random RANDOM = new Random();

    @Mock
    private RepositoryBundle repositoryBundle;

    @Mock
    private MudCharacterRepository characterRepository;

    @Mock
    private CommService commService;

    @Mock
    private MudCharacter ch;

    @Mock
    private PlayerComponent playerComponent;

    @Mock
    private LocationComponent locationComponent;

    @Mock
    private MudRoom room;

    @Mock
    private Question question;

    @Mock
    private WebSocketContext webSocketContext;

    @Mock
    private ApplicationContext applicationContext;

    @BeforeEach
    void setUp() {
        Long chId = RANDOM.nextLong();

        when(webSocketContext.getAttributes()).thenReturn(Map.of(
            MUD_CHARACTER, chId
        ));
        when(repositoryBundle.getCharacterRepository()).thenReturn(characterRepository);
        when(characterRepository.findById(eq(chId))).thenReturn(Optional.of(ch));

        when(locationComponent.getRoom()).thenReturn(room);

        lenient().when(ch.getPlayer()).thenReturn(playerComponent);
        when(ch.getLocation()).thenReturn(locationComponent);
    }

    @Test
    void configNoArg() {
        ConfigCommand uut = new ConfigCommand(repositoryBundle, commService, applicationContext);
        List<String> tokens = List.of("CONFIG");
        Input input = new Input("config");
        Output output = new Output();

        when(playerComponent.getAdminFlags()).thenReturn(EnumSet.noneOf(AdminFlag.class));

        uut.execute(question, webSocketContext, tokens, input, output);

        assertTrue(output.getOutput().stream().anyMatch(line -> line.contains("Admin Configuration Flags:")));
    }
    
    @Test
    void configHolylightEnable() {
        ConfigCommand uut = new ConfigCommand(repositoryBundle, commService, applicationContext);
        List<String> tokens = List.of("CONFIG", "HOLYLIGHT");
        Input input = new Input("config holylight");
        Output output = new Output();
        EnumSet<AdminFlag> flags = EnumSet.noneOf(AdminFlag.class);

        when(playerComponent.getAdminFlags()).thenReturn(flags);

        uut.execute(question, webSocketContext, tokens, input, output);

        assertTrue(flags.contains(AdminFlag.HOLYLIGHT));
        assertTrue(output.getOutput().stream().anyMatch(line -> line.contains("HOLYLIGHT enabled")));
    }

    @Test
    void configHolylightDisable() {
        ConfigCommand uut = new ConfigCommand(repositoryBundle, commService, applicationContext);
        List<String> tokens = List.of("CONFIG", "HOLYLIGHT");
        Input input = new Input("config holylight");
        Output output = new Output();
        EnumSet<AdminFlag> flags = EnumSet.of(AdminFlag.HOLYLIGHT);

        when(playerComponent.getAdminFlags()).thenReturn(flags);

        uut.execute(question, webSocketContext, tokens, input, output);

        assertFalse(flags.contains(AdminFlag.HOLYLIGHT));
        assertTrue(output.getOutput().stream().anyMatch(line -> line.contains("HOLYLIGHT disabled")));
    }


    @Test
    void configInvalidFlag() {
        ConfigCommand uut = new ConfigCommand(repositoryBundle, commService, applicationContext);
        List<String> tokens = List.of("CONFIG", "INVALIDFLAG");
        Input input = new Input("config invalidflag");
        Output output = new Output();
        EnumSet<AdminFlag> flags = EnumSet.noneOf(AdminFlag.class);

        lenient().when(playerComponent.getAdminFlags()).thenReturn(flags);

        uut.execute(question, webSocketContext, tokens, input, output);

        assertTrue(output.getOutput().stream().anyMatch(line -> line.contains("No such config flag exists")));
    }

}
