package com.agonyforge.mud.core.web.controller;

import org.springframework.security.oauth2.client.authentication.OAuth2AuthenticationToken;
import org.springframework.security.oauth2.core.user.OAuth2User;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.servlet.ModelAndView;

import java.security.Principal;

@Controller
public class WebController {
    @RequestMapping("/")
    public ModelAndView index(Principal principal) {
        ModelAndView model = new ModelAndView("index");

        if (principal != null) {
            OAuth2AuthenticationToken token = (OAuth2AuthenticationToken) principal;
            OAuth2User user = token.getPrincipal();

            model.addObject("authorities", user.getAuthorities());
        }

        return model;
    }

    @RequestMapping("/play")
    public ModelAndView play(Principal principal) {
        ModelAndView model = new ModelAndView("play");

        if (principal != null) {
            OAuth2AuthenticationToken token = (OAuth2AuthenticationToken) principal;
            OAuth2User user = token.getPrincipal();

            model.addObject("authorities", user.getAuthorities());
        }

        return model;
    }
}
