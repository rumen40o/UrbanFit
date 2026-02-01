package com.example.UrbanFit.models.DTO.product;

import com.example.UrbanFit.models.enums.Audience;
import com.example.UrbanFit.models.enums.ClothingType;
import jakarta.validation.constraints.DecimalMin;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;

import java.util.List;

public class CreateProductRequest {

    @NotBlank
    private String name;

    private String description;

    @NotNull
    @DecimalMin("0.0")
    private double price;

    private String imageUrl;

    @NotNull
    private ClothingType type;

    @NotNull
    private Audience audience;

    // NEW
    @NotNull
    private List<CreateVariantRequest> variants;

    private Boolean active;

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

    public List<CreateVariantRequest> getVariants() { return variants; }
    public void setVariants(List<CreateVariantRequest> variants) { this.variants = variants; }

    public Boolean getActive() { return active; }
    public void setActive(Boolean active) { this.active = active; }
}
