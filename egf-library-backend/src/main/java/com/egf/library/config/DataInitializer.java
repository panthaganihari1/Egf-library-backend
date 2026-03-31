package com.egf.library.config;

import com.egf.library.model.User;
import com.egf.library.repository.UserRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.CommandLineRunner;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Component;

@Component
public class DataInitializer implements CommandLineRunner {

    @Autowired
    private UserRepository userRepository;

    @Autowired
    private PasswordEncoder passwordEncoder;

    @Override
    public void run(String... args) {
        try {
            if (userRepository.existsByUsername("hari")) {
                User admin = new User();
                admin.setUsername("egf");
                admin.setPassword(passwordEncoder.encode("egf"));
                admin.setFullName("hari");
                admin.setRole("INCHARGE");
                userRepository.save(admin);
                System.out.println("✅ Default user created");
            } else {
                System.out.println("ℹ️ User already exists, skipping");
            }
        } catch (Exception e) {
            // ✅ Don't crash app if user creation fails
            System.out.println("⚠️ DataInitializer skipped: " + e.getMessage());
        }
    }
}