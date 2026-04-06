package com.farmconnect.backend.repository;

import java.util.List;
import org.springframework.data.jpa.repository.JpaRepository;
import com.farmconnect.backend.model.Product;

public interface ProductRepository extends JpaRepository<Product, Long> {

    // ✅ Seller products
    List<Product> findByFarmerEmail(String email);
}