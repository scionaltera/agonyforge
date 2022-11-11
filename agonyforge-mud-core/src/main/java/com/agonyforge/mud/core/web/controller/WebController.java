package com.agonyforge.mud.core.web.controller;

import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.RequestMapping;

@Controller
public class WebController {
    @RequestMapping("/")
    public String index() {
        return "index";
    }

    @RequestMapping("/play")
    public String play() {
        return "play";
    }
}
