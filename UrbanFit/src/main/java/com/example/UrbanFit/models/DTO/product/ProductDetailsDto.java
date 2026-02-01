package com.example.UrbanFit.models.DTO.product;

import com.example.UrbanFit.models.enums.Audience;
import com.example.UrbanFit.models.enums.ClothingType;

import java.time.Instant;
import java.util.List;

public class ProductDetailsDto {

        private Long id;
        private String name;
        private String description;
        private double price;
        private String imageUrl;
        private ClothingType type;
        private Audience audience;
        private boolean active;
        private Instant createdAt;
        private List<VariantDto> variants;

        public ProductDetailsDto(
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

        // getters (важно!)
        public List<VariantDto> getVariants() { return variants; }
        public Long getId() { return id; }
        public String getName() { return name; }
        public String getDescription() { return description; }
        public double getPrice() { return price; }
        public String getImageUrl() { return imageUrl; }
        public ClothingType getType() { return type; }
        public Audience getAudience() { return audience; }
        public boolean isActive() { return active; }
        public Instant getCreatedAt() { return createdAt; }
}

