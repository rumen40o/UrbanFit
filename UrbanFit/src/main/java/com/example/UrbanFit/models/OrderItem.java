package com.example.UrbanFit.models;

import jakarta.persistence.*;
import jakarta.validation.constraints.Min;

@Entity
@Table(name = "order_items", indexes = {
        @Index(name = "idx_order_items_order", columnList = "order_id")
})
public class OrderItem {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @ManyToOne(fetch = FetchType.LAZY, optional = false)
    @JoinColumn(
            name = "order_id",
            nullable = false,
            foreignKey = @ForeignKey(name = "fk_order_items_order")
    )
    private Order order;

    // ---- SNAPSHOT ----
    @Column(nullable = false)
    private Long productId;

    @Column(nullable = false, length = 255)
    private String productName;

    @Column(nullable = false)
    private Long variantId;

    @Column(nullable = false, length = 20)
    private String size;

    @Column(nullable = false, length = 30)
    private String type;

    @Column(nullable = false, length = 10)
    private String audience;

    @Min(0)
    @Column(nullable = false)
    private double unitPrice;

    @Min(1)
    @Column(nullable = false)
    private Integer quantity;

    protected OrderItem() {}

    public OrderItem(
            Long productId,
            String productName,
            Long variantId,
            String size,
            String type,
            String audience,
            double unitPrice,
            Integer quantity
    ) {
        this.productId = productId;
        this.productName = productName;
        this.variantId = variantId;
        this.size = size;
        this.type = type;
        this.audience = audience;
        this.unitPrice = unitPrice;
        this.quantity = quantity;
    }

    // getters/setters
    public Long getId() { return id; }

    public Order getOrder() { return order; }
    public void setOrder(Order order) { this.order = order; }

    public Long getProductId() { return productId; }
    public String getProductName() { return productName; }

    public Long getVariantId() { return variantId; }
    public String getSize() { return size; }

    public String getType() { return type; }
    public String getAudience() { return audience; }

    public double getUnitPrice() { return unitPrice; }
    public void setUnitPrice(double unitPrice) { this.unitPrice = unitPrice; }

    public Integer getQuantity() { return quantity; }
    public void setQuantity(Integer quantity) { this.quantity = quantity; }
}
