package com.example.UrbanFit.models.DTO.cart;

import java.util.List;

public class CartSummaryDto {
    private List<CartItemDto> items;
    private Double total;

    public CartSummaryDto() {}
    public CartSummaryDto(List<CartItemDto> items, Double total) {
        this.items = items; this.total = total;
    }
    public List<CartItemDto> getItems() { return items; } public void setItems(List<CartItemDto> items) { this.items = items; }
    public Double getTotal() { return total; } public void setTotal(Double total) { this.total = total; }
}
