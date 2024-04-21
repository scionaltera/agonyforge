package com.agonyforge.mud.demo.event;

import com.agonyforge.mud.demo.model.impl.User;
import com.agonyforge.mud.demo.model.impl.UserSession;
import com.agonyforge.mud.demo.model.repository.UserRepository;
import com.agonyforge.mud.demo.model.repository.UserSessionRepository;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.ArgumentCaptor;
import org.mockito.Captor;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.security.authentication.event.AuthenticationSuccessEvent;
import org.springframework.security.core.Authentication;
import org.springframework.security.oauth2.core.oidc.user.DefaultOidcUser;
import org.springframework.security.web.authentication.WebAuthenticationDetails;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.mockito.ArgumentMatchers.eq;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

@ExtendWith(MockitoExtension.class)
public class AuthenticationListenerTest {
    @Mock
    private AuthenticationSuccessEvent successEvent;

    @Mock
    private Authentication authentication;

    @Mock
    private DefaultOidcUser principal;

    @Mock
    private WebAuthenticationDetails details;

    @Mock
    private UserRepository userRepository;

    @Mock
    private UserSessionRepository userSessionRepository;

    @Captor
    private ArgumentCaptor<User> userCaptor;

    @Captor
    private ArgumentCaptor<UserSession> userSessionCaptor;

    @Test
    void testOnEvent() {
        String principalName = "principal";
        String givenName = "given";
        String email = "e@mail.test";
        String remoteIpAddress = "999.888.777.666";

        when(successEvent.getAuthentication()).thenReturn(authentication);
        when(authentication.getPrincipal()).thenReturn(principal);
        when(authentication.getDetails()).thenReturn(details);
        when(principal.getName()).thenReturn(principalName);
        when(principal.getAttribute(eq("name"))).thenReturn(givenName);
        when(principal.getAttribute(eq("email"))).thenReturn(email);
        when(details.getRemoteAddress()).thenReturn(remoteIpAddress);

        AuthenticationListener uut = new AuthenticationListener(userRepository, userSessionRepository);

        uut.onAuthenticationSuccessEvent(successEvent);

        verify(userRepository).save(userCaptor.capture());
        verify(userSessionRepository).save(userSessionCaptor.capture());

        User user = userCaptor.getValue();

        assertEquals(principalName, user.getPrincipalName());
        assertEquals(givenName, user.getGivenName());
        assertEquals(email, user.getEmailAddress());

        UserSession session = userSessionCaptor.getValue();

        assertEquals(principalName, session.getPrincipalName());
        assertEquals(remoteIpAddress, session.getRemoteIpAddress());
    }
}
