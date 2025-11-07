package com.example.UrbanFit.Service;

import com.example.UrbanFit.Service.interfaces.ProductServiceInterface;
import com.example.UrbanFit.models.Category;
import com.example.UrbanFit.models.DTO.CreateProductRequest;
import com.example.UrbanFit.models.DTO.ProductDto;
import com.example.UrbanFit.models.DTO.UpdateProductRequest;
import com.example.UrbanFit.models.Product;
import com.example.UrbanFit.repository.CategoryRepo;
import com.example.UrbanFit.repository.ProductRepo;
import jakarta.transaction.Transactional;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.data.domain.Sort;
import org.springframework.stereotype.Service;

@Service
@Transactional
public class ProductService implements ProductServiceInterface {

    private final ProductRepo repo;
    private final CategoryRepo categoryRepo;

    public ProductService(ProductRepo repo, CategoryRepo categoryRepo) {
        this.repo = repo;
        this.categoryRepo = categoryRepo;
    }

    @Override
    @Transactional(Transactional.TxType.SUPPORTS)
    public Page<ProductDto> findPublic(Integer page, Integer size, String sort, Long categoryId, String q) {
        int p = (page == null || page < 0) ? 0 : page;
        int s = (size == null || size <= 0) ? 12 : Math.min(size, 100);

        Sort sortObj = parseSort(sort);               // <-- фиксирано парсване
        Pageable pageable = PageRequest.of(p, s, sortObj);

        Page<Product> result;
        boolean hasQ = q != null && !q.isBlank();

        if (categoryId != null && hasQ) {
            result = repo.findByActiveTrueAndCategory_IdAndNameContainingIgnoreCase(categoryId, q.trim(), pageable);
        } else if (categoryId != null) {
            result = repo.findByActiveTrueAndCategory_Id(categoryId, pageable);
        } else if (hasQ) {
            result = repo.findByActiveTrueAndNameContainingIgnoreCase(q.trim(), pageable);
        } else {
            result = repo.findByActiveTrue(pageable);
        }

        return result.map(this::toDto);
    }

    @Override
    @Transactional(Transactional.TxType.SUPPORTS)
    public ProductDto findById(Long id) {
        Product p = repo.findById(id).orElseThrow(() -> new IllegalArgumentException("Product not found"));
        return toDto(p);
    }

    @Override
    public ProductDto create(CreateProductRequest req) {
        Product p = new Product();
        p.setName(req.getName().trim());
        p.setDescription(req.getDescription());
        p.setPrice(req.getPrice());                       // Double/double – ок според твоите DTO/Entity
        p.setImageUrl(req.getImageUrl());
        p.setStockQty(req.getStockQty());
        p.setActive(req.getActive() == null || req.getActive());  // по подразбиране true

        if (req.getCategoryId() != null) {
            Category c = categoryRepo.findById(req.getCategoryId())
                    .orElseThrow(() -> new IllegalArgumentException("Category not found"));
            p.setCategory(c);
        }

        p = repo.save(p);
        return toDto(p);
    }

    @Override
    public ProductDto update(Long id, UpdateProductRequest req) {
        Product p = repo.findById(id).orElseThrow(() -> new IllegalArgumentException("Product not found"));

        if (req.getName() != null) p.setName(req.getName().trim());
        if (req.getDescription() != null) p.setDescription(req.getDescription());
        if (req.getPrice() != null) p.setPrice(req.getPrice());
        if (req.getImageUrl() != null) p.setImageUrl(req.getImageUrl());
        if (req.getStockQty() != null) p.setStockQty(req.getStockQty());
        if (req.getActive() != null) p.setActive(req.getActive());

        if (req.getCategoryId() != null) {
            if (req.getCategoryId() == 0) {
                p.setCategory(null); // махане на категория
            } else {
                Category c = categoryRepo.findById(req.getCategoryId())
                        .orElseThrow(() -> new IllegalArgumentException("Category not found"));
                p.setCategory(c);
            }
        }

        return toDto(p);
    }

    @Override
    public void delete(Long id) {
        if (!repo.existsById(id)) throw new IllegalArgumentException("Product not found");
        repo.deleteById(id);
    }

    // ---------- helpers ----------

    private Sort parseSort(String sortParam) {
        // default: createdAt,desc
        if (sortParam == null || sortParam.isBlank()) {
            return Sort.by(Sort.Order.desc("createdAt"));
        }
        String[] parts = sortParam.split(",");
        String field = parts[0].trim();
        String dir = (parts.length > 1) ? parts[1].trim().toLowerCase() : "asc";

        // whitelist на позволени полета за сортиране
        switch (field) {
            case "name":
            case "price":
            case "createdAt":
            case "stockQty":
                break;
            default:
                field = "createdAt"; // fallback ако подадат невалидно поле
        }

        return "desc".equals(dir)
                ? Sort.by(Sort.Order.desc(field))
                : Sort.by(Sort.Order.asc(field));
    }

    private ProductDto toDto(Product p) {
        Long categoryId = (p.getCategory() != null) ? p.getCategory().getId() : null;
        return new ProductDto(
                p.getId(),
                p.getName(),
                p.getDescription(),
                p.getPrice(),
                p.getImageUrl(),
                categoryId,
                p.getStockQty(),
                p.isActive(),
                p.getCreatedAt()
        );
    }
}
