package com.E_commerce_website_project.demo.controller;

import com.E_commerce_website_project.demo.entity.Product;
import com.E_commerce_website_project.demo.repository.ProductRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestParam;

import java.util.List;

@Controller
public class ShopController {

    @Autowired
    private ProductRepository productRepository;

    @GetMapping("/shop")
    public String showByCategory(@RequestParam("category") String category, Model model) {
        List<Product> products = productRepository.findByCategory(category);
        model.addAttribute("products", products);
        model.addAttribute("category", category);
        return "category_products"; // ✅ this HTML will handle all categories
    }
}
