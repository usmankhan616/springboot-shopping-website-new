package com.E_commerce_website_project.demo.controller;
import com.E_commerce_website_project.demo.entity.Admin;
import com.E_commerce_website_project.demo.repository.ProductRepository;
import com.E_commerce_website_project.demo.service.AdminService;
import jakarta.servlet.http.HttpSession;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.ModelAttribute;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestParam;

import java.util.List;

@Controller
public class AdminController {
    @Autowired
    private AdminService adminService;
    @Autowired
    private ProductRepository productRepo;
    @PostMapping("/admin/register")
    public String registerUser(@ModelAttribute Admin admin, Model model) {
        adminService.saveAdmin(admin); // Saves to DB
        model.addAttribute("message", "Registration successful!");
        return "login"; // or wherever you want to redirect
    }
    @PostMapping("/admin/login")
    public String loginAdmin(@RequestParam String username, @RequestParam String password, HttpSession session, Model model) {
        Admin admin = adminService.findByUsernameOrEmail(username);
        if (admin != null && admin.getPassword().equals(password)) {
            session.setAttribute("loggedInAdmin", admin);
            session.setAttribute("role", "ADMIN");
            session.setAttribute("username", admin.getUsername());
            session.setAttribute("email", admin.getEmailid());
            return "redirect:/";
        } else {
            model.addAttribute("error", "Invalid credentials");
            return "login";
        }
    }
    @GetMapping("/manage")
    public String manageCategories(Model model) {
        List<String> categories = productRepo.findDistinctCategories();
        model.addAttribute("categories", categories);
        return "manage_categories";
    }
}
