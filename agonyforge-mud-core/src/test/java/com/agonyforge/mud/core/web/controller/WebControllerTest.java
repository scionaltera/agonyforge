package com.agonyforge.mud.core.web.controller;

import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.junit.jupiter.MockitoExtension;

import static org.junit.jupiter.api.Assertions.assertEquals;

@ExtendWith(MockitoExtension.class)
public class WebControllerTest {
    @Test
    void testIndex() {
        WebController uut = new WebController();
        String result = uut.index();

        assertEquals("index", result);
    }

    @Test
    void testPlay() {
        WebController uut = new WebController();
        String result = uut.play();

        assertEquals("play", result);
    }
}
