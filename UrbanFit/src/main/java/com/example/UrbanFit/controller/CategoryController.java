package com.example.UrbanFit.controller;





import com.example.UrbanFit.Service.interfaces.CategoryServiceInterface;
import com.example.UrbanFit.models.DTO.category.CategoryDto;
import com.example.UrbanFit.models.DTO.category.CreateCategoryRequest;
import com.example.UrbanFit.models.DTO.category.UpdateCategoryRequest;
import jakarta.validation.Valid;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.*;

import java.net.URI;
import java.util.List;

@RestController
@RequestMapping("/categories")
@CrossOrigin(origins = "http://localhost:3000", allowCredentials = "true")
public class CategoryController {

    private final CategoryServiceInterface service;

    public CategoryController(CategoryServiceInterface service) {
        this.service = service;
    }

    // Публични:
    @GetMapping
    public List<CategoryDto> getAll() {
        return service.findAll();
    }

    @GetMapping("/{id}")
    public CategoryDto getOne(@PathVariable Long id) {
        return service.findById(id);
    }

    @PostMapping("/add")
    @PreAuthorize("hasRole('ADMIN')")
    public ResponseEntity<CategoryDto> create(@Valid @RequestBody CreateCategoryRequest req) {
        CategoryDto created = service.create(req);
        return ResponseEntity.created(URI.create("/categories/" + created.getId())).body(created);
    }

    @PutMapping("/{id}")
    @PreAuthorize("hasRole('ADMIN')")
    public CategoryDto update(@PathVariable Long id, @Valid @RequestBody UpdateCategoryRequest req) {
        return service.update(id, req);
    }

    @DeleteMapping("/{id}")
    @PreAuthorize("hasRole('ADMIN')")
    public ResponseEntity<Void> delete(@PathVariable Long id) {
        service.delete(id);
        return ResponseEntity.noContent().build();
    }
}

