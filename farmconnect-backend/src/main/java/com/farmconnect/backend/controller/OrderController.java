package com.farmconnect.backend.controller;

import java.util.List;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.*;

import com.farmconnect.backend.model.Order;
import com.farmconnect.backend.service.OrderService;

@RestController
@RequestMapping("/orders")
@CrossOrigin(origins = "http://localhost:5173")
public class OrderController {

    @Autowired
    private OrderService orderService;

    // PLACE ORDER
    @PostMapping("/{email}")
    public Order place(@PathVariable String email) {
        return orderService.placeOrder(email);
    }

    // GET ORDERS
    @GetMapping("/{email}")
    public List<Order> get(@PathVariable String email) {
        return orderService.getOrders(email);
    }
}