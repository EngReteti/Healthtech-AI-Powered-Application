package com.amason.hospitalinventory.controller;

import com.amason.hospitalinventory.dto.LoginRequest;
import com.amason.hospitalinventory.model.User;
import com.amason.hospitalinventory.repository.UserRepository;
import com.amason.hospitalinventory.security.JwtUtil;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.web.bind.annotation.*;
import java.util.HashMap;
import java.util.Map;

@RestController
@RequestMapping("/api/auth")
public class AuthController {

    @Autowired
    private UserRepository userRepository;

    @Autowired
    private PasswordEncoder passwordEncoder;

    @Autowired
    private JwtUtil jwtUtil;

    @PostMapping("/login")
    public Map<String, String> login(@RequestBody LoginRequest request) {

        User user = userRepository.findByEmail(request.getEmail())
            .orElseThrow(() -> new RuntimeException("Invalid email or password"));

        boolean passwordCorrect = passwordEncoder.matches(request.getPassword(), user.getPasswordHash());

        if (!passwordCorrect) {
            throw new RuntimeException("Invalid email or password");
        }

        // NEW: a correct password on a deactivated account still 
        // isn't allowed through - this is the actual enforcement 
        // that makes deactivation meaningful, not just a label
        if (!user.getActive()) {
            throw new RuntimeException("This account has been deactivated. Contact an administrator.");
        }

        String token = jwtUtil.generateToken(user.getEmail(), user.getRole().toString());

        Map<String, String> response = new HashMap<>();
        response.put("token", token);
        response.put("role", user.getRole().toString());

        return response;
    }
}
