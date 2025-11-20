package com.example.UrbanFit.Service;

import com.example.UrbanFit.Service.interfaces.CartServiceInterface;
import com.example.UrbanFit.models.CartItem;
import com.example.UrbanFit.models.DTO.AddToCartRequest;
import com.example.UrbanFit.models.DTO.CartItemDto;
import com.example.UrbanFit.models.DTO.CartSummaryDto;
import com.example.UrbanFit.models.DTO.UpdateCartRequest;
import com.example.UrbanFit.models.Product;
import com.example.UrbanFit.models.Users;
import com.example.UrbanFit.repository.CartItemRepo;
import com.example.UrbanFit.repository.ProductRepo;
import jakarta.transaction.Transactional;
import org.springframework.stereotype.Service;

import java.util.List;

@Service
@Transactional
public class CartService implements CartServiceInterface {

    private final CartItemRepo cartRepo;
    private final ProductRepo productRepo;

    public CartService(CartItemRepo cartRepo, ProductRepo productRepo) {
        this.cartRepo = cartRepo;
        this.productRepo = productRepo;
    }

    @Override
    @Transactional()
    public CartSummaryDto getMyCart(Users user) {
        var items = cartRepo.findByUser_Id(user.getId());
        return toSummary(items);
    }

    @Override
    public CartSummaryDto add(Users user, AddToCartRequest req) {
        Product product = productRepo.findById(req.getProductId())
                .orElseThrow(() -> new IllegalArgumentException("Product not found"));

        CartItem item = cartRepo.findByUserAndProduct(user, product)
                .orElse(null);

        if (item == null) {
            item = new CartItem(user, product, req.getQuantity());
        } else {
            item.setQuantity(item.getQuantity() + req.getQuantity());
        }

        cartRepo.save(item);
        return getMyCart(user);
    }

    @Override
    public CartSummaryDto updateQuantity(Users user, Long productId, UpdateCartRequest req) {
        Product product = productRepo.findById(productId)
                .orElseThrow(() -> new IllegalArgumentException("Product not found"));

        CartItem item = cartRepo.findByUserAndProduct(user, product)
                .orElseThrow(() -> new IllegalArgumentException("Item not in cart"));

        item.setQuantity(req.getQuantity());
        return getMyCart(user);
    }

    @Override
    public void remove(Users user, Long productId) {
        Product product = productRepo.findById(productId)
                .orElseThrow(() -> new IllegalArgumentException("Product not found"));
        cartRepo.findByUserAndProduct(user, product)
                .ifPresent(cartRepo::delete);
    }

    @Override
    public void clear(Users user) {
        cartRepo.deleteByUser_Id(user.getId());
    }

    // helpers
    private CartSummaryDto toSummary(List<CartItem> items) {
        List<CartItemDto> dtos = items.stream()
                .map(ci -> new CartItemDto(
                        ci.getProduct().getId(),
                        ci.getProduct().getName(),
                        ci.getQuantity(),
                        ci.getProduct().getPrice()
                )).toList();

        double total = dtos.stream().mapToDouble(CartItemDto::getLineTotal).sum();
        return new CartSummaryDto(dtos, total);
    }
}
