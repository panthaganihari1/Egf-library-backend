package com.egf.library.controller;

import com.egf.library.model.User;
import com.egf.library.repository.UserRepository;
import com.egf.library.security.JwtUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.Authentication;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.web.bind.annotation.*;

import java.util.HashMap;
import java.util.Map;

@RestController
@RequestMapping("/api/auth")
public class AuthController {

    @Autowired
    private AuthenticationManager authenticationManager;

    @Autowired
    private JwtUtils jwtUtils;

    @Autowired
    private UserRepository userRepository;

    @Autowired
    private PasswordEncoder passwordEncoder;

    @PostMapping("/login")
    public ResponseEntity<?> login(@RequestBody Map<String, String> request) {
        try {
            Authentication auth = authenticationManager.authenticate(
                    new UsernamePasswordAuthenticationToken(
                            request.get("username"),
                            request.get("password")
                    )
            );
            String token = jwtUtils.generateToken(request.get("username"));
            User user = userRepository.findByUsername(request.get("username")).orElseThrow();

            Map<String, Object> response = new HashMap<>();
            response.put("token", token);
            response.put("username", user.getUsername());
            response.put("fullName", user.getFullName());
            response.put("role", user.getRole());

            return ResponseEntity.ok(response);
        } catch (Exception e) {
            return ResponseEntity.badRequest().body(Map.of("error", "Invalid username or password"));
        }
    }

    @GetMapping("/ping")
    public ResponseEntity<String> ping() {
        return ResponseEntity.ok("Working Properly");
    }

    @PostMapping("/register")
    public ResponseEntity<?> register(@RequestBody Map<String, String> request) {
        if (userRepository.existsByUsername(request.get("username"))) {
            return ResponseEntity.badRequest().body(Map.of("error", "Username already exists"));
        }
        User user = new User();
        user.setUsername(request.get("username"));
        user.setPassword(passwordEncoder.encode(request.get("password")));
        user.setFullName(request.get("fullName"));
        user.setRole("INCHARGE");
        userRepository.save(user);
        return ResponseEntity.ok(Map.of("message", "Incharge registered successfully"));
    }
}
