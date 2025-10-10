package com.agonyforge.mud.demo.cli.command;

import com.agonyforge.mud.core.cli.Question;
import com.agonyforge.mud.demo.cli.RepositoryBundle;
import com.agonyforge.mud.demo.model.constant.Effort;
import com.agonyforge.mud.demo.model.constant.Stat;
import com.agonyforge.mud.demo.model.impl.MudCharacter;
import com.agonyforge.mud.demo.model.impl.CharacterComponent;
import com.agonyforge.mud.demo.service.CommService;
import com.agonyforge.mud.demo.model.repository.MudCharacterRepository;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import com.agonyforge.mud.core.service.dice.DiceResult;
import com.agonyforge.mud.core.service.dice.DiceService;
import com.agonyforge.mud.core.web.model.Output;
import com.agonyforge.mud.core.web.model.WebSocketContext;
import com.agonyforge.mud.demo.cli.command.RollCommand;
import com.agonyforge.mud.demo.service.CommService;
import com.agonyforge.mud.demo.model.impl.MudCharacter;
import com.agonyforge.mud.demo.model.impl.CharacterComponent;
import com.agonyforge.mud.demo.model.repository.MudCharacterRepository;
import org.springframework.context.ApplicationContext;
import org.springframework.context.annotation.AnnotationConfigApplicationContext;

import java.util.Arrays;
import java.util.List;
import java.util.Map;
import java.util.Optional;

import static com.agonyforge.mud.core.config.SessionConfiguration.MUD_CHARACTER;
import static org.assertj.core.api.Assertions.assertThat;
import static org.mockito.ArgumentMatchers.anyInt;
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
    
    @InjectMocks
    private RollCommand rollCommand;
    
    private MudCharacter character;
    private CharacterComponent characterComponent;
    
    @BeforeEach
    void setUp() {
        character = new MudCharacter();
        characterComponent = new CharacterComponent();
        character.setCharacter(characterComponent);
        
        when(repositoryBundle.getCharacterRepository()).thenReturn(characterRepository);
        when(webSocketContext.getAttributes()).thenReturn(Map.of(MUD_CHARACTER, 1L));
        when(characterRepository.findById(1L)).thenReturn(Optional.of(character));
    }
    
    @Test
    void testExecuteWithValidInput() {
        // Set up character stats
        characterComponent.setStat(Stat.STR, 10);
        characterComponent.setEffort(Effort.BASIC, 5);
        
        // Mock dice service to return specific values
        DiceResult attemptRoll = new DiceResult(20, 0);
        attemptRoll.addRoll(20);
        DiceResult effortRoll = new DiceResult(20, 0);
        effortRoll.addRoll(10);
        
        when(diceService.roll(1, 20, 10)).thenReturn(attemptRoll);
        when(diceService.roll(1, Effort.BASIC.getDie(), 5)).thenReturn(effortRoll);
        
        Output output = new Output();
        List<String> tokens = Arrays.asList("roll", "STR", "Basic");
        
        rollCommand.execute(question, webSocketContext, tokens, output);
        
        // Check that the output contains the expected messages
        List<String> outputLines = output.toList();
        assertThat(outputLines).anyMatch(line -> line.contains("ATTEMPT: 20 + 10 = 30 for STR"));
        assertThat(outputLines).anyMatch(line -> line.contains("EFFORT: 10 + 5 = 15 for Basic"));
        assertThat(outputLines).anyMatch(line -> line.contains("TOTAL: 25"));
    }
    
    @Test
    void testExecuteWithInvalidStat() {
        Output output = new Output();
        List<String> tokens = Arrays.asList("roll", "INVALID_STAT", "Basic");
        
        rollCommand.execute(question, webSocketContext, tokens, output);
        
        // Check that the output contains the error message
        List<String> outputLines = output.toList();
        assertThat(outputLines).anyMatch(line -> line.contains("No such Stat."));
    }
    
    @Test
    void testExecuteWithInvalidEffort() {
        Output output = new Output();
        List<String> tokens = Arrays.asList("roll", "STR", "INVALID_EFFORT");
        
        rollCommand.execute(question, webSocketContext, tokens, output);
        
        // Check that the output contains the error message
        List<String> outputLines = output.toList();
        assertThat(outputLines).anyMatch(line -> line.contains("No such Effort."));
    }
    
    @Test
    void testExecuteWithUltimateRoll() {
        // Set up character stats
        characterComponent.setStat(Stat.STR, 10);
        characterComponent.setEffort(Effort.BASIC, 5);
        
        // Mock dice service to return 20 for the attempt roll
        DiceResult attemptRoll = new DiceResult(20, 0);
        attemptRoll.addRoll(20);
        DiceResult effortRoll = new DiceResult(20, 0);
        effortRoll.addRoll(10);
        DiceResult ultimateRoll = new DiceResult(20, 0);
        ultimateRoll.addRoll(15);
        
        when(diceService.roll(1, 20, 10)).thenReturn(attemptRoll);
        when(diceService.roll(1, Effort.BASIC.getDie(), 5)).thenReturn(effortRoll);
        when(diceService.roll(1, Effort.ULTIMATE.getDie(), 0)).thenReturn(ultimateRoll);
        
        Output output = new Output();
        List<String> tokens = Arrays.asList("roll", "STR", "Basic");
        
        rollCommand.execute(question, webSocketContext, tokens, output);
        
        // Check that the output contains the ultimate roll message
        List<String> outputLines = output.toList();
        assertThat(outputLines).anyMatch(line -> line.contains("ULTIMATE: 15 + 0 = 15"));
        assertThat(outputLines).anyMatch(line -> line.contains("TOTAL: 30"));
    }
}
