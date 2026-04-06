package com.farmconnect.backend.repository;

import org.springframework.data.jpa.repository.JpaRepository;
import com.farmconnect.backend.model.Payment;

public interface PaymentRepository extends JpaRepository<Payment, Long> {
}