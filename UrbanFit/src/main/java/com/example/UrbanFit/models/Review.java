package com.example.UrbanFit.models;

import jakarta.persistence.*;
import jakarta.validation.constraints.Max;
import jakarta.validation.constraints.Min;
import java.time.Instant;

@Entity
@Table(name = "reviews",
        uniqueConstraints = @UniqueConstraint(name="uq_review_user_product", columnNames={"user_id","product_id"}),
        indexes = @Index(name="idx_reviews_product", columnList="product_id"))
public class Review {

    @Id @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @ManyToOne(fetch = FetchType.LAZY, optional = false)
    @JoinColumn(name="user_id", nullable=false, foreignKey=@ForeignKey(name="fk_review_user"))
    private Users user;

    @ManyToOne(fetch = FetchType.LAZY, optional = false)
    @JoinColumn(name="product_id", nullable=false, foreignKey=@ForeignKey(name="fk_review_product"))
    private Product product;

    @Min(1) @Max(5)
    @Column(nullable=false)
    private Integer rating;

    @Column(length = 2000)
    private String comment;

    @Column(nullable=false, updatable=false)
    private Instant createdAt;

    protected Review() {}

    public Review(Users user, Product product, Integer rating, String comment) {
        this.user = user;
        this.product = product;
        this.rating = rating;
        this.comment = comment;
    }

    @PrePersist
    void prePersist() { createdAt = Instant.now(); }

    public Long getId() { return id; }
    public Users getUser() { return user; }
    public Product getProduct() { return product; }
    public Integer getRating() { return rating; }
    public void setRating(Integer rating) { this.rating = rating; }
    public String getComment() { return comment; }
    public void setComment(String comment) { this.comment = comment; }
    public Instant getCreatedAt() { return createdAt; }
}
