package com.example.UrbanFit.models.DTO.cart;

public class CartItemDto {

    private Long variantId;
    private Long productId;

    private String productName;
    private String imageUrl;

    private String type;      // TSHIRT, HOODIE...
    private String audience;  // MEN, WOMEN...

    private String size;      // S, M, L, 42...

    private Integer qty;      // количество в количката
    private Integer maxQty;   // наличност (variant.stockQty)

    private Double unitPrice; // цена за 1 брой
    private Double lineTotal; // unitPrice * qty

    public CartItemDto() {}

    public CartItemDto(
            Long variantId,
            Long productId,
            String productName,
            String imageUrl,
            String type,
            String audience,
            String size,
            Integer qty,
            Integer maxQty,
            Double unitPrice
    ) {
        this.variantId = variantId;
        this.productId = productId;
        this.productName = productName;
        this.imageUrl = imageUrl;
        this.type = type;
        this.audience = audience;
        this.size = size;
        this.qty = qty;
        this.maxQty = maxQty;
        this.unitPrice = unitPrice;
        this.lineTotal = unitPrice * qty;
    }

    // -------- getters / setters --------

    public Long getVariantId() { return variantId; }
    public void setVariantId(Long variantId) { this.variantId = variantId; }

    public Long getProductId() { return productId; }
    public void setProductId(Long productId) { this.productId = productId; }

    public String getProductName() { return productName; }
    public void setProductName(String productName) { this.productName = productName; }

    public String getImageUrl() { return imageUrl; }
    public void setImageUrl(String imageUrl) { this.imageUrl = imageUrl; }

    public String getType() { return type; }
    public void setType(String type) { this.type = type; }

    public String getAudience() { return audience; }
    public void setAudience(String audience) { this.audience = audience; }

    public String getSize() { return size; }
    public void setSize(String size) { this.size = size; }

    public Integer getQty() { return qty; }
    public void setQty(Integer qty) {
        this.qty = qty;
        this.lineTotal = this.unitPrice * this.qty;
    }

    public Integer getMaxQty() { return maxQty; }
    public void setMaxQty(Integer maxQty) { this.maxQty = maxQty; }

    public Double getUnitPrice() { return unitPrice; }
    public void setUnitPrice(Double unitPrice) {
        this.unitPrice = unitPrice;
        this.lineTotal = this.unitPrice * this.qty;
    }

    public Double getLineTotal() { return lineTotal; }
}
