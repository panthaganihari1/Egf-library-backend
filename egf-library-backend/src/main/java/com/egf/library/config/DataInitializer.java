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
        if (true) {
            User admin = new User();
            admin.setUsername("hari");
            admin.setPassword(passwordEncoder.encode("hari"));
            admin.setFullName("hari");
            admin.setRole("INCHARGE");
            userRepository.save(admin);
            System.out.println("✅ Default admin created: username=admin, password=egf@2024");
        }
    }
}
