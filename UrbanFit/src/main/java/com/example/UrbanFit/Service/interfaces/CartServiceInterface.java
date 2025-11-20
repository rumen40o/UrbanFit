package com.example.UrbanFit.Service.interfaces;

import com.example.UrbanFit.models.DTO.AddToCartRequest;
import com.example.UrbanFit.models.DTO.CartSummaryDto;
import com.example.UrbanFit.models.DTO.UpdateCartRequest;
import com.example.UrbanFit.models.Users;

public interface CartServiceInterface {
    CartSummaryDto getMyCart(Users user);
    CartSummaryDto add(Users user, AddToCartRequest req);
    CartSummaryDto updateQuantity(Users user, Long productId, UpdateCartRequest req);
    void remove(Users user, Long productId);
    void clear(Users user);
}
