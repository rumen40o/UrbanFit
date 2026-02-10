package com.example.UrbanFit.Service;

import com.example.UrbanFit.Service.interfaces.ReviewServiceInterface;
import com.example.UrbanFit.models.DTO.review.CreateReviewRequest;
import com.example.UrbanFit.models.DTO.review.ReviewDto;
import com.example.UrbanFit.models.Product;
import com.example.UrbanFit.models.Review;
import com.example.UrbanFit.models.Users;
import com.example.UrbanFit.repository.ProductRepo;
import com.example.UrbanFit.repository.ReviewRepo;
import org.springframework.http.HttpStatus;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.web.server.ResponseStatusException;

import java.util.List;

@Service
@Transactional
public class ReviewService implements ReviewServiceInterface {

    private final ReviewRepo reviewRepo;
    private final ProductRepo productRepo;

    public ReviewService(ReviewRepo reviewRepo, ProductRepo productRepo) {
        this.reviewRepo = reviewRepo;
        this.productRepo = productRepo;
    }

    @Override
    @Transactional(readOnly = true)
    public List<ReviewDto> getByProduct(Long productId) {
        return reviewRepo.findByProduct_IdOrderByCreatedAtDesc(productId)
                .stream().map(this::toDto).toList();
    }

    @Override
    public ReviewDto createOrUpdate(Users user, CreateReviewRequest req) {
        Product product = productRepo.findById(req.getProductId())
            .orElseThrow(() -> new ResponseStatusException(HttpStatus.NOT_FOUND, "Product not found"));

        Review review = reviewRepo.findByUser_IdAndProduct_Id(user.getId(), product.getId())
                .orElse(null);

        if (review == null) {
            review = new Review(user, product, req.getRating(), req.getComment());
            reviewRepo.save(review);
        } else {
            review.setRating(req.getRating());
            review.setComment(req.getComment());
        }

        return toDto(review);
    }

    @Override
    public void deleteMine(Users user, Long productId) {
        reviewRepo.findByUser_IdAndProduct_Id(user.getId(), productId)
                .ifPresent(reviewRepo::delete);
    }

    private ReviewDto toDto(Review r) {
        return new ReviewDto(
                r.getId(),
                r.getProduct().getId(),
                r.getUser().getId(),
                r.getUser().getUsername(),
                r.getRating(),
                r.getComment(),
                r.getCreatedAt()
        );
    }
}