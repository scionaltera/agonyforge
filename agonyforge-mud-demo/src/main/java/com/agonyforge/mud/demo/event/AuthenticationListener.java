package com.agonyforge.mud.demo.event;

import com.agonyforge.mud.models.dynamodb.impl.User;
import com.agonyforge.mud.models.dynamodb.repository.UserRepository;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.event.EventListener;
import org.springframework.security.authentication.event.AuthenticationSuccessEvent;
import org.springframework.security.core.Authentication;
import org.springframework.security.oauth2.core.oidc.user.DefaultOidcUser;
import org.springframework.stereotype.Component;

@Component
public class AuthenticationListener {
    private static final Logger LOGGER = LoggerFactory.getLogger(AuthenticationListener.class);

    private final UserRepository userRepository;

    @Autowired
    public AuthenticationListener(UserRepository userRepository) {
        this.userRepository = userRepository;
    }

    @EventListener
    public void onAuthenticationSuccessEvent(AuthenticationSuccessEvent event) {
        LOGGER.debug("Authentication success: {}", event.getAuthentication().getName());

        Authentication authentication = event.getAuthentication();
        DefaultOidcUser principal = (DefaultOidcUser) authentication.getPrincipal();
        User user = new User();

        user.setPrincipalName(principal.getName());
        user.setGivenName(principal.getGivenName());
        user.setEmailAddress(principal.getEmail());

        userRepository.save(user);
    }
}
