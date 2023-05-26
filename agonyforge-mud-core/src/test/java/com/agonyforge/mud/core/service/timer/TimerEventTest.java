package com.agonyforge.mud.core.service.timer;

import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.junit.jupiter.MockitoExtension;

import java.util.concurrent.TimeUnit;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertTrue;

@ExtendWith(MockitoExtension.class)
public class TimerEventTest {
    @Test
    void testSourceAndFrequency() {
        TimerEvent uut = new TimerEvent(this, TimeUnit.MINUTES);

        assertEquals(this, uut.getSource());
        assertEquals(TimeUnit.MINUTES, uut.getFrequency());
    }

    @Test
    void testToString() {
        TimerEvent uut = new TimerEvent(this, TimeUnit.MINUTES);

        assertTrue(uut.toString().length() > 1);
    }
}
