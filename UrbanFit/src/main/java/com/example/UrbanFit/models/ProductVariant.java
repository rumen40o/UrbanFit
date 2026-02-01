package com.example.UrbanFit.models;

import jakarta.persistence.*;

@Entity
@Table(name = "product_variants",
        uniqueConstraints = @UniqueConstraint(name="uk_variant_product_size", columnNames={"product_id","size"}))
public class ProductVariant {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @ManyToOne(fetch = FetchType.LAZY, optional = false)
    @JoinColumn(name = "product_id", nullable = false,
            foreignKey = @ForeignKey(name="fk_variant_product"))
    private Product product;

    @Column(nullable = false, length = 10)
    private String size; // "S","M","L" или "41","42"

    @Column(nullable = false)
    private Integer stockQty = 0;

    public ProductVariant() {}

    public Long getId() {
        return id;
    }
    public void setId(Long id) {
        this.id = id;
    }
    public Product getProduct() {
        return product;
    }
    public void setProduct(Product product) {
        this.product = product;
    }
    public String getSize() {
        return size;
    }
    public void setSize(String size) {
        this.size = size;
    }
    public Integer getStockQty() {
        return stockQty;
    }
    public void setStockQty(Integer stockQty) {
        this.stockQty = stockQty;
    }
}

