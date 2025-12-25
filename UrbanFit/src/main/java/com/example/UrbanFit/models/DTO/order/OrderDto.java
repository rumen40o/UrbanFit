package com.example.UrbanFit.models.DTO.order;

import com.example.UrbanFit.models.enums.OrderStatus;
import com.example.UrbanFit.models.enums.PaymentStatus;

import java.time.Instant;
import java.util.List;

public class OrderDto {
    private Long id;
    private Double total;
    private OrderStatus status;
    private PaymentStatus paymentStatus;
    private String shippingName;
    private String shippingPhone;
    private String shippingAddress;
    private Instant createdAt;
    private List<OrderItemDto> items;

    public OrderDto() {}

    public OrderDto(Long id, Double total, OrderStatus status, PaymentStatus paymentStatus,
                    String shippingName, String shippingPhone, String shippingAddress,
                    Instant createdAt, List<OrderItemDto> items) {
        this.id = id;
        this.total = total;
        this.status = status;
        this.paymentStatus = paymentStatus;
        this.shippingName = shippingName;
        this.shippingPhone = shippingPhone;
        this.shippingAddress = shippingAddress;
        this.createdAt = createdAt;
        this.items = items;
    }

    public Long getId() { return id; }
    public void setId(Long id) { this.id = id; }
    public Double getTotal() { return total; }
    public void setTotal(Double total) { this.total = total; }
    public OrderStatus getStatus() { return status; }
    public void setStatus(OrderStatus status) { this.status = status; }
    public PaymentStatus getPaymentStatus() { return paymentStatus; }
    public void setPaymentStatus(PaymentStatus paymentStatus) { this.paymentStatus = paymentStatus; }
    public String getShippingName() { return shippingName; }
    public void setShippingName(String shippingName) { this.shippingName = shippingName; }
    public String getShippingPhone() { return shippingPhone; }
    public void setShippingPhone(String shippingPhone) { this.shippingPhone = shippingPhone; }
    public String getShippingAddress() { return shippingAddress; }
    public void setShippingAddress(String shippingAddress) { this.shippingAddress = shippingAddress; }
    public Instant getCreatedAt() { return createdAt; }
    public void setCreatedAt(Instant createdAt) { this.createdAt = createdAt; }
    public List<OrderItemDto> getItems() { return items; }
    public void setItems(List<OrderItemDto> items) { this.items = items; }
}
