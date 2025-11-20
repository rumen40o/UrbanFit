package com.example.UrbanFit.controller;

import com.example.UrbanFit.Service.interfaces.OrderServiceInterface;
import com.example.UrbanFit.models.DTO.CreateOrderRequest;
import com.example.UrbanFit.models.DTO.OrderDto;
import com.example.UrbanFit.models.Users;
import jakarta.validation.Valid;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.web.bind.annotation.*;

import java.net.URI;
import java.util.List;

@RestController
@RequestMapping("/orders")
@CrossOrigin(origins = "http://localhost:3000", allowCredentials = "true")
public class OrderController {

    private final OrderServiceInterface service;

    public OrderController(OrderServiceInterface service) {
        this.service = service;
    }

    // Създаване на поръчка от текущата количка
    @PostMapping("/checkout")
    @PreAuthorize("isAuthenticated()")
    public ResponseEntity<OrderDto> checkout(@AuthenticationPrincipal Users user,
                                             @Valid @RequestBody CreateOrderRequest req) {
        OrderDto created = service.createFromCart(user, req);
        return ResponseEntity.created(URI.create("/orders/" + created.getId())).body(created);
    }

    // Всички поръчки на текущия потребител
    @GetMapping("/my")
    @PreAuthorize("isAuthenticated()")
    public List<OrderDto> getMyOrders(@AuthenticationPrincipal Users user) {
        return service.getMyOrders(user);
    }

    // Конкретна поръчка на текущия потребител
    @GetMapping("/{id}")
    @PreAuthorize("isAuthenticated()")
    public OrderDto getMyOrder(@AuthenticationPrincipal Users user,
                               @PathVariable Long id) {
        return service.getMyOrderById(user, id);
    }
}
