package com.farmconnect.backend.controller;

import java.util.List;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.*;

import com.farmconnect.backend.model.Product;
import com.farmconnect.backend.service.ProductService;

@RestController
@RequestMapping("/products")
@CrossOrigin(origins = "http://localhost:5173")
public class ProductController {

    @Autowired
    private ProductService productService;

    // ✅ ADD PRODUCT
    @PostMapping("/add")
    public Product addProduct(@RequestBody Product product) {
        return productService.addProduct(product);
    }

    // ✅ GET ALL (Buyer Dashboard)
    @GetMapping
    public List<Product> getAll() {
        return productService.getAllProducts();
    }

    // ✅ GET SELLER PRODUCTS
    @GetMapping("/seller/{email}")
    public List<Product> getBySeller(@PathVariable String email) {
        return productService.getByFarmer(email);
    }

    // ✅ GET ONE
    @GetMapping("/{id}")
    public Product getOne(@PathVariable Long id) {
        return productService.getProduct(id);
    }

    // 🔥 IMPORTANT → UPDATE (FIX EDIT BUTTON)
    @PutMapping("/{id}")
    public Product update(@PathVariable Long id, @RequestBody Product product) {
        return productService.updateProduct(id, product);
    }

    // ✅ DELETE
    @DeleteMapping("/{id}")
    public String delete(@PathVariable Long id) {
        return productService.deleteProduct(id);
    }

    // ✅ RATE PRODUCT
    @PostMapping("/{id}/rate")
    public Product rate(@PathVariable Long id, @RequestParam int rating) {
        return productService.rateProduct(id, rating);
    }
}