package com.agonyforge.mud.demo.cli.command;

import com.agonyforge.mud.core.cli.Question;
import com.agonyforge.mud.demo.cli.RepositoryBundle;
import com.agonyforge.mud.demo.model.constant.Effort;
import com.agonyforge.mud.demo.model.constant.Stat;
import com.agonyforge.mud.demo.model.impl.LocationComponent;
import com.agonyforge.mud.demo.model.impl.MudCharacter;
import com.agonyforge.mud.demo.model.impl.CharacterComponent;
import com.agonyforge.mud.demo.model.impl.MudRoom;
import com.agonyforge.mud.demo.service.CommService;
import com.agonyforge.mud.demo.model.repository.MudCharacterRepository;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import com.agonyforge.mud.core.service.dice.DiceResult;
import com.agonyforge.mud.core.service.dice.DiceService;
import com.agonyforge.mud.core.web.model.Output;
import com.agonyforge.mud.core.web.model.WebSocketContext;
import org.springframework.context.ApplicationContext;

import java.util.Arrays;
import java.util.List;
import java.util.Map;
import java.util.Optional;

import static com.agonyforge.mud.core.config.SessionConfiguration.MUD_CHARACTER;
import static org.assertj.core.api.Assertions.assertThat;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
public class RollCommandTest {
    @Mock
    private RepositoryBundle repositoryBundle;
    
    @Mock
    private MudCharacterRepository characterRepository;
    
    @Mock
    private CommService commService;
    
    @Mock
    private WebSocketContext webSocketContext;
    
    @Mock
    private Question question;
    
    @Mock
    private ApplicationContext applicationContext;
    
    @Mock
    private DiceService diceService;

    @Mock
    private MudCharacter ch;

    @Mock
    private MudRoom room;

    @Mock
    private CharacterComponent characterComponent;

    @Mock
    private LocationComponent locationComponent;
    
    @BeforeEach
    void setUp() {
        when(repositoryBundle.getCharacterRepository()).thenReturn(characterRepository);
        when(webSocketContext.getAttributes()).thenReturn(Map.of(MUD_CHARACTER, 1L));
        when(characterRepository.findById(1L)).thenReturn(Optional.of(ch));
        lenient().when(ch.getCharacter()).thenReturn(characterComponent);
        when(ch.getLocation()).thenReturn(locationComponent);
        when(locationComponent.getRoom()).thenReturn(room);
    }
    
    @Test
    void testExecuteWithValidInput() {
        RollCommand uut = new RollCommand(repositoryBundle, commService, applicationContext, diceService);

        // Set up character stats
        when(characterComponent.getStat(Stat.STR)).thenReturn(2);
        when(characterComponent.getEffort(Effort.BASIC)).thenReturn(3);

        // Mock dice service to return specific values
        DiceResult attemptRoll = new DiceResult(20, 2, 12);
        DiceResult effortRoll = new DiceResult(Effort.BASIC.getDie(), 3, 5);
        
        when(diceService.roll(1, 20, 2)).thenReturn(attemptRoll);
        when(diceService.roll(1, Effort.BASIC.getDie(), 3)).thenReturn(effortRoll);

        Output output = new Output();
        List<String> tokens = Arrays.asList("roll", "STR", "Basic");
        
        uut.execute(question, webSocketContext, tokens, output);
        
        // Check that the output contains the expected messages
        List<String> outputLines = output.toList();
        assertThat(outputLines).anyMatch(line -> Command.stripColors(line).contains("ATTEMPT: 12 + 2 = 14 for STR"));
        assertThat(outputLines).anyMatch(line -> Command.stripColors(line).contains("EFFORT: 5 + 3 = 8 for Basic"));
        assertThat(outputLines).anyMatch(line -> Command.stripColors(line).contains("TOTAL: 8"));
    }
    
    @Test
    void testExecuteWithInvalidStat() {
        RollCommand uut = new RollCommand(repositoryBundle, commService, applicationContext, diceService);

        Output output = new Output();
        List<String> tokens = Arrays.asList("roll", "INVALID_STAT", "Basic");
        
        uut.execute(question, webSocketContext, tokens, output);
        
        // Check that the output contains the error message
        List<String> outputLines = output.toList();
        assertThat(outputLines).anyMatch(line -> line.contains("No such Stat."));
    }
    
    @Test
    void testExecuteWithInvalidEffort() {
        RollCommand uut = new RollCommand(repositoryBundle, commService, applicationContext, diceService);

        Output output = new Output();
        List<String> tokens = Arrays.asList("roll", "STR", "INVALID_EFFORT");
        
        uut.execute(question, webSocketContext, tokens, output);
        
        // Check that the output contains the error message
        List<String> outputLines = output.toList();
        assertThat(outputLines).anyMatch(line -> line.contains("No such Effort."));
    }
    
    @Test
    void testExecuteWithUltimateRoll() {
        RollCommand uut = new RollCommand(repositoryBundle, commService, applicationContext, diceService);

        // Set up character stats
        when(characterComponent.getStat(Stat.STR)).thenReturn(2);
        when(characterComponent.getEffort(Effort.BASIC)).thenReturn(3);

        // Mock dice service to return specific values
        DiceResult attemptRoll = new DiceResult(1, 2, 20);
        DiceResult effortRoll = new DiceResult(1, 3, 7);
        DiceResult ultimateRoll = new DiceResult(1, 0, 10);
        
        when(diceService.roll(1, 20, 2)).thenReturn(attemptRoll);
        when(diceService.roll(1, Effort.BASIC.getDie(), 3)).thenReturn(effortRoll);
        when(diceService.roll(1, Effort.ULTIMATE.getDie(), 0)).thenReturn(ultimateRoll);
        
        Output output = new Output();
        List<String> tokens = Arrays.asList("roll", "STR", "Basic");
        
        uut.execute(question, webSocketContext, tokens, output);
        
        // Check that the output contains the ultimate roll message
        List<String> outputLines = output.toList();
        assertThat(outputLines).anyMatch(line -> Command.stripColors(line).contains("ATTEMPT: 20 + 2 = 22 for STR"));
        assertThat(outputLines).anyMatch(line -> Command.stripColors(line).contains("EFFORT: 7 + 3 = 10 for Basic"));
        assertThat(outputLines).anyMatch(line -> Command.stripColors(line).contains("ULTIMATE: 10 + 0 = 10"));
        assertThat(outputLines).anyMatch(line -> Command.stripColors(line).contains("TOTAL: 20"));
    }
}
