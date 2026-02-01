package com.example.UrbanFit.repository;


import com.example.UrbanFit.models.ProductVariant;
import org.springframework.data.jpa.repository.JpaRepository;

public interface ProductVariantRepo extends JpaRepository<ProductVariant, Long> {}
