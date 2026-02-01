package com.example.UrbanFit.Service;

import com.example.UrbanFit.Service.interfaces.AdminOrderServiceInterface;
import com.example.UrbanFit.models.DTO.order.AdminUpdateOrderStatusRequest;
import com.example.UrbanFit.models.DTO.order.OrderDto;
import com.example.UrbanFit.models.DTO.order.OrderItemDto;
import com.example.UrbanFit.models.Order;
import com.example.UrbanFit.models.enums.OrderStatus;
import com.example.UrbanFit.repository.OrderRepo;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;

@Service
@Transactional
public class AdminOrderService implements AdminOrderServiceInterface {

    private final OrderRepo orderRepo;

    public AdminOrderService(OrderRepo orderRepo) {
        this.orderRepo = orderRepo;
    }

    @Override
    @Transactional(readOnly = true)
    public Page<OrderDto> list(Pageable pageable, OrderStatus status) {
        Page<Order> page = (status == null)
                ? orderRepo.findAllByOrderByCreatedAtDesc(pageable)
                : orderRepo.findByStatusOrderByCreatedAtDesc(status, pageable);

        return page.map(this::toDto);
    }

    @Override
    @Transactional(readOnly = true)
    public OrderDto getById(Long id) {
        Order order = orderRepo.findById(id)
                .orElseThrow(() -> new IllegalArgumentException("Order not found"));
        return toDto(order);
    }

    @Override
    public OrderDto updateStatus(Long id, AdminUpdateOrderStatusRequest req) {
        Order order = orderRepo.findById(id)
                .orElseThrow(() -> new IllegalArgumentException("Order not found"));

        order.setStatus(req.getStatus());
        // save не е нужен, защото сме в транзакция и entity е managed
        return toDto(order);
    }

    private OrderDto toDto(Order order) {
        List<OrderItemDto> items = order.getItems().stream()
                .map(oi -> new OrderItemDto(
                        oi.getProductId(),
                        oi.getProductName(),
                        oi.getVariantId(),
                        oi.getSize(),
                        oi.getType(),
                        oi.getAudience(),
                        oi.getQuantity(),
                        oi.getUnitPrice()
                ))
                .toList();

        return new OrderDto(
                order.getId(),
                order.getTotal(),
                order.getStatus(),
                order.getPaymentStatus(),
                order.getShippingName(),
                order.getShippingPhone(),
                order.getShippingAddress(),
                order.getCreatedAt(),
                items
        );
    }
}
