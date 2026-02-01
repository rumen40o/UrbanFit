package com.example.UrbanFit.models.DTO.order;

public class OrderItemDto {

    private Long productId;
    private String productName;

    private Long variantId;
    private String size;

    private String type;
    private String audience;

    private Integer quantity;
    private Double unitPrice;
    private Double lineTotal;

    public OrderItemDto() {}

    public OrderItemDto(
            Long productId,
            String productName,
            Long variantId,
            String size,
            String type,
            String audience,
            Integer quantity,
            Double unitPrice
    ) {
        this.productId = productId;
        this.productName = productName;
        this.variantId = variantId;
        this.size = size;
        this.type = type;
        this.audience = audience;
        this.quantity = quantity;
        this.unitPrice = unitPrice;
        this.lineTotal = unitPrice * quantity;
    }

    public Long getProductId() { return productId; }
    public void setProductId(Long productId) { this.productId = productId; }

    public String getProductName() { return productName; }
    public void setProductName(String productName) { this.productName = productName; }

    public Long getVariantId() { return variantId; }
    public void setVariantId(Long variantId) { this.variantId = variantId; }

    public String getSize() { return size; }
    public void setSize(String size) { this.size = size; }

    public String getType() { return type; }
    public void setType(String type) { this.type = type; }

    public String getAudience() { return audience; }
    public void setAudience(String audience) { this.audience = audience; }

    public Integer getQuantity() { return quantity; }
    public void setQuantity(Integer quantity) { this.quantity = quantity; }

    public Double getUnitPrice() { return unitPrice; }
    public void setUnitPrice(Double unitPrice) { this.unitPrice = unitPrice; }

    public Double getLineTotal() { return lineTotal; }
    public void setLineTotal(Double lineTotal) { this.lineTotal = lineTotal; }
}
