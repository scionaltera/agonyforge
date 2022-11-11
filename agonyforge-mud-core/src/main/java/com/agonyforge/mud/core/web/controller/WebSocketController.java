package com.agonyforge.mud.core.web.controller;

import com.agonyforge.mud.core.cli.OutputLoader;
import com.agonyforge.mud.core.cli.Question;
import com.agonyforge.mud.core.cli.Response;
import com.agonyforge.mud.core.web.model.Input;
import com.agonyforge.mud.core.web.model.Output;
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
import org.springframework.session.FindByIndexNameSessionRepository;
import org.springframework.session.Session;
import org.springframework.session.SessionRepository;
import org.springframework.stereotype.Controller;

import java.io.IOException;
import java.security.Principal;
import java.util.Map;

import static com.agonyforge.mud.core.config.RemoteIpHandshakeInterceptor.SESSION_REMOTE_IP_KEY;
import static org.springframework.web.socket.server.support.HttpSessionHandshakeInterceptor.HTTP_SESSION_ID_ATTR_NAME;

@Controller
public class WebSocketController {
    public static final String CURRENT_QUESTION_KEY = "MUD.QUESTION";
    public static final String WS_SESSION_ID = "WS.SESSION.ID";

    private static final Logger LOGGER = LoggerFactory.getLogger(WebSocketController.class);

    private final ApplicationContext applicationContext;
    private final SessionRepository<Session> sessionRepository;
    private final Question initialQuestion;
    private Output greeting;

    @Autowired
    public WebSocketController(ApplicationContext applicationContext,
                               FindByIndexNameSessionRepository<Session> sessionRepository,
                               @Qualifier("initialQuestion") Question initialQuestion) {
        this.applicationContext = applicationContext;
        this.sessionRepository = sessionRepository;
        this.initialQuestion = initialQuestion;

        try {
            greeting = OutputLoader.loadTextFile("greeting.txt");
        } catch (IOException e) {
            LOGGER.warn("No greeting.txt found in classpath.");
            greeting = new Output("Welcome!");
        }
    }

    @SubscribeMapping("/queue/output")
    public Output onSubscribe(Principal principal, @Headers Map<String, Object> headers) {
        Map<String, Object> attributes = SimpMessageHeaderAccessor.getSessionAttributes(headers);

        if (attributes == null) {
            LOGGER.error("No headers on subscribe message!");
            return new Output("[red]Oops! Something went wrong. Please try refreshing your browser.");
        }

        String remoteIp = (String)attributes.getOrDefault(SESSION_REMOTE_IP_KEY, "(no IP)");
        String wsSessionName = SimpMessageHeaderAccessor.getSessionId(headers);
        Session httpSession = sessionRepository.findById((String) attributes.get(HTTP_SESSION_ID_ATTR_NAME));

        httpSession.setAttribute(CURRENT_QUESTION_KEY, initialQuestion.getBeanName());
        httpSession.setAttribute(WS_SESSION_ID, wsSessionName);

        LOGGER.info("New connection: {} {} {} {}",
            remoteIp,
            httpSession.getId(),
            wsSessionName,
            principal.getName());

        return greeting.append(initialQuestion.prompt(principal, httpSession));
    }

    @MessageMapping("/input")
    @SendToUser(value = "/queue/output", broadcast = false)
    public Output onInput(Principal principal, Input input, @Headers Map<String, Object> headers) {
        Map<String, Object> attributes = SimpMessageHeaderAccessor.getSessionAttributes(headers);

        if (attributes == null) {
            LOGGER.error("No headers on input message!");
            return new Output("[red]Oops! Something went wrong. Please try again.");
        }

        Session httpSession = sessionRepository.findById((String) attributes.get(HTTP_SESSION_ID_ATTR_NAME));
        Question currentQuestion = applicationContext.getBean(httpSession.getAttribute(CURRENT_QUESTION_KEY), Question.class);

        Response response = currentQuestion.answer(principal, httpSession, input);
        Question nextQuestion = response.getNext();
        Output output = new Output();

        // append any feedback from the last question
        response.getFeedback().ifPresent(output::append);

        // append the prompt from the next question
        output.append(nextQuestion.prompt(principal, httpSession));

        // store the next question in the session
        httpSession.setAttribute(CURRENT_QUESTION_KEY, nextQuestion.getBeanName());

        return output;
    }
}
