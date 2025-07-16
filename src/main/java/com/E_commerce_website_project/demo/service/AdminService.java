package com.E_commerce_website_project.demo.service;

import com.E_commerce_website_project.demo.entity.Admin;
import com.E_commerce_website_project.demo.repository.AdminRepo;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

@Service
public class AdminService {
    @Autowired
    private AdminRepo adminRepo;

    public void saveAdmin(Admin admin) {
        adminRepo.save(admin);
    }
    public Admin findByUsernameOrEmail(String input) {
        return adminRepo.findByUsername(input)
                .orElse(adminRepo.findByEmailid(input).orElse(null));
    }

}
