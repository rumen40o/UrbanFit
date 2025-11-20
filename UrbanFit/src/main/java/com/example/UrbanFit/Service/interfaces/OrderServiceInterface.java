package com.example.UrbanFit.Service.interfaces;

import com.example.UrbanFit.models.DTO.CreateOrderRequest;
import com.example.UrbanFit.models.DTO.OrderDto;
import com.example.UrbanFit.models.Users;

import java.util.List;

public interface OrderServiceInterface {
    OrderDto createFromCart(Users user, CreateOrderRequest req);
    List<OrderDto> getMyOrders(Users user);
    OrderDto getMyOrderById(Users user, Long orderId);
}
