package com.example.UrbanFit.Service.interfaces;

import com.example.UrbanFit.models.DTO.CreateProductRequest;
import com.example.UrbanFit.models.DTO.ProductDto;
import com.example.UrbanFit.models.DTO.UpdateProductRequest;
import org.springframework.data.domain.Page;

public interface ProductServiceInterface {
    Page<ProductDto> findPublic(Integer page, Integer size, String sort, Long categoryId, String q);
    ProductDto findById(Long id);
    ProductDto create(CreateProductRequest req);
    ProductDto update(Long id, UpdateProductRequest req);
    void delete(Long id);
}
