package com.agonyforge.mud.core.web.controller;

import com.agonyforge.mud.core.web.model.WebSocketContext;

public interface WebSocketContextAware {
    void setWebSocketContext(WebSocketContext webSocketContext);
}
