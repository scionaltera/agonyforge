package com.agonyforge.mud.core.service.timer;

import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.junit.jupiter.MockitoExtension;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertTrue;

@ExtendWith(MockitoExtension.class)
public class TimerMessageTest {
    @Test
    void testGetTimestampDefault() {
        TimerMessage uut = new TimerMessage();

        assertEquals(0L, uut.getTimestamp());
    }

    @Test
    void testGetTimestamp() {
        Long systemTime = System.currentTimeMillis();
        TimerMessage uut = new TimerMessage(systemTime);

        assertEquals(systemTime, uut.getTimestamp());
    }

    @Test
    void testToString() {
        TimerMessage uut = new TimerMessage();

        assertTrue(uut.toString().length() > 1);
    }
}
