package com.example.UrbanFit.Service.interfaces;

import com.example.UrbanFit.models.DTO.category.CategoryDto;
import com.example.UrbanFit.models.DTO.category.CreateCategoryRequest;
import com.example.UrbanFit.models.DTO.category.UpdateCategoryRequest;

import java.util.List;

public interface CategoryServiceInterface {
    List<CategoryDto> findAll();
    CategoryDto findById(Long id);
    CategoryDto create(CreateCategoryRequest req);
    CategoryDto update(Long id, UpdateCategoryRequest req);
    void delete(Long id);
}
