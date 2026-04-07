package com.farmconnect.backend.controller;

import java.util.List;
import java.util.Map;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import com.farmconnect.backend.model.Product;
import com.farmconnect.backend.model.Wishlist;
import com.farmconnect.backend.service.WishlistService;

@RestController
@RequestMapping("/wishlist")
@CrossOrigin(origins = "*")
public class WishlistController {

    @Autowired
    private WishlistService wishlistService;

    // ✅ ADD TO WISHLIST
    @PostMapping("/add")
    public ResponseEntity<?> add(@RequestBody Map<String, Object> body) {
        try {
            String userEmail = (String) body.get("userEmail");
            Long productId = Long.valueOf(body.get("productId").toString());
            Wishlist w = wishlistService.addToWishlist(userEmail, productId);
            return ResponseEntity.ok(w);
        } catch (Exception e) {
            return ResponseEntity.badRequest().body(Map.of("message", e.getMessage()));
        }
    }

    // ✅ REMOVE FROM WISHLIST
    @DeleteMapping("/remove")
    public ResponseEntity<?> remove(@RequestParam String userEmail, @RequestParam Long productId) {
        try {
            wishlistService.removeFromWishlist(userEmail, productId);
            return ResponseEntity.ok(Map.of("message", "Removed from wishlist"));
        } catch (Exception e) {
            return ResponseEntity.badRequest().body(Map.of("message", e.getMessage()));
        }
    }

    // ✅ GET WISHLIST (returns full product objects)
    @GetMapping("/{email}")
    public List<Product> getWishlist(@PathVariable String email) {
        return wishlistService.getWishlistProducts(email);
    }

    // ✅ CHECK IF PRODUCT IS IN WISHLIST
    @GetMapping("/check")
    public Map<String, Boolean> check(@RequestParam String userEmail, @RequestParam Long productId) {
        return Map.of("inWishlist", wishlistService.isInWishlist(userEmail, productId));
    }
}
