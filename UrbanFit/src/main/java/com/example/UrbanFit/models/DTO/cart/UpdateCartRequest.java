package com.example.UrbanFit.models.DTO.cart;

import com.fasterxml.jackson.annotation.JsonAlias;
import jakarta.validation.constraints.Min;
import jakarta.validation.constraints.NotNull;

public class UpdateCartRequest {

    @NotNull
    @Min(1)
    @JsonAlias({"quantity"}) // приема и quantity ако някъде още го ползваш
    private Integer qty;

    public Integer getQty() {
        return qty;
    }

    public void setQty(Integer qty) {
        this.qty = qty;
    }
}
