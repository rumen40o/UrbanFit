package com.example.UrbanFit.models.DTO.product;

public class UpdateVariantStockRequest {
    private Long id;        // variantId (null for new variants)
    private String size;    // size (required)
    private Integer stockQty;

    public Long getId() { return id; }
    public void setId(Long id) { this.id = id; }
    
    public String getSize() { return size; }
    public void setSize(String size) { this.size = size; }
    
    public Integer getStockQty() { return stockQty; }
    public void setStockQty(Integer stockQty) { this.stockQty = stockQty; }
}
