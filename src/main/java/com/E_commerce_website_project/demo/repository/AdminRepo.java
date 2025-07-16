package com.E_commerce_website_project.demo.repository;

import com.E_commerce_website_project.demo.entity.Admin;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.Optional;

public interface AdminRepo extends JpaRepository<Admin,Integer> {
    Optional<Admin> findByUsername(String username);
    Optional<Admin> findByEmailid(String emailid);

}
