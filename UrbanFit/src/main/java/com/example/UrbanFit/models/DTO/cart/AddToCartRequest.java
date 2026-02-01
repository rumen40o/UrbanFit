package com.example.UrbanFit.models.DTO.cart;

import com.fasterxml.jackson.annotation.JsonAlias;
import jakarta.validation.constraints.Min;
import jakarta.validation.constraints.NotNull;

public class AddToCartRequest {

    @NotNull
    private Long variantId;

    @NotNull
    @Min(1)
    @JsonAlias({"quantity"}) // ще приема и "quantity" ако някъде още го пращаш
    private Integer qty;

    public Long getVariantId() { return variantId; }
    public void setVariantId(Long variantId) { this.variantId = variantId; }

    public Integer getQty() { return qty; }
    public void setQty(Integer qty) { this.qty = qty; }
}
