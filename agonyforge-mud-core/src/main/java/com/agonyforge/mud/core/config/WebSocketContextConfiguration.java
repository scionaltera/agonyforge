package com.agonyforge.mud.core.config;

import com.agonyforge.mud.core.web.controller.WebSocketContextAware;
import com.agonyforge.mud.core.web.model.WebSocketContext;
import org.springframework.boot.autoconfigure.condition.ConditionalOnMissingBean;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

@Configuration
public class WebSocketContextConfiguration {
    @Bean
    @ConditionalOnMissingBean
    public WebSocketContextAware webSocketContextAware() {
        return new WebSocketContextAware() {
            @Override
            public void setWebSocketContext(WebSocketContext webSocketContext) {
                // default implementation does nothing
                webSocketContext.getPrincipal().getName();
            }
        };
    }
}
