package com.agonyforge.mud.web.controller;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.boot.web.servlet.error.ErrorAttributes;
import org.springframework.ui.Model;

import javax.servlet.http.HttpServletRequest;
import java.util.HashMap;
import java.util.Map;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.eq;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

@ExtendWith(MockitoExtension.class)
public class CustomErrorControllerTest {
    @Mock
    private ErrorAttributes errorAttributes;

    @Mock
    private Model model;

    @Mock
    private HttpServletRequest request;

    private Map<String, Object> attributes;

    @BeforeEach
    void setUp() {
        attributes = new HashMap<>();
        attributes.put("status", 400);
        attributes.put("error", "too many squirrels");

        when(errorAttributes.getErrorAttributes(any(), any())).thenReturn(attributes);
    }

    @Test
    void testError() {
        CustomErrorController uut = new CustomErrorController(errorAttributes);
        String result = uut.error(model, request);

        verify(model).addAttribute(eq("errorAttributes"), eq(attributes));

        assertEquals("error", result);
    }
}
