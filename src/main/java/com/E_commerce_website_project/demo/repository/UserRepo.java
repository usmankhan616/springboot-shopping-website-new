package com.E_commerce_website_project.demo.repository;

import com.E_commerce_website_project.demo.entity.User;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.Optional;

public interface UserRepo extends JpaRepository<User,Long> {
    Optional<User> findByUsername(String username);
    Optional<User> findByEmailid(String emailid);

}
