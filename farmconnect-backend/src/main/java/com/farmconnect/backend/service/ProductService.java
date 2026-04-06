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
}