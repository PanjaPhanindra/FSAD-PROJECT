package com.farmconnect.backend.controller;

import java.util.List;
import java.util.Map;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import com.farmconnect.backend.model.Order;
import com.farmconnect.backend.service.OrderService;

@RestController
@RequestMapping("/orders")
@CrossOrigin(origins = "*")
public class OrderController {

    @Autowired
    private OrderService orderService;

    // ✅ PLACE ORDER (with items + address)
    @PostMapping("/place")
    public ResponseEntity<?> placeOrder(@RequestBody Map<String, Object> orderData) {
        try {
            Order order = orderService.placeOrder(orderData);
            return ResponseEntity.ok(order);
        } catch (Exception e) {
            return ResponseEntity.badRequest().body(Map.of("message", e.getMessage()));
        }
    }

    // ✅ GET BUYER ORDERS
    @GetMapping("/buyer/{email}")
    public List<Order> getBuyerOrders(@PathVariable String email) {
        return orderService.getOrders(email);
    }

    // ✅ GET SELLER ORDERS
    @GetMapping("/seller/{email}")
    public List<Order> getSellerOrders(@PathVariable String email) {
        return orderService.getSellerOrders(email);
    }

    // ✅ UPDATE ORDER STATUS (for seller)
    @PutMapping("/{id}/status")
    public ResponseEntity<?> updateStatus(@PathVariable Long id, @RequestParam String status) {
        try {
            Order order = orderService.updateStatus(id, status);
            return ResponseEntity.ok(order);
        } catch (Exception e) {
            return ResponseEntity.badRequest().body(Map.of("message", e.getMessage()));
        }
    }

    // ✅ CANCEL ORDER (for buyer - restores stock)
    @PutMapping("/{id}/cancel")
    public ResponseEntity<?> cancelOrder(@PathVariable Long id) {
        try {
            Order order = orderService.cancelOrder(id);
            return ResponseEntity.ok(order);
        } catch (Exception e) {
            return ResponseEntity.badRequest().body(Map.of("message", e.getMessage()));
        }
    }
}