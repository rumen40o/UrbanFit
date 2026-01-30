package com.example.UrbanFit.models.DTO.product;

import com.example.UrbanFit.models.enums.Audience;
import com.example.UrbanFit.models.enums.ClothingType;
import jakarta.validation.constraints.Min;
import jakarta.validation.constraints.NotNull;

public class UpdateProductRequest {
    private String name;
    private String description;
    @Min(0) private Double price;
    private String imageUrl;
    @NotNull
    private ClothingType type;
    @NotNull
    private Audience audience;
    // може да се смени/нулира
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
    public Audience getAudience() {
        return audience;
    }
    public void setAudience(Audience audience) {
        this.audience = audience;
    }
    public ClothingType getType() {
        return type;
    }
    public void setType(ClothingType type) {
        this.type = type;
    }
    public void setPrice(Double price) {
        this.price = price;
    }
    public Integer getStockQty() { return stockQty; }
    public void setStockQty(Integer stockQty) { this.stockQty = stockQty; }
    public Boolean getActive() { return active; }
    public void setActive(Boolean active) { this.active = active; }
}
