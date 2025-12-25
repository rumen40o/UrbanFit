package com.example.UrbanFit.controller;

import com.example.UrbanFit.Service.interfaces.AdminOrderServiceInterface;
import com.example.UrbanFit.models.DTO.order.AdminUpdateOrderStatusRequest;
import com.example.UrbanFit.models.DTO.order.OrderDto;
import com.example.UrbanFit.models.enums.OrderStatus;
import jakarta.validation.Valid;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.web.PageableDefault;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("/admin/orders")
@CrossOrigin(origins = "http://localhost:3000", allowCredentials = "true")
@PreAuthorize("hasRole('ADMIN')")
public class AdminOrderController {

    private final AdminOrderServiceInterface service;

    public AdminOrderController(AdminOrderServiceInterface service) {
        this.service = service;
    }

    @GetMapping
    public Page<OrderDto> list(
            @PageableDefault(size = 20, sort = "createdAt") Pageable pageable,
            @RequestParam(required = false) OrderStatus status
    ) {
        return service.list(pageable, status);
    }

    @GetMapping("/{id}")
    public OrderDto get(@PathVariable Long id) {
        return service.getById(id);
    }

    @PatchMapping("/{id}/status")
    public ResponseEntity<OrderDto> updateStatus(
            @PathVariable Long id,
            @Valid @RequestBody AdminUpdateOrderStatusRequest req
    ) {
        return ResponseEntity.ok(service.updateStatus(id, req));
    }
}
