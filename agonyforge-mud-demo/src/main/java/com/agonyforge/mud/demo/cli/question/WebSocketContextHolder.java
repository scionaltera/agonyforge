package com.agonyforge.mud.demo.cli.question;

import com.agonyforge.mud.core.web.controller.WebSocketContextAware;
import com.agonyforge.mud.core.web.model.WebSocketContext;
import org.springframework.data.domain.AuditorAware;
import org.springframework.stereotype.Component;

import java.util.Optional;

@Component
public class WebSocketContextHolder implements WebSocketContextAware, AuditorAware<String> {
    private String currentUser;

    @Override
    public void setWebSocketContext(WebSocketContext webSocketContext) {
        currentUser = webSocketContext.getPrincipal().getName();
    }

    @Override
    public Optional<String> getCurrentAuditor() {
        return Optional.ofNullable(currentUser);
    }
}
