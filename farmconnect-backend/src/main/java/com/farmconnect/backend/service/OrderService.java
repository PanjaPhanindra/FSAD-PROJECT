package com.farmconnect.backend.service;

import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import com.farmconnect.backend.model.CartItem;
import com.farmconnect.backend.model.Order;
import com.farmconnect.backend.model.OrderItem;
import com.farmconnect.backend.model.Product;
import com.farmconnect.backend.repository.CartRepository;
import com.farmconnect.backend.repository.OrderRepository;
import com.farmconnect.backend.repository.OrderItemRepository;
import com.farmconnect.backend.repository.ProductRepository;

@Service
public class OrderService {

    @Autowired
    private OrderRepository orderRepo;

    @Autowired
    private OrderItemRepository orderItemRepo;

    @Autowired
    private CartRepository cartRepo;

    @Autowired
    private ProductRepository productRepo;

    // ✅ PLACE ORDER (with items, address, stock reduction)
    @Transactional
    public Order placeOrder(Map<String, Object> orderData) {
        String email = (String) orderData.get("userEmail");

        // Get cart items for this user
        List<CartItem> cartItems = cartRepo.findByUserEmail(email);
        if (cartItems.isEmpty()) {
            throw new RuntimeException("Cart is empty");
        }

        // Create order
        Order order = new Order();
        order.setUserEmail(email);
        order.setStatus("placed");
        order.setCreatedAt(LocalDateTime.now());

        // Set shipping info
        Map<String, String> shipping = (Map<String, String>) orderData.get("shipping");
        if (shipping != null) {
            order.setShippingName(shipping.getOrDefault("fullName", ""));
            order.setShippingPhone(shipping.getOrDefault("phone", ""));
            order.setShippingAddress(shipping.getOrDefault("address", ""));
            order.setShippingCity(shipping.getOrDefault("city", ""));
            order.setShippingState(shipping.getOrDefault("state", ""));
            order.setShippingPincode(shipping.getOrDefault("pinCode", ""));
        }

        // Calculate total and create order items
        double total = 0;
        List<OrderItem> orderItems = new ArrayList<>();

        for (CartItem cartItem : cartItems) {
            // ✅ Data is now snapshotted in CartItem — no Product lookup needed
            if (cartItem.getProductId() == null) continue;

            OrderItem oi = new OrderItem();
            oi.setOrder(order);
            oi.setProductId(cartItem.getProductId());
            oi.setProductName(cartItem.getProductName());
            oi.setProductImage(cartItem.getProductImage());
            oi.setSellerEmail(cartItem.getSellerEmail());
            oi.setSellerName(cartItem.getSellerName());
            oi.setPrice(cartItem.getPrice());
            oi.setQuantity(cartItem.getQuantity());
            orderItems.add(oi);

            total += cartItem.getPrice() * cartItem.getQuantity();

            // ✅ REDUCE STOCK — still look up live product for accurate stock update
            Product product = productRepo.findById(cartItem.getProductId()).orElse(null);
            if (product != null) {
                int newStock = product.getStock() - cartItem.getQuantity();
                product.setStock(Math.max(0, newStock));
                product.setSoldCount(product.getSoldCount() + cartItem.getQuantity());
                productRepo.save(product);
            }
        }

        // Add tax (5%) and shipping
        double tax = Math.round(total * 0.05 * 100.0) / 100.0;
        double shippingCost = total > 500 ? 0 : 50;
        order.setTotalAmount(Math.round((total + tax + shippingCost) * 100.0) / 100.0);

        // Save order first
        Order savedOrder = orderRepo.save(order);

        // Save order items
        for (OrderItem oi : orderItems) {
            oi.setOrder(savedOrder);
        }
        orderItemRepo.saveAll(orderItems);

        // Clear cart
        cartRepo.deleteByUserEmail(email);

        // Reload to get items
        return orderRepo.findById(savedOrder.getId()).orElse(savedOrder);
    }

    // ✅ CANCEL ORDER (restore stock)
    @Transactional
    public Order cancelOrder(Long orderId) {
        Order order = orderRepo.findById(orderId)
            .orElseThrow(() -> new RuntimeException("Order not found"));

        if ("cancelled".equals(order.getStatus())) {
            throw new RuntimeException("Order already cancelled");
        }

        // Restore stock for each item
        if (order.getItems() != null) {
            for (OrderItem item : order.getItems()) {
                Product product = productRepo.findById(item.getProductId()).orElse(null);
                if (product != null) {
                    product.setStock(product.getStock() + item.getQuantity());
                    product.setSoldCount(Math.max(0, product.getSoldCount() - item.getQuantity()));
                    productRepo.save(product);
                }
            }
        }

        order.setStatus("cancelled");
        return orderRepo.save(order);
    }

    // ✅ UPDATE ORDER STATUS (for seller)
    public Order updateStatus(Long orderId, String status) {
        Order order = orderRepo.findById(orderId)
            .orElseThrow(() -> new RuntimeException("Order not found"));
        order.setStatus(status);
        return orderRepo.save(order);
    }

    // ✅ GET BUYER ORDERS
    public List<Order> getOrders(String email) {
        return orderRepo.findByUserEmail(email);
    }

    // ✅ GET SELLER ORDERS (orders containing their products)
    public List<Order> getSellerOrders(String sellerEmail) {
        List<OrderItem> sellerItems = orderItemRepo.findBySellerEmail(sellerEmail);
        // Get unique order IDs
        List<Long> orderIds = sellerItems.stream()
            .map(item -> item.getOrder().getId())
            .distinct()
            .collect(Collectors.toList());
        return orderRepo.findAllById(orderIds);
    }
}