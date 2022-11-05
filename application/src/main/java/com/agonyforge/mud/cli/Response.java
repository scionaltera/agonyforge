package com.agonyforge.mud.cli;

import com.agonyforge.mud.web.model.Output;

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
