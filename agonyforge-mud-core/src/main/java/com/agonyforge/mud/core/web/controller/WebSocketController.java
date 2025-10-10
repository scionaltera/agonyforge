package com.agonyforge.mud.core.web.controller;

import com.agonyforge.mud.core.cli.OutputLoader;
import com.agonyforge.mud.core.cli.Question;
import com.agonyforge.mud.core.service.InputProcessingService;
import com.agonyforge.mud.core.web.model.Input;
import com.agonyforge.mud.core.web.model.Output;
import com.agonyforge.mud.core.web.model.WebSocketContext;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.context.ApplicationContext;
import org.springframework.messaging.handler.annotation.Headers;
import org.springframework.messaging.handler.annotation.MessageMapping;
import org.springframework.messaging.simp.SimpMessageHeaderAccessor;
import org.springframework.messaging.simp.annotation.SendToUser;
import org.springframework.messaging.simp.annotation.SubscribeMapping;
import org.springframework.stereotype.Controller;
import org.springframework.transaction.annotation.Transactional;

import java.io.IOException;
import java.util.Map;

import static com.agonyforge.mud.core.config.RemoteIpHandshakeInterceptor.SESSION_REMOTE_IP_KEY;
import static com.agonyforge.mud.core.config.SessionConfiguration.MUD_QUESTION;
import static org.springframework.web.socket.server.support.HttpSessionHandshakeInterceptor.HTTP_SESSION_ID_ATTR_NAME;

@Transactional
@Controller
public class WebSocketController {
    private static final Logger LOGGER = LoggerFactory.getLogger(WebSocketController.class);

    private final ApplicationContext applicationContext;
    private final WebSocketContextAware webSocketContextAware;
    private final InputProcessingService inputProcessingService;
    private final Question initialQuestion;
    private Output greeting;

    @Autowired
    public WebSocketController(ApplicationContext applicationContext,
                               WebSocketContextAware webSocketContextAware,
                               InputProcessingService inputProcessingService,
                               @Qualifier("initialQuestion") Question initialQuestion) {
        this.applicationContext = applicationContext;
        this.webSocketContextAware = webSocketContextAware;
        this.inputProcessingService = inputProcessingService;
        this.initialQuestion = initialQuestion;

        try {
            greeting = OutputLoader.loadTextFile("greeting.txt");
        } catch (IOException e) {
            LOGGER.warn("No greeting.txt found in classpath.");
            greeting = new Output("Welcome!");
        }
    }

    @SubscribeMapping("/queue/output")
    public Output onSubscribe(@Headers Map<String, Object> headers) {
        WebSocketContext wsContext;

        try {
            wsContext = WebSocketContext.build(headers);
            webSocketContextAware.setWebSocketContext(wsContext);
        } catch (IllegalStateException e) {
            LOGGER.error("Error building WebSocketContext: {}", e.getMessage());
            return new Output("[red]Oops! Something went wrong. The error has been reported. Please try again.");
        }

        String remoteIp = (String) wsContext.getAttributes().getOrDefault(SESSION_REMOTE_IP_KEY, "(no IP)");
        String httpSessionId = (String) wsContext.getAttributes().getOrDefault(HTTP_SESSION_ID_ATTR_NAME, "(no HTTP session)");
        String wsSessionId = SimpMessageHeaderAccessor.getSessionId(headers);

        wsContext.getAttributes().put(MUD_QUESTION, initialQuestion.getBeanName());

        LOGGER.info("New connection: {} {} {} {}",
            remoteIp,
            wsSessionId,
            httpSessionId,
            wsContext.getPrincipal().getName());

        return new Output()
            .append(greeting)
            .append(initialQuestion.prompt(wsContext));
    }

    @MessageMapping("/input")
    @SendToUser(value = "/queue/output", broadcast = false)
    public Output onInput(@Headers Map<String, Object> headers, Input input) {
        WebSocketContext wsContext;

        try {
            wsContext = WebSocketContext.build(headers);
            this.webSocketContextAware.setWebSocketContext(wsContext);
        } catch (IllegalStateException e) {
            LOGGER.error("Error building WebSocketContext: {}", e.getMessage());
            return new Output("[red]Oops! Something went wrong. The error has been reported. Please try again.");
        }

        return inputProcessingService.processInput(wsContext, input);
    }
}
