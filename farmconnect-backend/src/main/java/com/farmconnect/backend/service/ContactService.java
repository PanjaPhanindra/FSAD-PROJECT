package com.farmconnect.backend.service;

import com.farmconnect.backend.model.ContactMessage;
import com.farmconnect.backend.model.ContactPending;
import com.farmconnect.backend.repository.ContactMessageRepository;
import com.farmconnect.backend.repository.ContactPendingRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.time.LocalDateTime;
import java.util.Random;

@Service
public class ContactService {

    @Autowired
    private ContactMessageRepository messageRepo;

    @Autowired
    private ContactPendingRepository pendingRepo;

    @Autowired
    private EmailService emailService;

    // ========== STEP 1: SEND OTP TO EMAIL ==========
    @Transactional
    public void sendVerification(String email) {

        // Generate 6-digit OTP
        String otp = String.format("%06d", new Random().nextInt(999999));

        // Upsert: delete old pending if exists, then create new
        pendingRepo.deleteByEmail(email);

        ContactPending pending = new ContactPending();
        pending.setEmail(email);
        pending.setOtp(otp);
        pending.setExpiresAt(LocalDateTime.now().plusMinutes(10));
        pendingRepo.save(pending);

        // Send OTP email
        emailService.sendEmail(
            email,
            "FarmConnect – Verify Your Contact Form Submission",
            "Hello!\n\n" +
            "You requested to send a message through the FarmConnect Contact Us form.\n\n" +
            "Your verification code is:\n\n" +
            "  " + otp + "\n\n" +
            "This code expires in 10 minutes.\n\n" +
            "If you did not request this, you can safely ignore this email.\n\n" +
            "— FarmConnect Support Team"
        );
    }

    // ========== STEP 2: VERIFY OTP + SAVE MESSAGE ==========
    @Transactional
    public void submitMessage(String name, String email, String message, String otp) {

        // Find pending record
        ContactPending pending = pendingRepo.findByEmail(email)
            .orElseThrow(() -> new RuntimeException("No verification pending for this email. Please request a new code."));

        // Check expiry
        if (LocalDateTime.now().isAfter(pending.getExpiresAt())) {
            pendingRepo.deleteByEmail(email);
            throw new RuntimeException("Verification code has expired. Please request a new one.");
        }

        // Check OTP match
        if (!pending.getOtp().equals(otp.trim())) {
            throw new RuntimeException("Invalid verification code. Please check your email and try again.");
        }

        // Save the contact message
        ContactMessage contactMessage = new ContactMessage();
        contactMessage.setName(name);
        contactMessage.setEmail(email);
        contactMessage.setMessage(message);
        messageRepo.save(contactMessage);

        // Clean up pending
        pendingRepo.deleteByEmail(email);

        // Send confirmation email to user
        emailService.sendEmail(
            email,
            "FarmConnect – We Received Your Message!",
            "Hi " + name + ",\n\n" +
            "Thank you for contacting FarmConnect! We've received your message and our team will get back to you shortly.\n\n" +
            "Your message:\n\"" + message + "\"\n\n" +
            "We typically respond within 1-2 business days.\n\n" +
            "— FarmConnect Support Team"
        );
    }
}
