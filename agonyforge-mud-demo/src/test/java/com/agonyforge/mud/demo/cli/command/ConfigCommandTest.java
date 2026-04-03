package com.agonyforge.mud.demo.cli.command;

import com.agonyforge.mud.core.cli.Question;
import com.agonyforge.mud.demo.cli.Binding;
import com.agonyforge.mud.demo.model.constant.AdminFlag;
import com.agonyforge.mud.demo.model.impl.LocationComponent;
import com.agonyforge.mud.demo.model.impl.MudCharacter;
import com.agonyforge.mud.demo.model.impl.MudRoom;
import com.agonyforge.mud.demo.model.impl.PlayerComponent;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import com.agonyforge.mud.core.web.model.Output;

import java.util.*;

import static com.agonyforge.mud.core.config.SessionConfiguration.MUD_CHARACTER;
import static org.assertj.core.api.Assertions.assertThat;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
public class ConfigCommandTest extends CommandTestBoilerplate {
    @Mock
    private MudCharacter ch;

    @Mock
    private LocationComponent locationComponent;

    @Mock
    private PlayerComponent playerComponent;

    @Mock
    private MudRoom room;

    @Mock
    private Question question;

    @Mock
    private Binding commandBinding, adminFlagBinding;

    @BeforeEach
    void setUp() {
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

        uut.execute(question, webSocketContext, List.of(commandBinding), output);

        assertThat(output.getOutput()).anyMatch(line -> line.contains("Admin Configuration Flags:"));
        Arrays.stream(AdminFlag.values()).forEach(flag -> assertThat(output.getOutput()).anyMatch(line -> line.contains(flag.name())));
    }
    
    @Test
    void testToggleHolylight() {
        when(webSocketContext.getAttributes()).thenReturn(Map.of(MUD_CHARACTER, 1L));
        when(characterRepository.findById(eq(1L))).thenReturn(Optional.of(ch));
        when(adminFlagBinding.asAdminFlag()).thenReturn(AdminFlag.HOLYLIGHT);

        ConfigCommand uut = new ConfigCommand(repositoryBundle, commService, applicationContext);
        Output output = new Output();
        
        uut.execute(question, webSocketContext, List.of(commandBinding, adminFlagBinding), output);

        assertThat(output.getOutput()).anyMatch(line -> line.contains("HOLYLIGHT enabled."));
    }
}
