package com.agonyforge.mud.core.service.timer;

import org.springframework.context.ApplicationEvent;

import java.util.concurrent.TimeUnit;

public class TimerEvent extends ApplicationEvent {
    private TimeUnit frequency;

    public TimerEvent(Object source, TimeUnit frequency) {
        super(source);

        this.frequency = frequency;
    }

    public TimeUnit getFrequency() {
        return frequency;
    }

    @Override
    public String toString() {
        return "TimerEvent{" +
            "frequency=" + frequency +
            ", source=" + source +
            '}';
    }
}
