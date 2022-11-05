package com.agonyforge.mud.web.controller;

import com.agonyforge.mud.cli.Question;
import com.agonyforge.mud.cli.Response;
import com.agonyforge.mud.web.model.Input;
import com.agonyforge.mud.web.model.Output;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.messaging.handler.annotation.Headers;
import org.springframework.messaging.handler.annotation.MessageMapping;
import org.springframework.messaging.simp.SimpMessageHeaderAccessor;
import org.springframework.messaging.simp.annotation.SendToUser;
import org.springframework.messaging.simp.annotation.SubscribeMapping;
import org.springframework.stereotype.Controller;

import java.security.Principal;
import java.util.Map;

import static com.agonyforge.mud.config.RemoteIpHandshakeInterceptor.SESSION_REMOTE_IP_KEY;

@Controller
public class WebSocketController {
    public static final String CURRENT_QUESTION_KEY = "MUD.QUESTION";

    private static final Logger LOGGER = LoggerFactory.getLogger(WebSocketController.class);


    private final Question initialQuestion;

    @Autowired
    public WebSocketController(@Qualifier("menuQuestion") Question initialQuestion) {
        this.initialQuestion = initialQuestion;
    }

    @SubscribeMapping("/queue/output")
    public Output onSubscribe(Principal principal, @Headers Map<String, Object> headers) {
        Map<String, Object> attributes = SimpMessageHeaderAccessor.getSessionAttributes(headers);

        if (attributes == null) {
            LOGGER.error("No headers on subscribe message!");
            return new Output("[red]Oops! Something went wrong. Please try refreshing your browser.");
        }

        String remoteIp = (String)attributes.getOrDefault(SESSION_REMOTE_IP_KEY, "(no IP)");
        String sessionId = SimpMessageHeaderAccessor.getSessionId(headers);

        LOGGER.info("New connection: {} {} {}",
            remoteIp,
            sessionId,
            principal.getName());

        attributes.put(CURRENT_QUESTION_KEY, initialQuestion);

        return new Output("Welcome!")
            .append(initialQuestion.prompt(principal));
    }

    @MessageMapping("/input")
    @SendToUser(value = "/queue/output", broadcast = false)
    public Output onInput(Principal principal, Input input, @Headers Map<String, Object> headers) {
        Map<String, Object> attributes = SimpMessageHeaderAccessor.getSessionAttributes(headers);

        if (attributes == null) {
            LOGGER.error("No headers on input message!");
            return new Output("[red]Oops! Something went wrong. Please try again.");
        }

        Question currentQuestion = (Question)attributes.get(CURRENT_QUESTION_KEY);
        Response response = currentQuestion.answer(principal, input);
        Question nextQuestion = response.getNext();
        Output output = new Output();

        // append any feedback from the last question
        response.getFeedback().ifPresent(output::append);

        // append the prompt from the next question
        output.append(nextQuestion.prompt(principal));

        // store the next question in the session
        attributes.put(CURRENT_QUESTION_KEY, nextQuestion);

        return output;
    }
}
