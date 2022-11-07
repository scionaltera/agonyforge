package com.agonyforge.mud.core.service;

import com.agonyforge.mud.core.web.model.Output;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.messaging.simp.SimpMessagingTemplate;
import org.springframework.stereotype.Component;

import java.security.Principal;

/*
 * TODO Not writing tests for this one yet because it's very likely to change.
 */
@Component
public class EchoService {
    private final SimpMessagingTemplate simpMessagingTemplate;
    private final InMemoryUserRepository userRepository;

    @Autowired
    public EchoService(SimpMessagingTemplate simpMessagingTemplate,
                       InMemoryUserRepository userRepository) {
        this.simpMessagingTemplate = simpMessagingTemplate;
        this.userRepository = userRepository;
    }

    // echo to everyone except principal
    public void echo(Principal principal, Output message) {
        userRepository.getWsSessionNames()
                .stream()
                .filter(name -> !name.equals(principal.getName()))
                .forEach(name -> simpMessagingTemplate.convertAndSendToUser(
                    name,
                    "/queue/output",
                    message));
    }
}
