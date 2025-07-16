package com.E_commerce_website_project.demo.controller;


import com.E_commerce_website_project.demo.entity.User;
import com.E_commerce_website_project.demo.service.UserService;
import jakarta.servlet.http.HttpSession;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.ModelAttribute;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.RequestParam;


@Controller
public class UserController {

    @Autowired
    private UserService userService;

    @PostMapping("/user/register")
    public String registerUser(@ModelAttribute User user, Model model) {
        userService.saveUser(user); // Saves to DB
        model.addAttribute("message", "Registration successful!");
        return "login"; // or wherever you want to redirect
    }
    @PostMapping("/user/login")
    public String loginUser(@RequestParam String username, @RequestParam String password, HttpSession session, Model model) {
        User user = userService.findByUsernameOrEmail(username);

        if (user != null) {
            System.out.println("User found: " + user.getUsername());

            if (user.getPassword().equals(password)) {
                session.setAttribute("loggedInUser", user);
                session.setAttribute("role", "USER");
                session.setAttribute("username", user.getUsername());
                session.setAttribute("email", user.getEmailid());
                return "redirect:/";
            } else {
                System.out.println("Password mismatch");
                model.addAttribute("error", "Invalid password");
                return "login";
            }
        } else {
            System.out.println("User not found");
            model.addAttribute("error", "User not found");
            return "login";
        }
    }

}
