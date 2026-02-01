package com.example.UrbanFit.models.DTO.product;

public class VariantDto {
    private Long id;
    private String size;
    private Integer stockQty;

    public VariantDto(Long id, String size, Integer stockQty) {
        this.id = id; this.size = size; this.stockQty = stockQty;
    }
    public Long getId(){ return id; }
    public String getSize(){ return size; }
    public Integer getStockQty(){ return stockQty; }
    public void setId(Long id) {
        this.id = id;
    }
    public void setSize(String size) {
        this.size = size;
    }
    public void setStockQty(Integer stockQty) {
        this.stockQty = stockQty;
    }
}
