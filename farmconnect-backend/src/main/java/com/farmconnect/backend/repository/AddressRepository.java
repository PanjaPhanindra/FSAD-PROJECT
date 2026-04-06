package com.farmconnect.backend.repository;

import java.util.List;
import org.springframework.data.jpa.repository.JpaRepository;
import com.farmconnect.backend.model.Address;

public interface AddressRepository extends JpaRepository<Address, Long> {

    List<Address> findByUserEmail(String email);
}