package com.agonyforge.mud.demo.cli.command;

import com.agonyforge.mud.core.cli.Question;
import com.agonyforge.mud.demo.model.constant.AdminFlag;
import com.agonyforge.mud.demo.model.impl.LocationComponent;
import com.agonyforge.mud.demo.model.impl.MudCharacter;
import com.agonyforge.mud.demo.model.impl.MudRoom;
import com.agonyforge.mud.demo.model.impl.PlayerComponent;
import com.agonyforge.mud.demo.model.repository.MudCharacterRepository;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import com.agonyforge.mud.core.web.model.Output;
import com.agonyforge.mud.core.web.model.WebSocketContext;
import org.springframework.context.ApplicationContext;
import com.agonyforge.mud.demo.cli.RepositoryBundle;
import com.agonyforge.mud.demo.service.CommService;

import java.util.EnumSet;
import java.util.List;
import java.util.Map;
import java.util.Optional;

import static com.agonyforge.mud.core.config.SessionConfiguration.MUD_CHARACTER;
import static org.assertj.core.api.Assertions.assertThat;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
public class ConfigCommandTest {
    @Mock
    private RepositoryBundle repositoryBundle;

    @Mock
    private MudCharacterRepository characterRepository;

    @Mock
    private CommService commService;

    @Mock
    private MudCharacter ch;

    @Mock
    private LocationComponent locationComponent;

    @Mock
    private PlayerComponent playerComponent;

    @Mock
    private MudRoom room;

    @Mock
    private ApplicationContext applicationContext;
    
    @Mock
    private WebSocketContext webSocketContext;

    @Mock
    private Question question;

    @BeforeEach
    void setUp() {
        when(repositoryBundle.getCharacterRepository()).thenReturn(characterRepository);
        when(ch.getLocation()).thenReturn(locationComponent);
        when(locationComponent.getRoom()).thenReturn(room);
        when(ch.getPlayer()).thenReturn(playerComponent);
        when(playerComponent.getAdminFlags()).thenReturn(EnumSet.noneOf(AdminFlag.class));
    }

    @Test
    void testListConfigOptions() {
        when(webSocketContext.getAttributes()).thenReturn(Map.of(MUD_CHARACTER, 1L));
        when(characterRepository.findById(eq(1L))).thenReturn(Optional.of(ch));

        ConfigCommand uut = new ConfigCommand(repositoryBundle, commService, applicationContext);
        Output output = new Output();

        uut.execute(question, webSocketContext, List.of("config"), output);

        assertThat(output.getOutput()).anyMatch(line -> line.contains("Admin Configuration Flags:"));
        Arrays.stream(AdminFlag.values()).forEach(flag -> {
            assertThat(output.getOutput()).anyMatch(line -> line.contains(flag.name()));
        });
    }
    
    @Test
    void testToggleHolylight() {
        when(webSocketContext.getAttributes()).thenReturn(Map.of(MUD_CHARACTER, 1L));
        when(characterRepository.findById(eq(1L))).thenReturn(Optional.of(ch));

        ConfigCommand uut = new ConfigCommand(repositoryBundle, commService, applicationContext);
        Output output = new Output();
        
        uut.execute(question, webSocketContext, List.of("config", "holylight"), output);

        assertThat(output.getOutput()).anyMatch(line -> line.contains("HOLYLIGHT enabled."));
    }
}
