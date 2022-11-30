package com.agonyforge.mud.core.web.controller;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.security.oauth2.client.authentication.OAuth2AuthenticationToken;
import org.springframework.security.oauth2.core.user.OAuth2User;
import org.springframework.web.servlet.ModelAndView;

import java.util.List;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertTrue;
import static org.mockito.Mockito.when;

@ExtendWith(MockitoExtension.class)
public class WebControllerTest {
    @Mock
    private OAuth2AuthenticationToken principal;

    @Mock
    private OAuth2User user;

    @BeforeEach
    void setUp() {
        when(principal.getPrincipal()).thenReturn(user);
        when(user.getAuthorities()).thenReturn(List.of());
    }

    @Test
    void testIndex() {
        WebController uut = new WebController();
        ModelAndView result = uut.index(principal);

        assertEquals("index", result.getViewName());
        assertTrue(result.getModel().containsKey("authorities"));
    }

    @Test
    void testPlay() {
        WebController uut = new WebController();
        ModelAndView result = uut.play(principal);

        assertEquals("play", result.getViewName());
        assertTrue(result.getModel().containsKey("authorities"));
    }
}
