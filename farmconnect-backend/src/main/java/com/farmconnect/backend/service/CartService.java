package com.farmconnect.backend.service;

import java.util.List;
import java.util.Optional;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import com.farmconnect.backend.model.CartItem;
import com.farmconnect.backend.model.Product;
import com.farmconnect.backend.repository.CartRepository;
import com.farmconnect.backend.repository.ProductRepository;

@Service
public class CartService {

    @Autowired
    private CartRepository cartRepo;

    @Autowired
    private ProductRepository productRepo;

    // ================= ADD TO CART =================
    public CartItem addToCart(CartItem dto) {
        // Look up the product to snapshot its details
        Product product = productRepo.findById(dto.getProductId())
                .orElseThrow(() -> new RuntimeException("Product not found: " + dto.getProductId()));

        // If same product already in cart → just increase quantity
        Optional<CartItem> existingItem = cartRepo.findByUserEmailAndProductId(
                dto.getUserEmail(), dto.getProductId());

        if (existingItem.isPresent()) {
            CartItem item = existingItem.get();
            item.setQuantity(item.getQuantity() + dto.getQuantity());
            return cartRepo.save(item);
        }

        // Build a new cart item with snapshotted product data
        CartItem item = new CartItem();
        item.setUserEmail(dto.getUserEmail());
        item.setProductId(product.getId());
        item.setProductName(product.getName());
        item.setProductImage(product.getImage());
        item.setSellerEmail(product.getFarmerEmail());
        item.setSellerName(product.getSellerName());
        item.setPrice(product.getPrice());
        item.setStock(product.getStock());
        item.setQuantity(dto.getQuantity());
        return cartRepo.save(item);
    }

    // ================= GET USER CART =================
    public List<CartItem> getCart(String email) {
        return cartRepo.findByUserEmail(email);
    }

    // ================= REMOVE ITEM =================
    public void removeCartItem(Long itemId) {
        cartRepo.deleteById(itemId);
    }

    // ================= UPDATE QUANTITY =================
    public CartItem updateQuantity(Long itemId, int quantity) {
        CartItem item = cartRepo.findById(itemId)
                .orElseThrow(() -> new RuntimeException("Item not found"));
        item.setQuantity(quantity);
        return cartRepo.save(item);
    }

    // ================= CLEAR CART =================
    @Transactional
    public String clearCart(String email) {
        cartRepo.deleteByUserEmail(email);
        return "Cart cleared";
    }
}