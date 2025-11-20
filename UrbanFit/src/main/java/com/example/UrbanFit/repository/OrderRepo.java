package com.example.UrbanFit.repository;

import com.example.UrbanFit.models.Order;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.List;
import java.util.Optional;

public interface OrderRepo extends JpaRepository<Order, Long> {
    List<Order> findByUser_IdOrderByCreatedAtDesc(Long userId);
    Optional<Order> findByIdAndUser_Id(Long id, Long userId);
}
