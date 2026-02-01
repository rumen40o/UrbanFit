package com.example.UrbanFit.models.DTO.product;

import com.example.UrbanFit.models.enums.Audience;
import com.example.UrbanFit.models.enums.ClothingType;

import java.time.Instant;
import java.util.List;

public class ProductDto {
    private Long id;
    private String name;
    private String description;
    private double price;
    private String imageUrl;

    private ClothingType type;
    private Audience audience;

    private boolean active;
    private Instant createdAt;

    // NEW
    private List<VariantDto> variants;

    public ProductDto() {}

    public ProductDto(
            Long id,
            String name,
            String description,
            double price,
            String imageUrl,
            ClothingType type,
            Audience audience,
            boolean active,
            Instant createdAt,
            List<VariantDto> variants
    ) {
        this.id = id;
        this.name = name;
        this.description = description;
        this.price = price;
        this.imageUrl = imageUrl;
        this.type = type;
        this.audience = audience;
        this.active = active;
        this.createdAt = createdAt;
        this.variants = variants;
    }

    public Long getId() { return id; }
    public void setId(Long id) { this.id = id; }

    public String getName() { return name; }
    public void setName(String name) { this.name = name; }

    public String getDescription() { return description; }
    public void setDescription(String description) { this.description = description; }

    public double getPrice() { return price; }
    public void setPrice(double price) { this.price = price; }

    public String getImageUrl() { return imageUrl; }
    public void setImageUrl(String imageUrl) { this.imageUrl = imageUrl; }

    public ClothingType getType() { return type; }
    public void setType(ClothingType type) { this.type = type; }

    public Audience getAudience() { return audience; }
    public void setAudience(Audience audience) { this.audience = audience; }

    public boolean isActive() { return active; }
    public void setActive(boolean active) { this.active = active; }

    public Instant getCreatedAt() { return createdAt; }
    public void setCreatedAt(Instant createdAt) { this.createdAt = createdAt; }

    public List<VariantDto> getVariants() { return variants; }
    public void setVariants(List<VariantDto> variants) { this.variants = variants; }
}
