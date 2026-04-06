package com.farmconnect.backend.service;

import java.util.List;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import com.farmconnect.backend.model.CartItem;
import com.farmconnect.backend.model.Order;
import com.farmconnect.backend.repository.CartRepository;
import com.farmconnect.backend.repository.OrderRepository;

@Service
public class OrderService {

    @Autowired
    private OrderRepository orderRepo;

    @Autowired
    private CartRepository cartRepo;

    // PLACE ORDER
    public Order placeOrder(String email) {

        List<CartItem> cartItems = cartRepo.findByUserEmail(email);

        double total = 0;

        for (CartItem item : cartItems) {
            total += item.getQuantity() * 100; // simple price logic
        }

        Order order = new Order();
        order.setUserEmail(email);
        order.setTotalAmount(total);

        orderRepo.save(order);

        // clear cart
        cartRepo.deleteByUserEmail(email);

        return order;
    }

    // GET USER ORDERS
    public List<Order> getOrders(String email) {
        return orderRepo.findByUserEmail(email);
    }
}