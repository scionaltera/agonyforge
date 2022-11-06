package com.agonyforge.mud.demo.cli;

import com.agonyforge.mud.core.cli.Question;
import com.agonyforge.mud.core.cli.Response;
import com.agonyforge.mud.core.web.model.Input;
import com.agonyforge.mud.core.web.model.Output;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.junit.jupiter.params.ParameterizedTest;
import org.junit.jupiter.params.provider.CsvSource;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContext;
import org.springframework.security.web.authentication.WebAuthenticationDetails;
import org.springframework.session.FindByIndexNameSessionRepository;
import org.springframework.session.Session;

import java.security.Principal;
import java.util.List;
import java.util.Map;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.eq;
import static org.mockito.Mockito.never;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

@ExtendWith(MockitoExtension.class)
public class MenuQuestionTest {
    @Mock
    private Principal principal;

    @Mock
    private FindByIndexNameSessionRepository<Session> sessionRepository;

    @Mock
    private Session session;

    @Mock
    private SecurityContext securityContext;

    @Mock
    private Authentication authentication;

    @Mock
    private WebAuthenticationDetails details;

    @Mock
    private Question question;

    @Test
    void testPrompt() {
        MenuQuestion uut = new MenuQuestion(sessionRepository);
        uut.setNextQuestion(question);

        Output result = uut.prompt(principal);

        assertEquals(10, result.getOutput().size());
        assertEquals("[dcyan]*************", result.getOutput().get(0));
        assertEquals("[dcyan]* [cyan]    Demo Menu    [dcyan] *", result.getOutput().get(1));
        assertEquals("[dcyan]*************", result.getOutput().get(2));
        assertEquals("[cyan]S[dcyan]) [cyan]Session ID", result.getOutput().get(3));
        assertEquals("[cyan]F[dcyan]) [cyan]Foo", result.getOutput().get(4));
        assertEquals("[cyan]B[dcyan]) [cyan]Bar", result.getOutput().get(5));
        assertEquals("[cyan]C[dcyan]) [cyan]Crazy Town", result.getOutput().get(6));
        assertEquals("[cyan]Z[dcyan]) [cyan]Zed's Dead, Baby", result.getOutput().get(7));
        assertEquals("[cyan]P[dcyan]) [cyan]Puerto Rico", result.getOutput().get(8));
        assertEquals("[cyan]Please [dcyan]make your selection[cyan]: ", result.getOutput().get(9));

    }

    @Test
    void testAnswerSNoSession() {
        MenuQuestion uut = new MenuQuestion(sessionRepository);
        uut.setNextQuestion(question);

        Input input = new Input("S");
        Response result = uut.answer(principal, input);

        assertEquals(uut, result.getNext());

        List<String> lines = result.getFeedback().orElseThrow().getOutput();
        assertEquals(1, lines.size());
        assertEquals("Your sessions:", lines.get(0));

        verify(sessionRepository).findByPrincipalName(any());
    }

    @Test
    void testAnswerSWithSession() {
        when(details.getRemoteAddress()).thenReturn("900.900.900.900");
        when(authentication.getDetails()).thenReturn(details);
        when(securityContext.getAuthentication()).thenReturn(authentication);
        when(session.getAttribute(eq("SPRING_SECURITY_CONTEXT"))).thenReturn(securityContext);
        when(session.getAttributeOrDefault(eq("MENU.DEMO"), eq(1))).thenReturn(9000);
        when(sessionRepository.findByPrincipalName(any())).thenReturn(Map.of("foo", session));

        MenuQuestion uut = new MenuQuestion(sessionRepository);
        uut.setNextQuestion(question);

        Input input = new Input("S");
        Response result = uut.answer(principal, input);

        assertEquals(uut, result.getNext());

        List<String> lines = result.getFeedback().orElseThrow().getOutput();
        assertEquals(2, lines.size());
        assertEquals("Your sessions:", lines.get(0));
        assertEquals("foo @ 900.900.900.900 (9000 times)", lines.get(1));

        verify(sessionRepository).findByPrincipalName(any());
        verify(session).setAttribute(eq("MENU.DEMO"), eq(9001));
    }

    @ParameterizedTest
    @CsvSource({
        "F,Bar!",
        "B,Baz!",
        "C,I'm the only sane one around here.",
        "Z,Royale with cheese.",
        "P,Rico Suave!"
    })
    void testAnswers(String letter, String expected) {
        MenuQuestion uut = new MenuQuestion(sessionRepository);
        uut.setNextQuestion(question);

        Input input = new Input(letter);
        Response result = uut.answer(principal, input);

        assertEquals(question, result.getNext());

        List<String> lines = result.getFeedback().orElseThrow().getOutput();
        assertEquals(1, lines.size());
        assertEquals(expected, lines.get(0));

        verify(sessionRepository, never()).findByPrincipalName(any());
    }

    @Test
    void testAnswerInvalid() {
        MenuQuestion uut = new MenuQuestion(sessionRepository);
        uut.setNextQuestion(question);

        Input input = new Input("A");
        Response result = uut.answer(principal, input);

        assertEquals(uut, result.getNext());

        List<String> lines = result.getFeedback().orElseThrow().getOutput();
        assertEquals(1, lines.size());
        assertEquals("Please choose one of the menu options.", lines.get(0));

        verify(sessionRepository, never()).findByPrincipalName(any());
    }
}
