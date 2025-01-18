package com.agonyforge.mud.demo.cli.question.login;

import com.agonyforge.mud.core.cli.Question;
import com.agonyforge.mud.core.cli.Response;
import com.agonyforge.mud.core.web.model.Input;
import com.agonyforge.mud.core.web.model.Output;
import com.agonyforge.mud.core.web.model.WebSocketContext;
import com.agonyforge.mud.demo.cli.RepositoryBundle;
import com.agonyforge.mud.demo.model.constant.WearSlot;
import com.agonyforge.mud.demo.model.impl.MudCharacter;
import com.agonyforge.mud.demo.model.impl.Role;
import com.agonyforge.mud.demo.model.repository.MudCharacterRepository;
import com.agonyforge.mud.demo.model.repository.MudItemRepository;
import com.agonyforge.mud.demo.model.repository.RoleRepository;
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

import java.security.Principal;
import java.util.Optional;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertTrue;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.eq;
import static org.mockito.Mockito.lenient;
import static org.mockito.Mockito.never;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.verifyNoInteractions;
import static org.mockito.Mockito.when;

@ExtendWith(MockitoExtension.class)
public class CharacterNameQuestionTest {
    @Mock
    private RepositoryBundle repositoryBundle;

    @Mock
    private RoleRepository roleRepository;

    @Mock
    private Principal principal;

    @Mock
    private Question question;

    @Mock
    private Role role;

    @Mock
    private ApplicationContext applicationContext;

    @Mock
    private MudCharacterRepository characterRepository;

    @Mock
    private MudItemRepository itemRepository;

    @Mock
    private WebSocketContext webSocketContext;

    @Mock
    private MudCharacter ch;

    @Captor
    private ArgumentCaptor<MudCharacter> characterCaptor;

    @BeforeEach
    void setUp() {
        lenient().when(repositoryBundle.getCharacterRepository()).thenReturn(characterRepository);
        lenient().when(repositoryBundle.getItemRepository()).thenReturn(itemRepository);
    }

    @Test
    void testPrompt() {
        CharacterNameQuestion uut = new CharacterNameQuestion(applicationContext, repositoryBundle, roleRepository);
        Output result = uut.prompt(webSocketContext);

        assertEquals(1, result.getOutput().size());
        assertEquals("[default]By what name do you wish to be known? ", result.getOutput().get(0));

        verifyNoInteractions(webSocketContext);
    }

    @ParameterizedTest
    @ValueSource(strings = {
        "Scion",
        "Sc",
        "Scionscionsc",
        "SCION"
    })
    void testAnswer(String userInput) {
        when(applicationContext.getBean(eq("characterPronounQuestion"), eq(Question.class))).thenReturn(question);
        when(principal.getName()).thenReturn("principal");
        when(webSocketContext.getPrincipal()).thenReturn(principal);
        when(roleRepository.findByName(eq("Player"))).thenReturn(Optional.of(role));
        when(characterRepository.save(any(MudCharacter.class))).thenAnswer(i -> {
            MudCharacter saved = i.getArgument(0);
            saved.setId(1L);
            return saved;
        });

        CharacterNameQuestion uut = new CharacterNameQuestion(applicationContext, repositoryBundle, roleRepository);
        Input input = new Input(userInput);
        Response result = uut.answer(webSocketContext, input);
        Output output = result.getFeedback().orElseThrow();

        assertEquals(question, result.getNext());

        assertEquals(1, output.getOutput().size());
        assertEquals(String.format("[default]Hello, [white]%s[default]!", userInput), output.getOutput().get(0));

        verify(characterRepository).save(characterCaptor.capture());

        MudCharacter ch = characterCaptor.getValue();

        assertEquals(1L, ch.getId());
        assertEquals(principal.getName(), ch.getPlayer().getUsername());
        assertEquals(userInput, ch.getCharacter().getName());
        assertTrue(ch.getCharacter().getWearSlots().contains(WearSlot.HEAD));
    }

    @ParameterizedTest
    @ValueSource(strings = {
        "",
        "S"
    })
    void testAnswerTooShort(String userInput) {
        CharacterNameQuestion uut = new CharacterNameQuestion(applicationContext, repositoryBundle, roleRepository);
        Input input = new Input(userInput);
        Response result = uut.answer(webSocketContext, input);
        Output output = result.getFeedback().orElseThrow();

        assertEquals(uut, result.getNext());

        assertEquals(1, output.getOutput().size());
        assertEquals("[red]Names need to be at least two letters in length.", output.getOutput().get(0));

        verify(webSocketContext, never()).getAttributes();
    }

    @Test
    void testAnswerTooLong() {
        CharacterNameQuestion uut = new CharacterNameQuestion(applicationContext, repositoryBundle, roleRepository);
        Input input = new Input("S".repeat(13));
        Response result = uut.answer(webSocketContext, input);
        Output output = result.getFeedback().orElseThrow();

        assertEquals(uut, result.getNext());

        assertEquals(1, output.getOutput().size());
        assertEquals("[red]Names need to be 12 or fewer letters in length.", output.getOutput().get(0));

        verify(webSocketContext, never()).getAttributes();
    }

    @ParameterizedTest
    @ValueSource(strings = {
        "11111",
        "Sc1on"
    })
    void testAnswerInvalidLetters(String userInput) {
        CharacterNameQuestion uut = new CharacterNameQuestion(applicationContext, repositoryBundle, roleRepository);
        Response result = uut.answer(webSocketContext, new Input(userInput));
        Output output = result.getFeedback().orElseThrow();

        assertEquals(uut, result.getNext());

        assertEquals(1, output.getOutput().size());
        assertEquals("[red]Names may only have letters in them.", output.getOutput().get(0));

        verify(webSocketContext, never()).getAttributes();
    }

    @Test
    void testAnswerNoCaps() {
        String userInput = "scion";
        CharacterNameQuestion uut = new CharacterNameQuestion(applicationContext, repositoryBundle, roleRepository);
        Response result = uut.answer(webSocketContext, new Input(userInput));
        Output output = result.getFeedback().orElseThrow();

        assertEquals(uut, result.getNext());

        assertEquals(1, output.getOutput().size());
        assertEquals("[red]Names must begin with a capital letter.", output.getOutput().get(0));

        verify(webSocketContext, never()).getAttributes();
    }

    @Test
    void testAnswerNotUnique() {
        CharacterNameQuestion uut = new CharacterNameQuestion(applicationContext, repositoryBundle, roleRepository);
        Input input = new Input("Scion");

        when(characterRepository.findByCharacterName(eq("Scion"))).thenReturn(Optional.of(ch));

        Response result = uut.answer(webSocketContext, input);
        Output output = result.getFeedback().orElseThrow();

        assertEquals(uut, result.getNext());

        assertEquals(1, output.getOutput().size());
        assertEquals("[red]Somebody else is already using that name. Please try a different one.", output.getOutput().get(0));

        verify(webSocketContext, never()).getAttributes();
    }
}
