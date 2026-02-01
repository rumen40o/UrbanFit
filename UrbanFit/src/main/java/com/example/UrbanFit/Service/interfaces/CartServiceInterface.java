package com.example.UrbanFit.Service.interfaces;

import com.example.UrbanFit.models.DTO.cart.AddToCartRequest;
import com.example.UrbanFit.models.DTO.cart.CartSummaryDto;
import com.example.UrbanFit.models.DTO.cart.UpdateCartRequest;
import com.example.UrbanFit.models.Users;

public interface CartServiceInterface {

    CartSummaryDto getMyCart(Users user);

    CartSummaryDto add(Users user, AddToCartRequest req);

    // productId -> variantId
    CartSummaryDto updateQty(Users user, Long variantId, UpdateCartRequest req);

    // productId -> variantId
    void remove(Users user, Long variantId);

    void clear(Users user);
}
