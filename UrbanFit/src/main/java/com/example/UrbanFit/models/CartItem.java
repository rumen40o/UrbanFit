package com.example.UrbanFit.models;

import jakarta.persistence.*;
import jakarta.validation.constraints.Min;

import java.time.Instant;

@Entity
@Table(
        name = "cart_items",
        uniqueConstraints = @UniqueConstraint(
                name = "uq_cart_user_variant",
                columnNames = {"user_id", "variant_id"}
        ),
        indexes = {
                @Index(name = "idx_cart_user", columnList = "user_id"),
                @Index(name = "idx_cart_variant", columnList = "variant_id")
        }
)
public class CartItem {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @ManyToOne(fetch = FetchType.LAZY, optional = false)
    @JoinColumn(
            name = "user_id",
            nullable = false,
            foreignKey = @ForeignKey(name = "fk_cart_user")
    )
    private Users user;

    @ManyToOne(fetch = FetchType.LAZY, optional = false)
    @JoinColumn(
            name = "variant_id",
            nullable = false,
            foreignKey = @ForeignKey(name = "fk_cart_variant")
    )
    private ProductVariant variant;

    @Min(1)
    @Column(nullable = false)
    private Integer quantity;

    @Column(nullable = false, updatable = false)
    private Instant createdAt;

    protected CartItem() {}

    public CartItem(Users user, ProductVariant variant, Integer quantity) {
        this.user = user;
        this.variant = variant;
        this.quantity = quantity;
    }

    @PrePersist
    void prePersist() {
        createdAt = Instant.now();
    }

    // getters/setters
    public Long getId() { return id; }

    public Users getUser() { return user; }
    public void setUser(Users user) { this.user = user; }

    public ProductVariant getVariant() { return variant; }
    public void setVariant(ProductVariant variant) { this.variant = variant; }

    public Integer getQuantity() { return quantity; }
    public void setQuantity(Integer quantity) { this.quantity = quantity; }

    public Instant getCreatedAt() { return createdAt; }
}
