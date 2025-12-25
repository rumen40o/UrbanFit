package com.example.UrbanFit.Service.interfaces;

import com.example.UrbanFit.models.DTO.review.CreateReviewRequest;
import com.example.UrbanFit.models.DTO.review.ReviewDto;
import com.example.UrbanFit.models.Users;

import java.util.List;

public interface ReviewServiceInterface {
    List<ReviewDto> getByProduct(Long productId);
    ReviewDto createOrUpdate(Users user, CreateReviewRequest req);
    void deleteMine(Users user, Long productId);
}
