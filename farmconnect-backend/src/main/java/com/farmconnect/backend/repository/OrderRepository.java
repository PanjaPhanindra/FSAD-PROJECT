package com.farmconnect.backend.repository;

import java.util.List;
import org.springframework.data.jpa.repository.JpaRepository;
import com.farmconnect.backend.model.Order;

public interface OrderRepository extends JpaRepository<Order, Long> {

    List<Order> findByUserEmail(String email);
}