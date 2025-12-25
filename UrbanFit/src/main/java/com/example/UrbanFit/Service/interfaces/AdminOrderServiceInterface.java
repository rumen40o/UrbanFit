package com.example.UrbanFit.Service.interfaces;

import com.example.UrbanFit.models.DTO.order.OrderDto;
import com.example.UrbanFit.models.DTO.order.AdminUpdateOrderStatusRequest;
import com.example.UrbanFit.models.enums.OrderStatus;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;


public interface AdminOrderServiceInterface {
    Page<OrderDto> list(Pageable pageable, OrderStatus status);
    OrderDto getById(Long id);
    OrderDto updateStatus(Long id, AdminUpdateOrderStatusRequest req);
}
