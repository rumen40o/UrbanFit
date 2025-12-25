package com.example.UrbanFit.repository;

import com.example.UrbanFit.models.Review;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.List;
import java.util.Optional;

public interface ReviewRepo extends JpaRepository<Review, Long> {
    List<Review> findByProduct_IdOrderByCreatedAtDesc(Long productId);
    Optional<Review> findByUser_IdAndProduct_Id(Long userId, Long productId);
}
