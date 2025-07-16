package com.E_commerce_website_project.demo.controller;

import jakarta.servlet.http.HttpSession;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;

@Controller
public class ProfileController {

    @GetMapping("/profile")
    public String profilePage(HttpSession session, Model model) {
        String role = (String) session.getAttribute("role");
        if (role == null) return "redirect:/login";

        if ("USER".equals(role)) {
            model.addAttribute("username", session.getAttribute("username"));
            model.addAttribute("email", session.getAttribute("email"));
            model.addAttribute("role", "User");
            return "userprofile";
        } else if ("ADMIN".equals(role)) {
            model.addAttribute("username", session.getAttribute("username"));
            model.addAttribute("email", session.getAttribute("email"));
            model.addAttribute("role", "Admin");
            return "adminprofile";
        }
        else {
            return "login";
        }
    }

}
