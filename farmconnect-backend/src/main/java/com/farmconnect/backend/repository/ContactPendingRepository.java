package com.farmconnect.backend.repository;

import com.farmconnect.backend.model.ContactPending;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.Optional;

public interface ContactPendingRepository extends JpaRepository<ContactPending, Long> {

    Optional<ContactPending> findByEmail(String email);

    void deleteByEmail(String email);
}
