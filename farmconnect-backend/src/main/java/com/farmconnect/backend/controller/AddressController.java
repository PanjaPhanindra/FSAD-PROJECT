package com.farmconnect.backend.controller;

import java.util.List;
import java.util.Map;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import com.farmconnect.backend.model.Address;
import com.farmconnect.backend.service.AddressService;

@RestController
@RequestMapping("/address")
@CrossOrigin(origins = "*")
public class AddressController {

    @Autowired
    private AddressService addressService;

    // ADD ADDRESS
    @PostMapping("/add")
    public Address add(@RequestBody Address address) {
        return addressService.addAddress(address);
    }

    // GET ADDRESS
    @GetMapping("/{email}")
    public List<Address> get(@PathVariable String email) {
        return addressService.getAddresses(email);
    }

    // UPDATE ADDRESS
    @PutMapping("/update/{id}")
    public ResponseEntity<?> update(@PathVariable Long id, @RequestBody Address address) {
        try {
            Address updated = addressService.updateAddress(id, address);
            return ResponseEntity.ok(updated);
        } catch (RuntimeException e) {
            return ResponseEntity.badRequest().body(Map.of("message", e.getMessage()));
        }
    }

    // DELETE ADDRESS
    @DeleteMapping("/delete/{id}")
    public ResponseEntity<?> delete(@PathVariable Long id) {
        try {
            addressService.deleteAddress(id);
            return ResponseEntity.ok(Map.of("message", "Address deleted"));
        } catch (Exception e) {
            return ResponseEntity.badRequest().body(Map.of("message", e.getMessage()));
        }
    }
}