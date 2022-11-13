package com.agonyforge.mud.demo.event;

import com.agonyforge.mud.models.dynamodb.impl.User;
import com.agonyforge.mud.models.dynamodb.repository.UserRepository;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.ArgumentCaptor;
import org.mockito.Captor;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.security.authentication.event.AuthenticationSuccessEvent;
import org.springframework.security.core.Authentication;
import org.springframework.security.oauth2.core.oidc.user.DefaultOidcUser;

import static org.junit.jupiter.api.Assertions.assertEquals;
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
    private UserRepository userRepository;

    @Captor
    private ArgumentCaptor<User> userCaptor;

    @Test
    void testOnEvent() {
        String principalName = "principal";
        String givenName = "given";
        String email = "e@mail.test";

        when(successEvent.getAuthentication()).thenReturn(authentication);
        when(authentication.getPrincipal()).thenReturn(principal);
        when(principal.getName()).thenReturn(principalName);
        when(principal.getGivenName()).thenReturn(givenName);
        when(principal.getEmail()).thenReturn(email);

        AuthenticationListener uut = new AuthenticationListener(userRepository);

        uut.onAuthenticationSuccessEvent(successEvent);

        verify(userRepository).save(userCaptor.capture());

        User user = userCaptor.getValue();

        assertEquals(principalName, user.getPrincipalName());
        assertEquals(givenName, user.getGivenName());
        assertEquals(email, user.getEmailAddress());
    }
}
