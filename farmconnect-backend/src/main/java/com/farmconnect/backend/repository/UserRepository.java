package com.farmconnect.backend.repository;

import java.util.Optional;
import org.springframework.data.jpa.repository.JpaRepository;
import com.farmconnect.backend.model.User;

public interface UserRepository extends JpaRepository<User, Long> {

    Optional<User> findByEmail(String email);

    boolean existsByEmail(String email); // ✅ ADD THIS

    Optional<User> findByVerificationCode(String code);

    Optional<User> findByResetToken(String token);
}