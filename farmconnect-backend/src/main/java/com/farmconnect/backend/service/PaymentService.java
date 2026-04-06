package com.farmconnect.backend.service;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import com.farmconnect.backend.model.Payment;
import com.farmconnect.backend.repository.PaymentRepository;

@Service
public class PaymentService {

    @Autowired
    private PaymentRepository paymentRepo;

    public Payment makePayment(Payment payment) {
        payment.setStatus("SUCCESS"); // demo
        return paymentRepo.save(payment);
    }
}