package com.example.UrbanFit.Service;

import com.example.UrbanFit.Service.interfaces.CartServiceInterface;
import com.example.UrbanFit.models.CartItem;
import com.example.UrbanFit.models.DTO.cart.AddToCartRequest;
import com.example.UrbanFit.models.DTO.cart.CartItemDto;
import com.example.UrbanFit.models.DTO.cart.CartSummaryDto;
import com.example.UrbanFit.models.DTO.cart.UpdateCartRequest;
import com.example.UrbanFit.models.Product;
import com.example.UrbanFit.models.ProductVariant;
import com.example.UrbanFit.models.Users;
import com.example.UrbanFit.repository.CartItemRepo;
import com.example.UrbanFit.repository.ProductVariantRepo;
import jakarta.transaction.Transactional;
import org.springframework.stereotype.Service;

import java.util.List;

@Service
@Transactional
public class CartService implements CartServiceInterface {

    private final CartItemRepo cartRepo;
    private final ProductVariantRepo variantRepo;

    public CartService(CartItemRepo cartRepo, ProductVariantRepo variantRepo) {
        this.cartRepo = cartRepo;
        this.variantRepo = variantRepo;
    }

    @Override
    @Transactional(Transactional.TxType.SUPPORTS)
    public CartSummaryDto getMyCart(Users user) {
        List<CartItem> items = cartRepo.findByUser_Id(user.getId());
        return toSummary(items);
    }

    @Override
    public CartSummaryDto add(Users user, AddToCartRequest req) {
        Long variantId = req.getVariantId();
        int addQty = req.getQty();

        ProductVariant variant = variantRepo.findById(variantId)
                .orElseThrow(() -> new IllegalArgumentException("Variant not found"));

        if (!variant.getProduct().isActive()) {
            throw new IllegalArgumentException("Product is not available");
        }

        int stock = variant.getStockQty() == null ? 0 : variant.getStockQty();
        if (stock <= 0) {
            throw new IllegalArgumentException("Out of stock");
        }

        CartItem item = cartRepo.findByUserAndVariant(user, variant).orElse(null);

        if (item == null) {
            if (addQty > stock) throw new IllegalArgumentException("Not enough stock");
            item = new CartItem(user, variant, addQty);
        } else {
            int nextQty = item.getQuantity() + addQty;
            if (nextQty > stock) throw new IllegalArgumentException("Not enough stock");
            item.setQuantity(nextQty);
        }

        cartRepo.save(item);
        return getMyCart(user);
    }

    @Override
    public CartSummaryDto updateQty(Users user, Long variantId, UpdateCartRequest req) {
        int qty = req.getQty();

        ProductVariant variant = variantRepo.findById(variantId)
                .orElseThrow(() -> new IllegalArgumentException("Variant not found"));

        int stock = variant.getStockQty() == null ? 0 : variant.getStockQty();
        if (qty > stock) throw new IllegalArgumentException("Not enough stock");

        CartItem item = cartRepo.findByUserAndVariant(user, variant)
                .orElseThrow(() -> new IllegalArgumentException("Item not in cart"));

        item.setQuantity(qty);
        return getMyCart(user);
    }

    @Override
    public void remove(Users user, Long variantId) {
        ProductVariant variant = variantRepo.findById(variantId)
                .orElseThrow(() -> new IllegalArgumentException("Variant not found"));

        cartRepo.findByUserAndVariant(user, variant)
                .ifPresent(cartRepo::delete);
    }

    @Override
    public void clear(Users user) {
        cartRepo.deleteByUser_Id(user.getId());
    }

    // ---------- helpers ----------

    private CartSummaryDto toSummary(List<CartItem> items) {
        List<CartItemDto> dtos = items.stream()
                .map(ci -> {
                    ProductVariant v = ci.getVariant();
                    Product p = v.getProduct();

                    return new CartItemDto(
                            v.getId(),                 // variantId
                            p.getId(),                 // productId
                            p.getName(),
                            p.getImageUrl(),
                            p.getType().name(),
                            p.getAudience().name(),
                            v.getSize(),
                            ci.getQuantity(),          // qty
                            v.getStockQty(),           // maxQty
                            p.getPrice()               // unitPrice
                    );
                })
                .toList();

        // ако си оставил CartSummaryDto да си смята total сам — може просто new CartSummaryDto(dtos)
        double total = dtos.stream().mapToDouble(d -> d.getLineTotal() != null ? d.getLineTotal() : 0.0).sum();
        return new CartSummaryDto(dtos, total);
    }
}
