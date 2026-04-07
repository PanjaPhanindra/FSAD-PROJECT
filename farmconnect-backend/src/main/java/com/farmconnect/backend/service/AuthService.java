package com.farmconnect.backend.service;

import java.util.UUID;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;

import com.farmconnect.backend.dto.LoginRequest;
import com.farmconnect.backend.dto.RegisterRequest;
import com.farmconnect.backend.model.User;
import com.farmconnect.backend.repository.UserRepository;

@Service
public class AuthService {

    @Autowired
    private UserRepository userRepo;

    @Autowired
    private PasswordEncoder passwordEncoder;

    @Autowired
    private EmailService emailService;

    // ================= REGISTER =================
    public String register(RegisterRequest req) {

        if (userRepo.existsByEmail(req.getEmail())) {
            throw new RuntimeException("Email already exists");
        }

        User user = new User();
        user.setName(req.getName());
        user.setEmail(req.getEmail());
        user.setPassword(passwordEncoder.encode(req.getPassword()));
        user.setRole(req.getRole());

        // 🔥 IMPORTANT FIX
        String code = UUID.randomUUID().toString();
        user.setVerificationCode(code);
        user.setVerified(false);   // ✅ MUST ADD

        userRepo.save(user);

        // 🔥 EMAIL LINK (backend verify API)
        emailService.sendEmail(
            user.getEmail(),
            "Verify Account",
            "Click to verify: http://localhost:5173/auth/verify?code=" + code
        );

        return "Registration successful. Check email.";
    }

    // ================= VERIFY =================
    public String verify(String code) {

        User user = userRepo.findByVerificationCode(code)
                .orElseThrow(() -> new RuntimeException("Invalid verification code"));

        user.setVerified(true);
        user.setVerificationCode(null);

        userRepo.save(user);

        return "Account verified successfully";
    }

    // ================= LOGIN =================
    public User login(LoginRequest req) {

        User user = userRepo.findByEmail(req.getEmail())
                .orElseThrow(() -> new RuntimeException("User not found"));

        if (!user.isVerified()) {
            throw new RuntimeException("Please verify your email first");
        }

        if (!passwordEncoder.matches(req.getPassword(), user.getPassword())) {
            throw new RuntimeException("Invalid password");
        }

        return user;
    }

    // ================= FORGOT PASSWORD =================
    public String forgotPassword(String email) {

        User user = userRepo.findByEmail(email)
                .orElseThrow(() -> new RuntimeException("User not found"));

        String token = UUID.randomUUID().toString();
        user.setResetToken(token);
        userRepo.save(user);

        emailService.sendEmail(
            email,
            "Reset Password",
            "Click: http://localhost:5173/reset-password?token=" + token
        );

        return "Reset email sent";
    }

    // ================= RESET PASSWORD =================
    public String resetPassword(String token, String newPassword) {

        User user = userRepo.findByResetToken(token)
                .orElseThrow(() -> new RuntimeException("Invalid token"));

        user.setPassword(passwordEncoder.encode(newPassword));
        user.setResetToken(null);

        userRepo.save(user);

        return "Password updated successfully";
    }

    // ================= CHANGE PASSWORD (authenticated) =================
    public String changePassword(String email, String oldPassword, String newPassword) {

        User user = userRepo.findByEmail(email)
                .orElseThrow(() -> new RuntimeException("User not found"));

        if (!passwordEncoder.matches(oldPassword, user.getPassword())) {
            throw new RuntimeException("Old password is incorrect");
        }

        if (newPassword == null || newPassword.length() < 6) {
            throw new RuntimeException("New password must be at least 6 characters");
        }

        user.setPassword(passwordEncoder.encode(newPassword));
        userRepo.save(user);

        return "Password changed successfully";
    }
}