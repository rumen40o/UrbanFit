package com.example.UrbanFit.Service.interfaces;

import com.example.UrbanFit.models.DTO.cart.AddToCartRequest;
import com.example.UrbanFit.models.DTO.cart.CartSummaryDto;
import com.example.UrbanFit.models.DTO.cart.UpdateCartRequest;
import com.example.UrbanFit.models.Users;

public interface CartServiceInterface {
    CartSummaryDto getMyCart(Users user);
    CartSummaryDto add(Users user, AddToCartRequest req);
    CartSummaryDto updateQuantity(Users user, Long productId, UpdateCartRequest req);
    void remove(Users user, Long productId);
    void clear(Users user);
}
