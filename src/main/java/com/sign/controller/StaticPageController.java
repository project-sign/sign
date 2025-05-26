package com.sign.controller;

import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.GetMapping;

@Controller
public class StaticPageController {

    @GetMapping("registration")
    String register() {
        return "registration";
    }

    @GetMapping("login")
    String login() {
        return "login";
    }
}
