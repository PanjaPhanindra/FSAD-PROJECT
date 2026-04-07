package com.farmconnect.backend.repository;

import java.util.List;
import org.springframework.data.jpa.repository.JpaRepository;
import com.farmconnect.backend.model.OrderItem;

public interface OrderItemRepository extends JpaRepository<OrderItem, Long> {
    List<OrderItem> findBySellerEmail(String sellerEmail);
}
