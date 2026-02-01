package com.example.UrbanFit.repository;

import com.example.UrbanFit.models.Product;
import com.example.UrbanFit.models.ProductVariant;
import com.example.UrbanFit.models.enums.Audience;
import com.example.UrbanFit.models.enums.ClothingType;
import jakarta.persistence.OrderBy;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.EntityGraph;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.ArrayList;
import java.util.List;
import java.util.Optional;

public interface ProductRepo extends JpaRepository<Product, Long> {

    // list filters - ок
    Page<Product> findByActiveTrue(Pageable pageable);
    Page<Product> findByActiveTrueAndNameContainingIgnoreCase(String q, Pageable pageable);
    Page<Product> findByActiveTrueAndType(ClothingType type, Pageable pageable);
    Page<Product> findByActiveTrueAndAudience(Audience audience, Pageable pageable);
    Page<Product> findByActiveTrueAndTypeAndAudience(ClothingType type, Audience audience, Pageable pageable);
    Page<Product> findByActiveTrueAndTypeAndNameContainingIgnoreCase(ClothingType type, String q, Pageable pageable);
    Page<Product> findByActiveTrueAndAudienceAndNameContainingIgnoreCase(Audience audience, String q, Pageable pageable);
    Page<Product> findByActiveTrueAndTypeAndAudienceAndNameContainingIgnoreCase(
            ClothingType type, Audience audience, String q, Pageable pageable
    );

    // NEW for product details
    @EntityGraph(attributePaths = "variants")
    Optional<Product> findWithVariantsById(Long id);

}

