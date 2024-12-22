package com.agonyforge.mud.core.web.controller;

import jakarta.servlet.http.HttpServletRequest;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.boot.web.servlet.error.ErrorAttributes;
import org.springframework.security.oauth2.client.authentication.OAuth2AuthenticationToken;
import org.springframework.security.oauth2.core.user.OAuth2User;
import org.springframework.web.servlet.ModelAndView;

import java.util.HashMap;
import java.util.Map;
import java.util.Set;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertTrue;
import static org.mockito.ArgumentMatchers.*;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

@ExtendWith(MockitoExtension.class)
public class CustomErrorControllerTest {
    @Mock
    private ErrorAttributes errorAttributes;

    @Mock
    private OAuth2AuthenticationToken principal;

    @Mock
    private OAuth2User user;

    @Mock
    private HttpServletRequest request;

    private Map<String, Object> attributes;

    @BeforeEach
    void setUp() {
        attributes = new HashMap<>();
        attributes.put("status", 419);
        attributes.put("error", "too many squirrels");

        when(errorAttributes.getErrorAttributes(any(), any())).thenReturn(attributes);
    }

    @Test
    void testError() {
        CustomErrorController uut = new CustomErrorController(errorAttributes);
        ModelAndView result = uut.error(request);

        assertTrue(result.getModelMap().containsAttribute("error"));
        assertEquals("error", result.getViewName());
    }
}
