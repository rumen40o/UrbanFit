package com.example.UrbanFit.Service.interfaces;

import com.example.UrbanFit.models.DTO.product.CreateProductRequest;
import com.example.UrbanFit.models.DTO.product.ProductDto;
import com.example.UrbanFit.models.DTO.product.UpdateProductRequest;
import org.springframework.data.domain.Page;

public interface ProductServiceInterface {
    Page<ProductDto> findPublic(Integer page, Integer size, String sort, Long categoryId, String q);
    ProductDto findById(Long id);
    ProductDto create(CreateProductRequest req);
    ProductDto update(Long id, UpdateProductRequest req);
    void delete(Long id);
}
