package com.example.UrbanFit.repository;

import com.example.UrbanFit.models.Users;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.Optional;

public interface UserRepo extends JpaRepository<Users, Long> {
    Optional<Users> findByUsername(String username);
    Optional<Users> findByEmail(String email);
    Optional<Users> findByUsernameIgnoreCase(String username);
}
