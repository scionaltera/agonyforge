package com.agonyforge.mud.demo.cli.question.ingame;

import com.agonyforge.mud.core.cli.Question;
import com.agonyforge.mud.core.cli.Response;
import com.agonyforge.mud.core.web.model.Input;
import com.agonyforge.mud.core.web.model.Output;
import com.agonyforge.mud.core.web.model.WebSocketContext;
import com.agonyforge.mud.demo.cli.RepositoryBundle;
import com.agonyforge.mud.demo.cli.command.Command;
import com.agonyforge.mud.demo.cli.question.CommandException;
import com.agonyforge.mud.demo.model.impl.*;
import com.agonyforge.mud.demo.model.repository.CommandRepository;
import com.agonyforge.mud.demo.model.repository.MudCharacterRepository;
import com.agonyforge.mud.demo.model.repository.MudItemRepository;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.junit.jupiter.params.ParameterizedTest;
import org.junit.jupiter.params.provider.ValueSource;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.context.ApplicationContext;
import org.springframework.data.domain.Sort;

import java.util.*;

import static com.agonyforge.mud.core.config.SessionConfiguration.MUD_CHARACTER;
import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertNotNull;
import static org.junit.jupiter.api.Assertions.assertTrue;
import static org.mockito.ArgumentMatchers.*;
import static org.mockito.Mockito.lenient;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.never;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

@ExtendWith(MockitoExtension.class)
public class CommandQuestionTest {
    @Mock
    private RepositoryBundle repositoryBundle;

    @Mock
    private ApplicationContext applicationContext;

    @Mock
    private MudCharacterRepository characterRepository;

    @Mock
    private MudItemRepository itemRepository;

    @Mock
    private CommandRepository commandRepository;

    @Mock
    private PlayerComponent playerComponent;

    @Mock
    private CharacterComponent characterComponent;

    @Mock
    private Role role;

    @Mock
    private MudCharacter ch;

    @Mock
    private WebSocketContext webSocketContext;

    @Mock
    private Command command;

    @Mock
    private Question question;

    private final Random random = new Random();

    @BeforeEach
    void setUp() {
        lenient().when(repositoryBundle.getCharacterRepository()).thenReturn(characterRepository);
        lenient().when(repositoryBundle.getItemRepository()).thenReturn(itemRepository);
    }

    @Test
    void testPrompt() {
        Long chId = random.nextLong();
        when(webSocketContext.getAttributes()).thenReturn(Map.of(
            MUD_CHARACTER, chId
        ));

        when(ch.getCharacter()).thenReturn(characterComponent);
        when(characterRepository.findById(eq(chId))).thenReturn(Optional.of(ch));

        CommandQuestion uut = new CommandQuestion(applicationContext, repositoryBundle, commandRepository);
        Output output = uut.prompt(webSocketContext);

        assertEquals(2, output.getOutput().size());
        assertEquals("", output.getOutput().get(0));
        assertEquals("[green]null[default]> ", output.getOutput().get(1));
    }

    @Test
    void testPromptNoCharacter() {
        CommandQuestion uut = new CommandQuestion(applicationContext, repositoryBundle, commandRepository);
        Output output = uut.prompt(webSocketContext);

        assertEquals(3, output.getOutput().size());
        assertEquals("[red]Unable to find your character! The error has been reported.", output.getOutput().get(0));
        assertEquals("", output.getOutput().get(1));
        assertEquals("[default]> ", output.getOutput().get(2));
    }

    @ParameterizedTest
    @ValueSource(strings = {
        "t",
        "te",
        "tes",
        "test"
    })
    void testAnswer() {
        CommandReference commandReference = mock(CommandReference.class);

        when(commandReference.getBeanName()).thenReturn("testCommand");

        when(role.getCommands()).thenReturn(Set.of(commandReference));
        when(ch.getPlayer()).thenReturn(playerComponent);
        when(ch.getPlayer().getRoles()).thenReturn(Set.of(role));
        when(characterRepository.findById(any())).thenReturn(Optional.of(ch));
        when(commandRepository.findFirstByNameStartingWith(eq("TEST"), eq(Sort.by(Sort.Order.asc("priority"))))).thenReturn(Optional.of(commandReference));
        when(applicationContext.getBean(eq("testCommand"), eq(Command.class))).thenReturn(command);
        when(command.execute(any(Question.class), any(WebSocketContext.class), anyList(), any(Input.class), any(Output.class))).thenReturn(question);

        CommandQuestion uut = new CommandQuestion(applicationContext, repositoryBundle, commandRepository);
        Response result = uut.answer(webSocketContext, new Input("test"));
        Output output = result.getFeedback().orElseThrow();

        assertEquals(question, result.getNext());
        assertNotNull(output);

        verify(commandRepository).findFirstByNameStartingWith(eq("TEST"), eq(Sort.by(Sort.Order.asc("priority"))));
        verify(applicationContext).getBean(eq("testCommand"), eq(Command.class));
    }

    @Test
    void testAnswerNoRole() {
        CommandReference commandReference = mock(CommandReference.class);

        when(commandReference.getBeanName()).thenReturn("testCommand");

        when(ch.getPlayer()).thenReturn(playerComponent);
        when(ch.getCharacter()).thenReturn(characterComponent);
        when(characterRepository.findById(any())).thenReturn(Optional.of(ch));
        when(commandRepository.findFirstByNameStartingWith(eq("TEST"), eq(Sort.by(Sort.Order.asc("priority"))))).thenReturn(Optional.of(commandReference));
        when(applicationContext.getBean(eq("testCommand"), eq(Command.class))).thenReturn(command);

        CommandQuestion uut = new CommandQuestion(applicationContext, repositoryBundle, commandRepository);
        Response result = uut.answer(webSocketContext, new Input("test"));
        Output output = result.getFeedback().orElseThrow();

        assertEquals(uut, result.getNext());
        assertEquals(1, output.getOutput().size());
        assertEquals("[default]Huh?", output.getOutput().get(0));

        verify(commandRepository).findFirstByNameStartingWith(eq("TEST"), eq(Sort.by(Sort.Order.asc("priority"))));
        verify(applicationContext).getBean(any(), eq(Command.class));
        verify(command, never()).execute(any(), any(), anyList(), any(), any());
    }

    @Test
    void testAnswerCommandException() {
        CommandReference commandReference = mock(CommandReference.class);

        when(commandReference.getBeanName()).thenReturn("testCommand");

        when(commandRepository.findFirstByNameStartingWith(eq("TEST"), eq(Sort.by(Sort.Order.asc("priority"))))).thenReturn(Optional.of(commandReference));
        when(applicationContext.getBean(eq("testCommand"), eq(Command.class))).thenThrow(new CommandException("oops!"));

        CommandQuestion uut = new CommandQuestion(applicationContext, repositoryBundle, commandRepository);
        Response result = uut.answer(webSocketContext, new Input("test"));
        Output output = result.getFeedback().orElseThrow();

        assertEquals(uut, result.getNext());
        assertEquals(1, output.getOutput().size());
        assertTrue(output.getOutput().get(0).contains("[red]"));

        verify(commandRepository).findFirstByNameStartingWith(eq("TEST"), eq(Sort.by(Sort.Order.asc("priority"))));
        verify(applicationContext).getBean(eq("testCommand"), eq(Command.class));
    }

    @Test
    void testAnswerNoBean() {
        CommandQuestion uut = new CommandQuestion(applicationContext, repositoryBundle, commandRepository);
        Response result = uut.answer(webSocketContext, new Input("test"));
        Output output = result.getFeedback().orElseThrow();

        assertEquals(uut, result.getNext());
        assertEquals(1, output.getOutput().size());
        assertEquals("[default]Huh?", output.getOutput().get(0));

        verify(commandRepository).findFirstByNameStartingWith(eq("TEST"), eq(Sort.by(Sort.Order.asc("priority"))));
        verify(applicationContext, never()).getBean(eq("testCommand"), eq(Command.class));
    }

    @Test
    void testAnswerNotFound() {
        CommandQuestion uut = new CommandQuestion(applicationContext, repositoryBundle, commandRepository);
        Response result = uut.answer(webSocketContext, new Input("notfound"));
        Output output = result.getFeedback().orElseThrow();

        assertEquals(uut, result.getNext());
        assertEquals(1, output.getOutput().size());
        assertEquals("[default]Huh?", output.getOutput().get(0));

        verify(commandRepository).findFirstByNameStartingWith(eq("NOTFOUND"), eq(Sort.by(Sort.Order.asc("priority"))));
        verify(applicationContext, never()).getBean(any(), eq(Command.class));
    }

    @ParameterizedTest
    @ValueSource(strings = {
        "",
        " ",
        "  ",
        "\t",
        "\n"
    })
    void testAnswerBlankInput(String input) {
        CommandQuestion uut = new CommandQuestion(applicationContext, repositoryBundle, commandRepository);
        Response result = uut.answer(webSocketContext, new Input(input));
        Output output = result.getFeedback().orElseThrow();

        assertEquals(uut, result.getNext());
        assertEquals(0, output.getOutput().size());

        verify(commandRepository, never()).findFirstByNameStartingWith(anyString(), eq(Sort.by(Sort.Order.asc("priority"))));
        verify(applicationContext, never()).getBean(anyString(), eq(Command.class));
    }

    @Test
    void testAnswerQuotedTokens() {
        CommandReference commandReference = mock(CommandReference.class);

        when(commandReference.getBeanName()).thenReturn("testCommand");

        when(role.getCommands()).thenReturn(Set.of(commandReference));
        when(ch.getPlayer()).thenReturn(playerComponent);
        when(ch.getPlayer().getRoles()).thenReturn(Set.of(role));
        when(characterRepository.findById(any())).thenReturn(Optional.of(ch));
        when(commandRepository.findFirstByNameStartingWith(eq("QUOTED STRING"), eq(Sort.by(Sort.Order.asc("priority"))))).thenReturn(Optional.of(commandReference));
        when(applicationContext.getBean(eq("testCommand"), eq(Command.class))).thenReturn(command);
        when(command.execute(any(Question.class), any(WebSocketContext.class), anyList(), any(Input.class), any(Output.class))).thenReturn(question);

        CommandQuestion uut = new CommandQuestion(applicationContext, repositoryBundle, commandRepository);
        Response result = uut.answer(webSocketContext, new Input("\"quoted string\""));
        Output output = result.getFeedback().orElseThrow();

        assertEquals(question, result.getNext());
        assertNotNull(output);

        verify(commandRepository).findFirstByNameStartingWith(eq("QUOTED STRING"), eq(Sort.by(Sort.Order.asc("priority"))));
        verify(applicationContext).getBean(eq("testCommand"), eq(Command.class));
    }
}
