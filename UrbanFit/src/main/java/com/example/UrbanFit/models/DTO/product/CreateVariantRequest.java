package com.example.UrbanFit.models.DTO.product;


import jakarta.validation.constraints.Min;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;

public class CreateVariantRequest {

    @NotBlank
    private String size;        // "S", "M", "L" или "41", "42"

    @NotNull
    @Min(0)
    private Integer stockQty;

    public String getSize() {
        return size;
    }

    public void setSize(String size) {
        this.size = size;
    }

    public Integer getStockQty() {
        return stockQty;
    }

    public void setStockQty(Integer stockQty) {
        this.stockQty = stockQty;
    }

}