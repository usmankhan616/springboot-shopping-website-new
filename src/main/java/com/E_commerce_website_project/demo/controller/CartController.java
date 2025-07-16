package com.E_commerce_website_project.demo.controller;

import com.E_commerce_website_project.demo.entity.CartItem;
import com.E_commerce_website_project.demo.entity.Product;
import com.E_commerce_website_project.demo.repository.ProductRepository;
import com.E_commerce_website_project.demo.service.AdminService;
import com.E_commerce_website_project.demo.service.UserService;
import jakarta.servlet.http.HttpSession;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.*;

import java.util.HashMap;
import java.util.Map;

@Controller
@RequestMapping("/cart")
public class CartController {

    @Autowired
    private ProductRepository productRepo;

    @PostMapping("/add")
    public String addToCart(@RequestParam Long productId, HttpSession session) {
        // Initialize the cart if it doesn't exist
        Map<Long, CartItem> cart = (Map<Long, CartItem>) session.getAttribute("cart");
        if (cart == null) {
            cart = new HashMap<>();  // Initialize a new cart if none exists
            session.setAttribute("cart", cart);  // Set the cart in session
        }

        // Retrieve the product to add to the cart
        Product product = productRepo.findById(productId).orElse(null);
        if (product != null) {
            if (cart.containsKey(productId)) {
                // If the product already exists in the cart, increment quantity
                CartItem existingItem = cart.get(productId);
                existingItem.setQuantity(existingItem.getQuantity() + 1);
            } else {
                // Add the product with quantity 1 if not already in the cart
                cart.put(productId, new CartItem(product));
            }
        }

        // Save updated cart back to session
        session.setAttribute("cart", cart);
        return "redirect:/cart/view";  // Redirect to view cart page
    }

    // Shows password confirmation form (via POST from cart)
    @PostMapping("/checkout")
    public String checkoutPrompt(HttpSession session, Model model) {
        String username = (String) session.getAttribute("username");
        if (username == null) {
            return "redirect:/login";
        }
        model.addAttribute("username", username);
        return "confirm_checkout";  // This must match an HTML file name
    }

    // Optional: handle browser-typed GET to avoid 404
    @GetMapping("/checkout")
    public String preventDirectCheckout() {
        return "redirect:/cart/view";  // Redirect to cart if accessed directly
    }



    @Autowired
    private UserService userService;

    @Autowired
    private AdminService adminService;

    @PostMapping("/checkout/confirm")
    public String confirmCheckout(@RequestParam String username,
                                  @RequestParam String password,
                                  HttpSession session,
                                  Model model) {

        String role = (String) session.getAttribute("role");
        boolean valid = false;

        if ("USER".equals(role)) {
            var user = userService.findByUsernameOrEmail(username);
            if (user != null && user.getPassword().equals(password)) {
                valid = true;
            }
        } else if ("ADMIN".equals(role)) {
            var admin = adminService.findByUsernameOrEmail(username);
            if (admin != null && admin.getPassword().equals(password)) {
                valid = true;
            }
        }

        if (valid) {
            session.removeAttribute("cart");
            return "thankyou";
        } else {
            model.addAttribute("username", username);
            model.addAttribute("error", "Invalid password");
            return "confirm_checkout";
        }
    }

    @GetMapping("/remove/{productId}")
    public String removeFromCart(@PathVariable Long productId, HttpSession session) {
        Map<Long, CartItem> cart = (Map<Long, CartItem>) session.getAttribute("cart");
        if (cart != null) {
            cart.remove(productId);
            session.setAttribute("cart", cart);
        }
        return "redirect:/cart/view";
    }

    @GetMapping("/view")
    public String viewCart(HttpSession session, Model model) {
        Map<Long, CartItem> cart = (Map<Long, CartItem>) session.getAttribute("cart");
        if (cart == null) cart = new HashMap<>();

        double total = cart.values().stream()
                .mapToDouble(item -> item.getProduct().getPrice() * item.getQuantity())
                .sum();

        model.addAttribute("cartItems", cart.values());
        model.addAttribute("totalPrice", total);
        return "cart";
    }
}
