package com.E_commerce_website_project.demo.controller;
import com.E_commerce_website_project.demo.entity.Product;
import com.E_commerce_website_project.demo.repository.ProductRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.*;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.client.RestTemplate;
import org.springframework.web.multipart.MultipartFile;

import java.io.IOException;
import java.util.List;
import java.util.UUID;

@Controller
@RequestMapping("/admin/products")
public class AdminProductController {

    @Autowired
    private ProductRepository productRepo;

    // --- Supabase Configuration ---
    // It's a good practice to move these to your application.properties or environment variables
    private static final String SUPABASE_PROJECT_URL = "https://nzxfzuujvykaupllrbxv.supabase.co";
    private static final String SUPABASE_STORAGE_BUCKET = "product-images";
    private static final String SUPABASE_SERVICE_ROLE_KEY = "eyJhbGciOiJIUzI1NiIsInR5cCI6IkpXVCJ9.eyJpc3MiOiJzdXBhYmFzZSIsInJlZiI6Im56eGZ6dXVqdnlrYXVwbGxyYnh2Iiwicm9sZSI6InNlcnZpY2Vfcm9sZSIsImlhdCI6MTc1MjY2MDE0OSwiZXhwIjoyMDY4MjM2MTQ5fQ.WrZoVyKJYsOXQfdQZ0ZPEPoSZcPJAsLFUFoBuNAKO48";

    @PostMapping("/save")
    public String saveProduct(@RequestParam String category,
                              @RequestParam("image") MultipartFile imageFile,
                              @RequestParam String name,
                              @RequestParam String description,
                              @RequestParam double price) {
        try {
            String fileName = category + "/" + UUID.randomUUID() + "_" + imageFile.getOriginalFilename();
            uploadToSupabaseStorage(imageFile, fileName);

            String imagePath = SUPABASE_PROJECT_URL + "/storage/v1/object/public/" + SUPABASE_STORAGE_BUCKET + "/" + fileName;

            Product product = new Product();
            product.setName(name);
            product.setCategory(category);
            product.setDescription(description);
            product.setPrice(price);
            product.setImagePath(imagePath);

            productRepo.save(product);

            // **FIXED:** Redirect to a valid page that shows products for the saved category
            return "redirect:/admin/products/manage/" + product.getCategory();

        } catch (Exception e) {
            e.printStackTrace();
            // This will now direct to your error.html page if one exists
            return "error";
        }
    }

    private void uploadToSupabaseStorage(MultipartFile file, String fileName) throws IOException {
        String url = SUPABASE_PROJECT_URL + "/storage/v1/object/" + SUPABASE_STORAGE_BUCKET + "/" + fileName;

        HttpHeaders headers = new HttpHeaders();
        headers.set("Authorization", "Bearer " + SUPABASE_SERVICE_ROLE_KEY);
        headers.setContentType(MediaType.APPLICATION_OCTET_STREAM);
        headers.add("x-upsert", "true");

        HttpEntity<byte[]> entity = new HttpEntity<>(file.getBytes(), headers);
        RestTemplate restTemplate = new RestTemplate();
        restTemplate.exchange(url, HttpMethod.POST, entity, String.class);
    }

    @GetMapping
    public String viewProducts(Model model) {
        model.addAttribute("products", productRepo.findAll());
        return "products";
    }

    @GetMapping("/new")
    public String showAddForm() {
        return "add_product";
    }

    @GetMapping("/manage")
    public String manageCategories(Model model) {
        List<String> categories = productRepo.findDistinctCategories();
        model.addAttribute("categories", categories);
        return "manage_categories";
    }

    @GetMapping("/manage/{category}")
    public String manageProductsByCategory(@PathVariable String category, Model model) {
        List<Product> products = productRepo.findByCategory(category);
        model.addAttribute("products", products);
        model.addAttribute("category", category);
        return "manage_products";
    }

    @GetMapping("/edit/{id}")
    public String showEditForm(@PathVariable Long id, Model model) {
        Product product = productRepo.findById(id)
                .orElseThrow(() -> new IllegalArgumentException("Invalid product Id:" + id));
        model.addAttribute("product", product);
        return "edit_product";
    }

    @PostMapping("/update")
    public String updateProduct(@ModelAttribute Product updated,
                                @RequestParam("imageFile") MultipartFile imageFile) throws IOException {
        Product product = productRepo.findById(updated.getId())
                .orElseThrow(() -> new IllegalArgumentException("Invalid product Id:" + updated.getId()));

        product.setName(updated.getName());
        product.setDescription(updated.getDescription());
        product.setPrice(updated.getPrice());

        if (!imageFile.isEmpty()) {
            String fileName = product.getCategory() + "/" + UUID.randomUUID() + "_" + imageFile.getOriginalFilename();
            uploadToSupabaseStorage(imageFile, fileName);
            String imagePath = SUPABASE_PROJECT_URL + "/storage/v1/object/public/" + SUPABASE_STORAGE_BUCKET + "/" + fileName;
            product.setImagePath(imagePath);
        }

        productRepo.save(product);
        return "redirect:/admin/products/manage/" + product.getCategory();
    }

    @GetMapping("/delete/{id}")
    public String deleteProduct(@PathVariable Long id) {
        Product product = productRepo.findById(id).orElse(null);
        if (product != null) {
            String category = product.getCategory();
            productRepo.delete(product);
            // Redirect to the category page after deleting
            return "redirect:/admin/products/manage/" + category;
        }
        // Fallback redirect
        return "redirect:/admin/products/manage";
    }
}






//package com.E_commerce_website_project.demo.controller;
//
//import com.E_commerce_website_project.demo.entity.Product;
//import com.E_commerce_website_project.demo.repository.ProductRepository;
//import org.springframework.beans.factory.annotation.Autowired;
//import org.springframework.stereotype.Controller;
//import org.springframework.ui.Model;
//import org.springframework.web.bind.annotation.*;
//import org.springframework.web.multipart.MultipartFile;
//
//import java.io.File;
//import java.io.IOException;
//import java.nio.file.Files;
//import java.nio.file.Path;
//import java.nio.file.Paths;
//import java.util.List;
//import java.util.UUID;
//
//@Controller
//@RequestMapping("/admin/products")
//public class AdminProductController {
//
//    @Autowired
//    private ProductRepository productRepo;
//
//    @PostMapping("/save")
//    public String saveProduct(@RequestParam String category,
//                              @RequestParam("image") MultipartFile imageFile,
//                              @RequestParam String name,
//                              @RequestParam String description,
//                              @RequestParam double price) throws IOException {
//        try {
//            String rootPath = "C:/22bce9398/demo/demo/uploads/images/productimages/" + category;
//
//            File folder = new File(rootPath);
//            if (!folder.exists()) {
//                folder.mkdirs();
//            }
//
//            String fileName = UUID.randomUUID() + "_" + imageFile.getOriginalFilename();
//            Path filePath = Paths.get(rootPath, fileName);
//            Files.write(filePath, imageFile.getBytes());
//
//            // This will be the image URL path accessible from the browser
//            String imagePath = "/uploads/images/productimages/" + category + "/" + fileName;
//
//            Product product = new Product();
//            product.setName(name);
//            product.setCategory(category);
//            product.setDescription(description);
//            product.setPrice(price);
//            product.setImagePath(imagePath);  // Set relative path
//
//            productRepo.save(product);
//            return "redirect:/profile";
//        } catch (Exception e) {
//            e.printStackTrace();
//            return "error";
//        }
//    }
//
//
//
//    @GetMapping
//    public String viewProducts(Model model) {
//        model.addAttribute("products", productRepo.findAll());
//        return "products"; // your 'products.html'
//    }
//
//    @GetMapping("/new")
//    public String showAddForm() {
//        return "add_product";
//    }
//    @GetMapping("/manage")
//    public String manageCategories(Model model) {
//        List<String> categories = productRepo.findDistinctCategories();
//        model.addAttribute("categories", categories);
//        return "manage_categories";
//    }
//    @GetMapping("/manage/{category}")
//    public String manageProductsByCategory(@PathVariable String category, Model model) {
//        List<Product> products = productRepo.findByCategory(category);
//        model.addAttribute("products", products);
//        model.addAttribute("category", category);
//        return "manage_products";
//    }
//    @GetMapping("/edit/{id}")
//    public String showEditForm(@PathVariable Long id, Model model) {
//        Product product = productRepo.findById(id).orElseThrow();
//        model.addAttribute("product", product);
//        return "edit_product";
//    }
//
//    @PostMapping("/update")
//    public String updateProduct(@ModelAttribute Product updated,
//                                @RequestParam("imageFile") MultipartFile imageFile) throws IOException {
//        Product product = productRepo.findById(updated.getId()).orElseThrow();
//        product.setName(updated.getName());
//        product.setDescription(updated.getDescription());
//        product.setPrice(updated.getPrice());
//
//        if (!imageFile.isEmpty()) {
//            String folderPath = "C:/22bce9398/demo/demo/uploads/images/productimages/" + product.getCategory();
//            String fileName = UUID.randomUUID() + "_" + imageFile.getOriginalFilename();
//            Files.write(Paths.get(folderPath, fileName), imageFile.getBytes());
//            product.setImagePath("/uploads/images/productimages/" + product.getCategory() + "/" + fileName);
//        }
//
//        productRepo.save(product);
//        return "redirect:/admin/products/manage/" + product.getCategory();
//    }
//
//    @GetMapping("/delete/{id}")
//    public String deleteProduct(@PathVariable Long id) {
//        Product product = productRepo.findById(id).orElse(null);
//        if (product != null) {
//            productRepo.delete(product);
//        }
//        return "redirect:/admin/products/manage";
//    }
//
//}
