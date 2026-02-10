package com.example.UrbanFit.models.DTO.product;

import com.example.UrbanFit.models.enums.Audience;
import com.example.UrbanFit.models.enums.ClothingType;
import jakarta.validation.constraints.Min;

import java.util.List;

public class UpdateProductRequest {

    private String name;
    private String description;

    @Min(0)
    private Double price;

    private String imageUrl;

    // nullable -> partial update
    private ClothingType type;
    private Audience audience;

    private Boolean active;

    private List<UpdateVariantStockRequest> variants;


    // getters/setters
    public List<UpdateVariantStockRequest> getVariants() {
        return variants;
    }
    public void setVariants(List<UpdateVariantStockRequest> variants) {
        this.variants = variants;
    }

    public String getName() { return name; }
    public void setName(String name) { this.name = name; }

    public String getDescription() { return description; }
    public void setDescription(String description) { this.description = description; }

    public Double getPrice() { return price; }
    public void setPrice(Double price) { this.price = price; }

    public String getImageUrl() { return imageUrl; }
    public void setImageUrl(String imageUrl) { this.imageUrl = imageUrl; }

    public ClothingType getType() { return type; }
    public void setType(ClothingType type) { this.type = type; }

    public Audience getAudience() { return audience; }
    public void setAudience(Audience audience) { this.audience = audience; }

    public Boolean getActive() { return active; }
    public void setActive(Boolean active) { this.active = active; }
}
