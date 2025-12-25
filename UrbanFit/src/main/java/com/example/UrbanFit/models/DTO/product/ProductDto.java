package com.example.UrbanFit.models.DTO.product;

import java.time.Instant;

public class ProductDto {
    private Long id;
    private String name;
    private String description;
    private double price;
    private String imageUrl;
    private Long categoryId;
    private Integer stockQty;
    private boolean active;
    private Instant createdAt;

    public ProductDto() {}

    public ProductDto(Long id, String name, String description, double price,
                      String imageUrl, Long categoryId, Integer stockQty,
                      boolean active, Instant createdAt) {
        this.id = id; this.name = name; this.description = description;
        this.price = price; this.imageUrl = imageUrl;
        this.categoryId = categoryId; this.stockQty = stockQty;
        this.active = active; this.createdAt = createdAt;
    }

    // getters/setters
    public Long getId() { return id; }
    public void setId(Long id) { this.id = id; }
    public String getName() { return name; }
    public void setName(String name) { this.name = name; }
    public String getDescription() { return description; }
    public void setDescription(String description) { this.description = description; }
    public double getPriceCents() { return price; }
    public void setPriceCents(double priceCents) { this.price = price; }
    public String getImageUrl() { return imageUrl; }
    public void setImageUrl(String imageUrl) { this.imageUrl = imageUrl; }
    public Long getCategoryId() { return categoryId; }
    public void setCategoryId(Long categoryId) { this.categoryId = categoryId; }
    public Integer getStockQty() { return stockQty; }
    public void setStockQty(Integer stockQty) { this.stockQty = stockQty; }
    public boolean isActive() { return active; }
    public void setActive(boolean active) { this.active = active; }
    public Instant getCreatedAt() { return createdAt; }
    public void setCreatedAt(Instant createdAt) { this.createdAt = createdAt; }
}
