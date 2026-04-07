package com.farmconnect.backend.controller;

import java.util.Map; // ✅ IMPORTANT

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.*;

import com.farmconnect.backend.dto.LoginRequest;
import com.farmconnect.backend.dto.RegisterRequest;
import com.farmconnect.backend.model.User;
import com.farmconnect.backend.service.AuthService;

@RestController
@RequestMapping("/auth")
@CrossOrigin(origins = "http://localhost:5173")
public class AuthController {

    @Autowired
    private AuthService authService;

    // ================= REGISTER =================
    @PostMapping("/register")
    public Map<String, String> register(@RequestBody RegisterRequest req) {
        authService.register(req);

        return Map.of(
            "status", "success",
            "message", "Registration successful. Check email"
        );
    }

    // ================= LOGIN =================
    @PostMapping("/login")
    public Map<String, Object> login(@RequestBody LoginRequest req) {

        User user = authService.login(req);

        return Map.of(
            "status", "success",
            "user", user
        );
    }

    // ================= VERIFY =================
    @GetMapping("/verify")
    public Map<String, String> verify(@RequestParam String code) {

        authService.verify(code);

        return Map.of(
            "status", "success",
            "message", "Account verified successfully"
        );
    }

    // ================= FORGOT PASSWORD =================
    @PostMapping("/forgot-password")
    public Map<String, String> forgot(@RequestParam String email) {

        authService.forgotPassword(email);

        return Map.of(
            "status", "success",
            "message", "Reset email sent"
        );
    }

    // ================= RESET PASSWORD =================
    @PostMapping("/reset-password")
    public Map<String, String> reset(@RequestParam String token,
                                    @RequestParam String newPassword) {

        authService.resetPassword(token, newPassword);

        return Map.of(
            "status", "success",
            "message", "Password updated successfully"
        );
    }

    // ================= CHANGE PASSWORD (authenticated) =================
    @PostMapping("/change-password")
    public org.springframework.http.ResponseEntity<?> changePassword(@RequestBody Map<String, String> body) {
        String email = body.get("email");
        String oldPassword = body.get("oldPassword");
        String newPassword = body.get("newPassword");
        try {
            authService.changePassword(email, oldPassword, newPassword);
            return org.springframework.http.ResponseEntity.ok(
                Map.of("status", "success", "message", "Password changed successfully")
            );
        } catch (Exception e) {
            return org.springframework.http.ResponseEntity.badRequest().body(
                Map.of("status", "error", "message", e.getMessage())
            );
        }
    }
}