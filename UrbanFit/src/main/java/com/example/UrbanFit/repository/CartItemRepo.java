package com.example.UrbanFit.repository;

import com.example.UrbanFit.models.CartItem;
import com.example.UrbanFit.models.Product;
import com.example.UrbanFit.models.Users;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.List;
import java.util.Optional;

public interface CartItemRepo extends JpaRepository<CartItem, Long> {
    List<CartItem> findByUser_Id(Long userId);
    Optional<CartItem> findByUserAndProduct(Users user, Product product);
    void deleteByUser_Id(Long userId);
}

