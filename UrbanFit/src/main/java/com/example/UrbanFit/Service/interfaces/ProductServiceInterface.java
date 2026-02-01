package com.example.UrbanFit.Service.interfaces;

import com.example.UrbanFit.models.DTO.product.CreateProductRequest;
import com.example.UrbanFit.models.DTO.product.ProductDetailsDto;
import com.example.UrbanFit.models.DTO.product.ProductDto;
import com.example.UrbanFit.models.DTO.product.UpdateProductRequest;
import com.example.UrbanFit.models.enums.Audience;
import com.example.UrbanFit.models.enums.ClothingType;
import org.springframework.data.domain.Page;

public interface ProductServiceInterface {

    Page<ProductDto> findPublic(
            Integer page,
            Integer size,
            String sort,
            ClothingType type,
            Audience audience,
            String q
    );

    // NEW: details with variants
    ProductDetailsDto findDetails(Long id);

    ProductDto create(CreateProductRequest req);

    ProductDto update(Long id, UpdateProductRequest req);

    void delete(Long id);
}


