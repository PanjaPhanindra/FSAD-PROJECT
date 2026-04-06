package com.farmconnect.backend.repository;

import java.util.List;
import org.springframework.data.jpa.repository.JpaRepository;
import com.farmconnect.backend.model.CartItem;

public interface CartRepository extends JpaRepository<CartItem, Long> {

    List<CartItem> findByUserEmail(String email);

    void deleteByUserEmail(String email);
}