package com.example.UrbanFit.repository;

import com.example.UrbanFit.models.Product;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;


public interface ProductRepo extends JpaRepository<Product, Long> {
    Page<Product> findByActiveTrue(Pageable pageable);
    Page<Product> findByActiveTrueAndCategory_Id(Long categoryId, Pageable pageable);
    Page<Product> findByActiveTrueAndNameContainingIgnoreCase(String q, Pageable pageable);
    Page<Product> findByActiveTrueAndCategory_IdAndNameContainingIgnoreCase(Long categoryId, String q, Pageable pageable);
}
