package com.farmconnect.backend.controller;

import com.farmconnect.backend.model.User;
import com.farmconnect.backend.repository.UserRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.*;

import java.util.Optional;

@CrossOrigin(origins = "*") // allow frontend
@RestController
@RequestMapping("/users")
public class UserController {

    @Autowired
    private UserRepository userRepository;

    // ✅ REGISTER API
    @PostMapping("/register")
    public User register(@RequestBody User user) {

        Optional<User> existing = userRepository.findByEmail(user.getEmail());

        if (existing.isPresent()) {
            throw new RuntimeException("Email already exists");
        }

        return userRepository.save(user);
    }

    // ✅ LOGIN API
    @PostMapping("/login")
    public User login(@RequestBody User user) {

        Optional<User> existing = userRepository.findByEmail(user.getEmail());

        if (existing.isPresent() &&
            existing.get().getPassword().equals(user.getPassword())) {

            return existing.get();
        }

        throw new RuntimeException("Invalid credentials");
    }
}