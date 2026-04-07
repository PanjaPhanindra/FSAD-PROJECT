package com.farmconnect.backend.controller;

import java.util.List;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.*;

import com.farmconnect.backend.model.CartItem;
import com.farmconnect.backend.service.CartService;

@RestController
@RequestMapping("/cart")
@CrossOrigin(origins = "http://localhost:5173")
public class CartController {

    @Autowired
    private CartService cartService;

    // ADD TO CART
    @PostMapping("/add")
    public CartItem add(@RequestBody CartItem item) {
        return cartService.addToCart(item);
    }

    // VIEW CART
    @GetMapping("/{email}")
    public List<CartItem> get(@PathVariable String email) {
        return cartService.getCart(email);
    }

    // REMOVE ITEM
    @DeleteMapping("/remove/{itemId}")
    public void removeItem(@PathVariable Long itemId) {
        cartService.removeCartItem(itemId);
    }

    // UPDATE QUANTITY
    @PutMapping("/update/{itemId}")
    public CartItem updateQuantity(@PathVariable Long itemId, @RequestParam int quantity) {
        return cartService.updateQuantity(itemId, quantity);
    }

    // CLEAR CART
    @DeleteMapping("/{email}")
    public String clear(@PathVariable String email) {
        return cartService.clearCart(email);
    }
}