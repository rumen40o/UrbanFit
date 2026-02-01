package com.example.UrbanFit.controller;

import com.example.UrbanFit.Service.interfaces.CartServiceInterface;
import com.example.UrbanFit.models.DTO.cart.AddToCartRequest;
import com.example.UrbanFit.models.DTO.cart.CartSummaryDto;
import com.example.UrbanFit.models.DTO.cart.UpdateCartRequest;
import com.example.UrbanFit.models.Users;
import jakarta.validation.Valid;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("/cart")
@CrossOrigin(origins = "http://localhost:3000", allowCredentials = "true")
public class CartController {

    private final CartServiceInterface service;

    public CartController(CartServiceInterface service) {
        this.service = service;
    }

    @GetMapping
    @PreAuthorize("isAuthenticated()")
    public CartSummaryDto getMyCart(@AuthenticationPrincipal Users user) {
        return service.getMyCart(user);
    }

    @PostMapping("/add")
    @PreAuthorize("isAuthenticated()")
    public CartSummaryDto add(@AuthenticationPrincipal Users user,
                              @Valid @RequestBody AddToCartRequest req) {
        return service.add(user, req);
    }

    @PatchMapping("/item/{productId}")
    @PreAuthorize("isAuthenticated()")
    public CartSummaryDto updateQuantity(@AuthenticationPrincipal Users user,
                                         @PathVariable Long productId,
                                         @Valid @RequestBody UpdateCartRequest req) {
        return service.updateQty(user, productId, req);
    }

    @DeleteMapping("/item/{productId}")
    @PreAuthorize("isAuthenticated()")
    public ResponseEntity<Void> remove(@AuthenticationPrincipal Users user,
                                       @PathVariable Long productId) {
        service.remove(user, productId);
        return ResponseEntity.noContent().build();
    }

    @DeleteMapping("/clear")
    @PreAuthorize("isAuthenticated()")
    public ResponseEntity<Void> clear(@AuthenticationPrincipal Users user) {
        service.clear(user);
        return ResponseEntity.noContent().build();
    }
}
