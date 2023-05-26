package com.agonyforge.mud.core.service.timer;

public class TimerMessage {
    private Long timestamp = 0L;

    public TimerMessage() {}

    public TimerMessage(Long timestamp) {
        this.timestamp = timestamp;
    }

    public Long getTimestamp() {
        return timestamp;
    }

    @Override
    public String toString() {
        return "TimerMessage{" +
            "timestamp=" + timestamp +
            '}';
    }
}
