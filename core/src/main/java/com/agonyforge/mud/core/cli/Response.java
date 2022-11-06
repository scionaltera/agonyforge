package com.agonyforge.mud.core.cli;

import com.agonyforge.mud.core.web.model.Output;

import java.util.Optional;

public class Response {
    private final Question next;
    private final Output feedback;

    public Response(Question next) {
        this.next = next;
        this.feedback = null;
    }
    public Response(Question next, Output feedback) {
        this.next = next;
        this.feedback = feedback;
    }

    public Question getNext() {
        return next;
    }

    public Optional<Output> getFeedback() {
        return Optional.ofNullable(feedback);
    }
}
