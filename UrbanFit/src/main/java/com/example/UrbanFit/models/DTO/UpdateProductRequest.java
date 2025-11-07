package com.example.UrbanFit.models.DTO;

import jakarta.validation.constraints.Min;

public class UpdateProductRequest {
    private String name;
    private String description;
    @Min(0) private Double price;
    private String imageUrl;
    private Long categoryId; // може да се смени/нулира
    @Min(0) private Integer stockQty;
    private Boolean active;

    // getters/setters
    public String getName() { return name; }
    public void setName(String name) { this.name = name; }
    public String getDescription() { return description; }
    public void setDescription(String description) { this.description = description; }
    public Double getPrice() { return price; }
    public void setPrice(double price) { this.price = price; }
    public String getImageUrl() { return imageUrl; }
    public void setImageUrl(String imageUrl) { this.imageUrl = imageUrl; }
    public Long getCategoryId() { return categoryId; }
    public void setCategoryId(Long categoryId) { this.categoryId = categoryId; }
    public Integer getStockQty() { return stockQty; }
    public void setStockQty(Integer stockQty) { this.stockQty = stockQty; }
    public Boolean getActive() { return active; }
    public void setActive(Boolean active) { this.active = active; }
}
