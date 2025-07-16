package com.E_commerce_website_project.demo.repository;

import com.E_commerce_website_project.demo.entity.Product;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;

import java.util.List;

public interface ProductRepository extends JpaRepository<Product, Long> {
    List<Product> findByCategory(String category);
    @Query("SELECT DISTINCT p.category FROM Product p")
    List<String> findDistinctCategories();


}
