package com.farmconnect.backend.service;

import java.util.List;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import com.farmconnect.backend.model.Product;
import com.farmconnect.backend.repository.ProductRepository;

@Service
public class ProductService {

    @Autowired
    private ProductRepository productRepo;

    @Autowired
    private org.springframework.jdbc.core.JdbcTemplate jdbcTemplate;

    @org.springframework.context.event.EventListener(org.springframework.boot.context.event.ApplicationReadyEvent.class)
    public void fixSchema() {
        try {
            jdbcTemplate.execute("ALTER TABLE product MODIFY image LONGTEXT");
            jdbcTemplate.execute("ALTER TABLE product MODIFY description VARCHAR(1000)");
            System.out.println("✅ Schema Auto-Fix: image & description columns fixed!");
        } catch (Exception e) {
            System.out.println("❌ Schema fix (image/desc): " + e.getMessage());
        }
        try {
            // Drop stale 'quantity' column that doesn't belong in the Product entity
            jdbcTemplate.execute("ALTER TABLE product DROP COLUMN quantity");
            System.out.println("✅ Schema Auto-Fix: dropped stale 'quantity' column from product!");
        } catch (Exception e) {
            // Column may not exist — that's fine
            System.out.println("ℹ️ quantity column: " + e.getMessage());
        }
    }

    // ✅ ADD PRODUCT
    public Product addProduct(Product product) {
        return productRepo.save(product);
    }

    // ✅ GET ALL
    public List<Product> getAllProducts() {
        return productRepo.findAll();
    }

    // ✅ GET BY SELLER
    public List<Product> getByFarmer(String email) {
        return productRepo.findByFarmerEmail(email);
    }

    // ✅ GET ONE
    public Product getProduct(Long id) {
        return productRepo.findById(id)
                .orElseThrow(() -> new RuntimeException("Product not found"));
    }

    // ✅ DELETE
    public String deleteProduct(Long id) {
        productRepo.deleteById(id);
        return "Deleted successfully";
    }

    // 🔥 IMPORTANT FIX → UPDATE (EDIT NOT WORKING FIX)
    public Product updateProduct(Long id, Product updatedProduct) {
        Product p = getProduct(id);

        p.setName(updatedProduct.getName());
        p.setDescription(updatedProduct.getDescription());
        p.setPrice(updatedProduct.getPrice());
        p.setStock(updatedProduct.getStock());
        p.setCategory(updatedProduct.getCategory());
        p.setImage(updatedProduct.getImage());
        p.setSellerName(updatedProduct.getSellerName());
        p.setFarmerEmail(updatedProduct.getFarmerEmail());

        return productRepo.save(p);
    }

    // ✅ RATE PRODUCT (running average)
    public Product rateProduct(Long id, int rating) {
        Product p = getProduct(id);
        int total = p.getTotalRatings();
        double currentAvg = p.getRating();
        double newAvg = ((currentAvg * total) + rating) / (total + 1);
        p.setRating(Math.round(newAvg * 10.0) / 10.0);
        p.setTotalRatings(total + 1);
        return productRepo.save(p);
    }
}