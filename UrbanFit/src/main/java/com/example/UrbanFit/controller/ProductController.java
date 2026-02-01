package com.example.UrbanFit.controller;

import com.example.UrbanFit.Service.ProductService;
import com.example.UrbanFit.models.DTO.product.CreateProductRequest;
import com.example.UrbanFit.models.DTO.product.ProductDetailsDto;
import com.example.UrbanFit.models.DTO.product.ProductDto;
import com.example.UrbanFit.models.DTO.product.UpdateProductRequest;
import com.example.UrbanFit.models.enums.Audience;
import com.example.UrbanFit.models.enums.ClothingType;
import jakarta.validation.Valid;
import org.springframework.data.domain.Page;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.*;

import java.net.URI;

@RestController
@RequestMapping("/products")
@CrossOrigin(origins = "http://localhost:3000", allowCredentials = "true")
public class ProductController {

    private final ProductService service;

    public ProductController(ProductService service) {
        this.service = service;
    }

    // ---------- PUBLIC LIST ----------
    @GetMapping
    public Page<ProductDto> list(
            @RequestParam(required = false) Integer page,
            @RequestParam(required = false) Integer size,
            @RequestParam(required = false) String sort,          // напр. "price,desc"
            @RequestParam(required = false) ClothingType type,    // напр. TSHIRT
            @RequestParam(required = false) Audience audience,    // напр. MEN
            @RequestParam(required = false) String q
    ) {
        return service.findPublic(page, size, sort, type, audience, q);
    }

    // ---------- PUBLIC DETAILS (WITH VARIANTS) ----------
    @GetMapping("/{id}")
    public ProductDetailsDto getOne(@PathVariable Long id) {
        return service.findDetails(id);
    }

    // ---------- ADMIN ----------
    @PostMapping("/add")
    @PreAuthorize("hasRole('ADMIN')")
    public ResponseEntity<ProductDto> create(@Valid @RequestBody CreateProductRequest req) {
        ProductDto created = service.create(req);
        return ResponseEntity.created(URI.create("/products/" + created.getId())).body(created);
    }

    @PutMapping("/{id}")
    @PreAuthorize("hasRole('ADMIN')")
    public ProductDto update(@PathVariable Long id, @Valid @RequestBody UpdateProductRequest req) {
        return service.update(id, req);
    }

    @DeleteMapping("/{id}")
    @PreAuthorize("hasRole('ADMIN')")
    public ResponseEntity<Void> delete(@PathVariable Long id) {
        service.delete(id);
        return ResponseEntity.noContent().build();
    }
}
