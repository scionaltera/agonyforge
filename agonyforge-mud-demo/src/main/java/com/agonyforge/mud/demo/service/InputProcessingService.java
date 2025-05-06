package com.agonyforge.mud.demo.service;

import com.agonyforge.mud.core.cli.Question;
import com.agonyforge.mud.core.cli.Response;
import com.agonyforge.mud.core.web.model.Input;
import com.agonyforge.mud.core.web.model.Output;
import com.agonyforge.mud.core.web.model.WebSocketContext;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.ApplicationContext;
import org.springframework.stereotype.Service;

import static com.agonyforge.mud.core.config.SessionConfiguration.MUD_QUESTION;

@Service
public class InputProcessingService {
    private final ApplicationContext applicationContext;

    @Autowired
    public InputProcessingService(ApplicationContext applicationContext) {
        this.applicationContext = applicationContext;
    }

    public Output processInput(WebSocketContext wsContext, Input input) {
        String questionName = (String) wsContext.getAttributes().get(MUD_QUESTION);
        Question currentQuestion = applicationContext.getBean(questionName, Question.class);

        Response response = currentQuestion.answer(wsContext, input);
        Question nextQuestion = response.getNext();

        Output output = new Output();
        response.getFeedback().ifPresent(output::append);
        output.append(nextQuestion.prompt(wsContext));

        wsContext.getAttributes().put(MUD_QUESTION, nextQuestion.getBeanName());

        return output;
    }
}