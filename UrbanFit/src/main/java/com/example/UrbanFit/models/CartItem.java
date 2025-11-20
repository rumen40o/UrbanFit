package com.example.UrbanFit.models;
import jakarta.persistence.*;
import jakarta.validation.constraints.Min;
import java.time.Instant;

@Entity
@Table(name = "cart_items",
        uniqueConstraints = @UniqueConstraint(name = "uq_cart_user_product",
                columnNames = {"user_id","product_id"}),
        indexes = @Index(name = "idx_cart_user", columnList = "user_id"))

public class CartItem {

    @Id @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @ManyToOne(fetch = FetchType.LAZY, optional = false)
    @JoinColumn(name = "user_id", nullable = false,
            foreignKey = @ForeignKey(name = "fk_cart_user"))
    private Users user;

    @ManyToOne(fetch = FetchType.LAZY, optional = false)
    @JoinColumn(name = "product_id", nullable = false,
            foreignKey = @ForeignKey(name = "fk_cart_product"))
    private Product product;

    @Min(1)
    @Column(nullable = false)
    private Integer quantity;

    @Column(nullable = false, updatable = false)
    private Instant createdAt;

    protected CartItem() {}

    public CartItem(Users user, Product product, Integer quantity) {
        this.user = user; this.product = product; this.quantity = quantity;
    }

    @PrePersist void prePersist() { createdAt = Instant.now(); }

    // getters/setters
    public Long getId() { return id; }
    public Users getUser() { return user; }
    public void setUser(Users user) { this.user = user; }
    public Product getProduct() { return product; }
    public void setProduct(Product product) { this.product = product; }
    public Integer getQuantity() { return quantity; }
    public void setQuantity(Integer quantity) { this.quantity = quantity; }
    public Instant getCreatedAt() { return createdAt; }
}