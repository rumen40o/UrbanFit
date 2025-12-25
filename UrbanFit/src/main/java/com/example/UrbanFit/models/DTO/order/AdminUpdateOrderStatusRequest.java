package com.example.UrbanFit.models.DTO.order;

import com.example.UrbanFit.models.enums.OrderStatus;
import jakarta.validation.constraints.NotNull;

public class AdminUpdateOrderStatusRequest {
    @NotNull
    private OrderStatus status;

    public OrderStatus getStatus() { return status; }
    public void setStatus(OrderStatus status) { this.status = status; }
}
