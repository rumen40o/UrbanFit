package com.example.UrbanFit.Service;

import com.example.UrbanFit.Service.interfaces.ProductServiceInterface;
import com.example.UrbanFit.models.DTO.product.CreateProductRequest;
import com.example.UrbanFit.models.DTO.product.ProductDto;
import com.example.UrbanFit.models.DTO.product.UpdateProductRequest;
import com.example.UrbanFit.models.Product;
import com.example.UrbanFit.models.enums.Audience;
import com.example.UrbanFit.models.enums.ClothingType;
import com.example.UrbanFit.repository.ProductRepo;
import jakarta.transaction.Transactional;
import org.springframework.data.domain.*;
import org.springframework.stereotype.Service;

@Service
@Transactional
public class ProductService implements ProductServiceInterface {

    private final ProductRepo repo;

    public ProductService(ProductRepo repo) {
        this.repo = repo;
    }

    @Override
    @Transactional(Transactional.TxType.SUPPORTS)
    public Page<ProductDto> findPublic(
            Integer page,
            Integer size,
            String sort,
            ClothingType type,
            Audience audience,
            String q
    ) {
        int p = (page == null || page < 0) ? 0 : page;
        int s = (size == null || size <= 0) ? 12 : Math.min(size, 100);

        Sort sortObj = parseSort(sort);
        Pageable pageable = PageRequest.of(p, s, sortObj);

        boolean hasQ = q != null && !q.isBlank();
        String query = hasQ ? q.trim() : null;

        Page<Product> result;

        // 8 комбинации: type? audience? q?
        if (type != null && audience != null && hasQ) {
            result = repo.findByActiveTrueAndTypeAndAudienceAndNameContainingIgnoreCase(type, audience, query, pageable);
        } else if (type != null && audience != null) {
            result = repo.findByActiveTrueAndTypeAndAudience(type, audience, pageable);
        } else if (type != null && hasQ) {
            result = repo.findByActiveTrueAndTypeAndNameContainingIgnoreCase(type, query, pageable);
        } else if (audience != null && hasQ) {
            result = repo.findByActiveTrueAndAudienceAndNameContainingIgnoreCase(audience, query, pageable);
        } else if (type != null) {
            result = repo.findByActiveTrueAndType(type, pageable);
        } else if (audience != null) {
            result = repo.findByActiveTrueAndAudience(audience, pageable);
        } else if (hasQ) {
            result = repo.findByActiveTrueAndNameContainingIgnoreCase(query, pageable);
        } else {
            result = repo.findByActiveTrue(pageable);
        }

        return result.map(this::toDto);
    }

    @Override
    @Transactional(Transactional.TxType.SUPPORTS)
    public ProductDto findById(Long id) {
        Product p = repo.findById(id)
                .orElseThrow(() -> new IllegalArgumentException("Product not found"));
        return toDto(p);
    }

    @Override
    public ProductDto create(CreateProductRequest req) {
        Product p = new Product();
        p.setName(req.getName().trim());
        p.setDescription(req.getDescription());
        p.setPrice(req.getPrice());
        p.setImageUrl(req.getImageUrl());
        p.setStockQty(req.getStockQty());
        p.setActive(req.getActive() == null || req.getActive());

        // NEW
        p.setType(req.getType());           // NOT NULL
        p.setAudience(req.getAudience());   // NOT NULL

        p = repo.save(p);
        return toDto(p);
    }

    @Override
    public ProductDto update(Long id, UpdateProductRequest req) {
        Product p = repo.findById(id)
                .orElseThrow(() -> new IllegalArgumentException("Product not found"));

        if (req.getName() != null) p.setName(req.getName().trim());
        if (req.getDescription() != null) p.setDescription(req.getDescription());
        if (req.getPrice() != null) p.setPrice(req.getPrice());
        if (req.getImageUrl() != null) p.setImageUrl(req.getImageUrl());
        if (req.getStockQty() != null) p.setStockQty(req.getStockQty());
        if (req.getActive() != null) p.setActive(req.getActive());

        // NEW
        if (req.getType() != null) p.setType(req.getType());
        if (req.getAudience() != null) p.setAudience(req.getAudience());

        return toDto(p);
    }

    @Override
    public void delete(Long id) {
        if (!repo.existsById(id)) throw new IllegalArgumentException("Product not found");
        repo.deleteById(id);
    }

    // ---------- helpers ----------

    private Sort parseSort(String sortParam) {
        if (sortParam == null || sortParam.isBlank()) {
            return Sort.by(Sort.Order.desc("createdAt"));
        }
        String[] parts = sortParam.split(",");
        String field = parts[0].trim();
        String dir = (parts.length > 1) ? parts[1].trim().toLowerCase() : "asc";

        // whitelist
        switch (field) {
            case "name":
            case "price":
            case "createdAt":
            case "stockQty":
                break;
            default:
                field = "createdAt";
        }

        return "desc".equals(dir)
                ? Sort.by(Sort.Order.desc(field))
                : Sort.by(Sort.Order.asc(field));
    }

    private ProductDto toDto(Product p) {
        return new ProductDto(
                p.getId(),
                p.getName(),
                p.getDescription(),
                p.getPrice(),
                p.getImageUrl(),
                p.getType(),       // NEW
                p.getAudience(),   // NEW
                p.getStockQty(),
                p.isActive(),
                p.getCreatedAt()
        );
    }
}
