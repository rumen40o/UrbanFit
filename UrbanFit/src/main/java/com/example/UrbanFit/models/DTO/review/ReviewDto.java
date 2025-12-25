package com.example.UrbanFit.models.DTO.review;

import java.time.Instant;

public class ReviewDto {
    private Long id;
    private Long productId;
    private Long userId;
    private String username;
    private Integer rating;
    private String comment;
    private Instant createdAt;

    public ReviewDto() {}

    public ReviewDto(Long id, Long productId, Long userId, String username,
                     Integer rating, String comment, Instant createdAt) {
        this.id = id; this.productId = productId; this.userId = userId; this.username = username;
        this.rating = rating; this.comment = comment; this.createdAt = createdAt;
    }

    public Long getId() { return id; }
    public Long getProductId() { return productId; }
    public Long getUserId() { return userId; }
    public String getUsername() { return username; }
    public Integer getRating() { return rating; }
    public String getComment() { return comment; }
    public Instant getCreatedAt() { return createdAt; }
}
