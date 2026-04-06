package com.farmconnect.backend.service;

import java.util.List;

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
}