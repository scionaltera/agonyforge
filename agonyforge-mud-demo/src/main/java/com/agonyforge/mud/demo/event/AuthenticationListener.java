package com.agonyforge.mud.demo.event;

import com.agonyforge.mud.demo.model.impl.User;
import com.agonyforge.mud.demo.model.impl.UserSession;
import com.agonyforge.mud.demo.model.repository.UserRepository;
import com.agonyforge.mud.demo.model.repository.UserSessionRepository;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.event.EventListener;
import org.springframework.security.authentication.event.AuthenticationSuccessEvent;
import org.springframework.security.core.Authentication;
import org.springframework.security.oauth2.core.user.DefaultOAuth2User;
import org.springframework.security.web.authentication.WebAuthenticationDetails;
import org.springframework.stereotype.Component;

@Component
public class AuthenticationListener {
    private static final Logger LOGGER = LoggerFactory.getLogger(AuthenticationListener.class);

    private final UserRepository userRepository;
    private final UserSessionRepository userSessionRepository;

    @Autowired
    public AuthenticationListener(UserRepository userRepository,
                                  UserSessionRepository userSessionRepository) {
        this.userRepository = userRepository;
        this.userSessionRepository =  userSessionRepository;
    }

    @EventListener
    public void onAuthenticationSuccessEvent(AuthenticationSuccessEvent event) {
        LOGGER.debug("Authentication success: {}", event.getAuthentication().getName());

        Authentication authentication = event.getAuthentication();
        DefaultOAuth2User principal = (DefaultOAuth2User) authentication.getPrincipal();
        WebAuthenticationDetails details = (WebAuthenticationDetails) authentication.getDetails();
        User user = new User();
        UserSession session = new UserSession();

        user.setPrincipalName(principal.getName());
        user.setGivenName(principal.getAttribute("name"));
        user.setEmailAddress(principal.getAttribute("email"));

        session.setPrincipalName(principal.getName());
        session.setRemoteIpAddress(details.getRemoteAddress());

        userRepository.save(user);
        userSessionRepository.save(session);
    }
}
