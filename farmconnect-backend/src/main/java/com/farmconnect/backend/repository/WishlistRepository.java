package com.farmconnect.backend.repository;

import java.util.List;
import java.util.Optional;
import org.springframework.data.jpa.repository.JpaRepository;
import com.farmconnect.backend.model.Wishlist;

public interface WishlistRepository extends JpaRepository<Wishlist, Long> {
    List<Wishlist> findByUserEmail(String userEmail);
    Optional<Wishlist> findByUserEmailAndProductId(String userEmail, Long productId);
    void deleteByUserEmailAndProductId(String userEmail, Long productId);
    boolean existsByUserEmailAndProductId(String userEmail, Long productId);
}
