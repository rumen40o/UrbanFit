package com.example.UrbanFit.Service;

import com.example.UrbanFit.Service.interfaces.ProductServiceInterface;
import com.example.UrbanFit.models.DTO.product.*;
import com.example.UrbanFit.models.Order;
import com.example.UrbanFit.models.Product;
import com.example.UrbanFit.models.ProductVariant;
import com.example.UrbanFit.models.enums.Audience;
import com.example.UrbanFit.models.enums.ClothingType;
import com.example.UrbanFit.repository.ProductRepo;
import com.example.UrbanFit.repository.OrderRepo;
import com.example.UrbanFit.repository.ReviewRepo;
import com.example.UrbanFit.repository.CartItemRepo;
import jakarta.transaction.Transactional;
import org.springframework.data.domain.*;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.stream.Collectors;

@Service
@Transactional
public class ProductService implements ProductServiceInterface {

    private final ProductRepo repo;
    private final OrderRepo orderRepo;
    private final ReviewRepo reviewRepo;
    private final CartItemRepo cartItemRepo;

    public ProductService(ProductRepo repo, OrderRepo orderRepo, ReviewRepo reviewRepo, CartItemRepo cartItemRepo) {
        this.repo = repo;
        this.orderRepo = orderRepo;
        this.reviewRepo = reviewRepo;
        this.cartItemRepo = cartItemRepo;
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

        return result.map(this::toListDto);
    }

    @Override
    @Transactional(Transactional.TxType.SUPPORTS)
    public ProductDetailsDto findDetails(Long id) {
        Product p = repo.findWithVariantsById(id)
                .orElseThrow(() -> new IllegalArgumentException("Product not found"));

        return new ProductDetailsDto(
                p.getId(),
                p.getName(),
                p.getDescription(),
                p.getPrice(),
                p.getImageUrl(),
                p.getType(),
                p.getAudience(),
                p.isActive(),
                p.getCreatedAt(),
                p.getVariants().stream()
                        .map(v -> new VariantDto(v.getId(), v.getSize(), v.getStockQty()))
                        .collect(Collectors.toList())
        );
    }

    @Override
    public ProductDto create(CreateProductRequest req) {
        Product p = new Product();
        p.setName(req.getName().trim());
        p.setDescription(req.getDescription());
        p.setPrice(req.getPrice());
        p.setImageUrl(req.getImageUrl());
        p.setActive(req.getActive() == null || req.getActive());
        p.setType(req.getType());
        p.setAudience(req.getAudience());

        if (req.getVariants() == null || req.getVariants().isEmpty()) {
            throw new IllegalArgumentException("At least one size variant is required");
        }

        for (CreateVariantRequest vr : req.getVariants()) {
            if (vr.getSize() == null || vr.getSize().isBlank()) {
                throw new IllegalArgumentException("Variant size is required");
            }
            if (vr.getStockQty() == null || vr.getStockQty() < 0) {
                throw new IllegalArgumentException("Variant stockQty must be >= 0");
            }

            ProductVariant v = new ProductVariant();
            v.setSize(vr.getSize().trim());
            v.setStockQty(vr.getStockQty());

            v.setProduct(p);
            p.getVariants().add(v);
        }

        p = repo.save(p);

        // интерфейсът ти иска ProductDto, затова връщаме list dto (без variants)
        return toListDto(p);
    }

    @Override
    public ProductDto update(Long id, UpdateProductRequest req) {
        Product p = repo.findById(id)
                .orElseThrow(() -> new IllegalArgumentException("Product not found"));

        // --- product fields ---
        if (req.getName() != null) p.setName(req.getName().trim());
        if (req.getDescription() != null) p.setDescription(req.getDescription());
        if (req.getPrice() != null) p.setPrice(req.getPrice());
        if (req.getImageUrl() != null) p.setImageUrl(req.getImageUrl());
        if (req.getActive() != null) p.setActive(req.getActive());
        if (req.getType() != null) p.setType(req.getType());
        if (req.getAudience() != null) p.setAudience(req.getAudience());

        // --- ✅ variants stock update + new variants + removals ---
        if (req.getVariants() != null) {
            // First, remove variants that are no longer in the request
            p.getVariants().removeIf(existing -> 
                !req.getVariants().stream().anyMatch(vr -> vr.getId() != null && vr.getId().equals(existing.getId()))
            );
            
            // Then, update or create variants
            for (UpdateVariantStockRequest vr : req.getVariants()) {
                if (vr.getSize() == null || vr.getSize().isBlank())
                    throw new IllegalArgumentException("Variant size is required");
                if (vr.getStockQty() == null || vr.getStockQty() < 0)
                    throw new IllegalArgumentException("stockQty must be >= 0");

                if (vr.getId() == null) {
                    // ✅ NEW variant - create it
                    ProductVariant newVariant = new ProductVariant();
                    newVariant.setSize(vr.getSize().trim());
                    newVariant.setStockQty(vr.getStockQty());
                    newVariant.setProduct(p);
                    p.getVariants().add(newVariant);
                } else {
                    // ✅ EXISTING variant - update it
                    ProductVariant variant = p.getVariants().stream()
                            .filter(v -> v.getId().equals(vr.getId()))
                            .findFirst()
                            .orElseThrow(() ->
                                    new IllegalArgumentException("Variant not found for product"));
                    
                    variant.setSize(vr.getSize().trim());
                    variant.setStockQty(vr.getStockQty());
                }
            }
        }

        return toListDto(p);
    }

    @Override
    public void delete(Long id) {
        if (!repo.existsById(id)) throw new IllegalArgumentException("Product not found");

        // delete reviews referencing this product
        reviewRepo.deleteByProduct_Id(id);

        // delete cart items that reference variants of this product
        cartItemRepo.deleteByVariant_Product_Id(id);

        // Find and delete any orders that contain this product (order items store productId as snapshot)
        List<Order> ordersWithProduct = orderRepo.findByItemProductId(id);
        if (ordersWithProduct != null && !ordersWithProduct.isEmpty()) {
            orderRepo.deleteAll(ordersWithProduct);
        }

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

        switch (field) {
            case "name":
            case "price":
            case "createdAt":
                break;
            default:
                field = "createdAt";
        }

        return "desc".equals(dir)
                ? Sort.by(Sort.Order.desc(field))
                : Sort.by(Sort.Order.asc(field));
    }

    private ProductDto toListDto(Product p) {
        return new ProductDto(
                p.getId(),
                p.getName(),
                p.getDescription(),
                p.getPrice(),
                p.getImageUrl(),
                p.getType(),
                p.getAudience(),
                p.isActive(),
                p.getCreatedAt(),
                null
        );
    }
}
