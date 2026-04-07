package com.farmconnect.backend.service;

import java.util.List;
import java.util.Optional;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import com.farmconnect.backend.model.Address;
import com.farmconnect.backend.repository.AddressRepository;

@Service
public class AddressService {

    @Autowired
    private AddressRepository addressRepo;

    // ADD ADDRESS
    public Address addAddress(Address address) {
        return addressRepo.save(address);
    }

    // GET USER ADDRESSES
    public List<Address> getAddresses(String email) {
        return addressRepo.findByUserEmail(email);
    }

    // UPDATE ADDRESS
    public Address updateAddress(Long id, Address updated) {
        Optional<Address> existing = addressRepo.findById(id);
        if (existing.isEmpty()) {
            throw new RuntimeException("Address not found with id: " + id);
        }
        Address addr = existing.get();
        addr.setFullName(updated.getFullName());
        addr.setMobile(updated.getMobile());
        addr.setStreet(updated.getStreet());
        addr.setCity(updated.getCity());
        addr.setState(updated.getState());
        addr.setPincode(updated.getPincode());
        return addressRepo.save(addr);
    }

    // DELETE ADDRESS
    public void deleteAddress(Long id) {
        addressRepo.deleteById(id);
    }
}