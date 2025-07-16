package com.E_commerce_website_project.demo.service;

import com.E_commerce_website_project.demo.entity.User;
import com.E_commerce_website_project.demo.repository.UserRepo;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

@Service
public class UserService {

    @Autowired
    private UserRepo userRepo;

    public void saveUser(User user) {
        userRepo.save(user);
    }
    public User findByUsernameOrEmail(String input) {
        return userRepo.findByUsername(input)
                .orElse(userRepo.findByEmailid(input).orElse(null));
    }

}