package com.farmconnect.backend.controller;

import com.farmconnect.backend.service.ContactService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.Map;

@RestController
@RequestMapping("/contact")
@CrossOrigin(origins = {"http://localhost:5173", "http://localhost:5178"})
public class ContactController {

    @Autowired
    private ContactService contactService;

    /**
     * STEP 1 — Send OTP to the user's email
     * Body: { "email": "user@example.com" }
     */
    @PostMapping("/send-verification")
    public ResponseEntity<?> sendVerification(@RequestBody Map<String, String> body) {
        String email = body.get("email");
        if (email == null || email.isBlank()) {
            return ResponseEntity.badRequest().body(Map.of("status", "error", "message", "Email is required."));
        }
        try {
            contactService.sendVerification(email);
            return ResponseEntity.ok(Map.of(
                "status", "success",
                "message", "Verification code sent to " + email
            ));
        } catch (Exception e) {
            return ResponseEntity.badRequest().body(Map.of("status", "error", "message", e.getMessage()));
        }
    }

    /**
     * STEP 2 — Verify OTP and save the message
     * Body: { "name": "...", "email": "...", "message": "...", "otp": "123456" }
     */
    @PostMapping("/submit")
    public ResponseEntity<?> submit(@RequestBody Map<String, String> body) {
        String name    = body.get("name");
        String email   = body.get("email");
        String message = body.get("message");
        String otp     = body.get("otp");

        if (name == null || email == null || message == null || otp == null) {
            return ResponseEntity.badRequest().body(
                Map.of("status", "error", "message", "All fields are required.")
            );
        }

        try {
            contactService.submitMessage(name, email, message, otp);
            return ResponseEntity.ok(Map.of(
                "status", "success",
                "message", "Your message has been sent! We'll get back to you soon."
            ));
        } catch (Exception e) {
            return ResponseEntity.badRequest().body(Map.of("status", "error", "message", e.getMessage()));
        }
    }
}
