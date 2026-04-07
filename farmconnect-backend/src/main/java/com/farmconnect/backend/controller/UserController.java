package com.farmconnect.backend.controller;

import com.farmconnect.backend.model.User;
import com.farmconnect.backend.repository.UserRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.Map;
import java.util.Optional;

@CrossOrigin(origins = "*")
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

    // ✅ UPDATE PROFILE (name, avatarUrl)
    @PutMapping("/update-profile")
    public ResponseEntity<?> updateProfile(@RequestBody Map<String, String> body) {
        try {
            String email = body.get("email");
            Optional<User> opt = userRepository.findByEmail(email);
            if (opt.isEmpty()) {
                return ResponseEntity.badRequest().body(Map.of("message", "User not found"));
            }
            User user = opt.get();
            if (body.containsKey("name") && body.get("name") != null) user.setName(body.get("name"));
            if (body.containsKey("avatarUrl")) user.setAvatarUrl(body.get("avatarUrl"));
            userRepository.save(user);
            return ResponseEntity.ok(user);
        } catch (Exception e) {
            return ResponseEntity.badRequest().body(Map.of("message", e.getMessage()));
        }
    }

    // ✅ CHANGE PASSWORD
    @PutMapping("/change-password")
    public ResponseEntity<?> changePassword(@RequestBody Map<String, String> body) {
        String email = body.get("email");
        String oldPassword = body.get("oldPassword");
        String newPassword = body.get("newPassword");

        Optional<User> opt = userRepository.findByEmail(email);
        if (opt.isEmpty()) {
            return ResponseEntity.badRequest().body(Map.of("message", "User not found"));
        }
        User user = opt.get();
        if (!user.getPassword().equals(oldPassword)) {
            return ResponseEntity.badRequest().body(Map.of("message", "Old password is incorrect"));
        }
        if (newPassword == null || newPassword.length() < 6) {
            return ResponseEntity.badRequest().body(Map.of("message", "New password must be at least 6 characters"));
        }
        user.setPassword(newPassword);
        userRepository.save(user);
        return ResponseEntity.ok(Map.of("message", "Password changed successfully"));
    }

    // ✅ DELETE ACCOUNT
    @DeleteMapping("/delete/{email}")
    public ResponseEntity<?> deleteAccount(@PathVariable String email) {
        Optional<User> opt = userRepository.findByEmail(email);
        if (opt.isEmpty()) {
            return ResponseEntity.badRequest().body(Map.of("message", "User not found"));
        }
        userRepository.delete(opt.get());
        return ResponseEntity.ok(Map.of("message", "Account deleted successfully"));
    }
}