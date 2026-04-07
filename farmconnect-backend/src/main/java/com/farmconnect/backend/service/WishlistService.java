package com.farmconnect.backend.service;

import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import com.farmconnect.backend.model.Product;
import com.farmconnect.backend.model.Wishlist;
import com.farmconnect.backend.repository.ProductRepository;
import com.farmconnect.backend.repository.WishlistRepository;

@Service
public class WishlistService {

    @Autowired
    private WishlistRepository wishlistRepo;

    @Autowired
    private ProductRepository productRepo;

    // ✅ ADD TO WISHLIST
    public Wishlist addToWishlist(String userEmail, Long productId) {
        if (wishlistRepo.existsByUserEmailAndProductId(userEmail, productId)) {
            return wishlistRepo.findByUserEmailAndProductId(userEmail, productId).orElseThrow();
        }
        Wishlist w = new Wishlist();
        w.setUserEmail(userEmail);
        w.setProductId(productId);
        return wishlistRepo.save(w);
    }

    // ✅ REMOVE FROM WISHLIST
    @Transactional
    public void removeFromWishlist(String userEmail, Long productId) {
        wishlistRepo.deleteByUserEmailAndProductId(userEmail, productId);
    }

    // ✅ GET WISHLIST PRODUCTS
    public List<Product> getWishlistProducts(String userEmail) {
        List<Wishlist> items = wishlistRepo.findByUserEmail(userEmail);
        List<Long> ids = items.stream().map(Wishlist::getProductId).collect(Collectors.toList());
        return productRepo.findAllById(ids);
    }

    // ✅ CHECK IF IN WISHLIST
    public boolean isInWishlist(String userEmail, Long productId) {
        return wishlistRepo.existsByUserEmailAndProductId(userEmail, productId);
    }
}
