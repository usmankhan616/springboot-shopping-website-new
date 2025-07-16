package com.E_commerce_website_project.demo.controller;

import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.GetMapping;

@Controller
public class PageController {
    @GetMapping("/")
    public String home(){
        return "index";
    }
    @GetMapping("/login")
    public String handleLogin() {
        return "login";
    }
}
