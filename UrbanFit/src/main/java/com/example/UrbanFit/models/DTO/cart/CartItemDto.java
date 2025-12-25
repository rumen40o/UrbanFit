package com.example.UrbanFit.models.DTO.cart;

public class CartItemDto {
    private Long productId;
    private String productName;
    private Integer quantity;
    private Double price; // цена на единица (твоята Product.price)
    private Double lineTotal;

    public CartItemDto() {}

    public CartItemDto(Long productId, String productName, Integer quantity, Double price) {
        this.productId = productId;
        this.productName = productName;
        this.quantity = quantity;
        this.price = price;
        this.lineTotal = price * quantity;
    }

    // getters/setters
    public Long getProductId() { return productId; } public void setProductId(Long productId) { this.productId = productId; }
    public String getProductName() { return productName; } public void setProductName(String productName) { this.productName = productName; }
    public Integer getQuantity() { return quantity; } public void setQuantity(Integer quantity) { this.quantity = quantity; }
    public Double getPrice() { return price; } public void setPrice(Double price) { this.price = price; }
    public Double getLineTotal() { return lineTotal; } public void setLineTotal(Double lineTotal) { this.lineTotal = lineTotal; }
}
