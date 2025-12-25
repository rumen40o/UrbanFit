package com.example.UrbanFit.controller;

import com.example.UrbanFit.Service.interfaces.ReviewServiceInterface;
import com.example.UrbanFit.models.DTO.review.CreateReviewRequest;
import com.example.UrbanFit.models.DTO.review.ReviewDto;
import com.example.UrbanFit.models.Users;
import jakarta.validation.Valid;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/reviews")
@CrossOrigin(origins = "http://localhost:3000", allowCredentials = "true")
public class ReviewController {

    private final ReviewServiceInterface service;

    public ReviewController(ReviewServiceInterface service) {
        this.service = service;
    }

    // Публично: всички ревюта за продукт
    @GetMapping("/product/{productId}")
    public List<ReviewDto> getForProduct(@PathVariable Long productId) {
        return service.getByProduct(productId);
    }

    // Логнат: create/update (1 ревю на продукт за user)
    @PostMapping
    @PreAuthorize("isAuthenticated()")
    public ReviewDto createOrUpdate(@AuthenticationPrincipal Users user,
                                    @Valid @RequestBody CreateReviewRequest req) {
        return service.createOrUpdate(user, req);
    }

    // Логнат: delete моето ревю
    @DeleteMapping("/product/{productId}")
    @PreAuthorize("isAuthenticated()")
    public ResponseEntity<Void> deleteMine(@AuthenticationPrincipal Users user,
                                           @PathVariable Long productId) {
        service.deleteMine(user, productId);
        return ResponseEntity.noContent().build();
    }
}
