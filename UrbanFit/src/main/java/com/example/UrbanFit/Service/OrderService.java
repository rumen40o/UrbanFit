package com.example.UrbanFit.Service;

import com.example.UrbanFit.Service.interfaces.OrderServiceInterface;
import com.example.UrbanFit.models.*;
import com.example.UrbanFit.models.DTO.CreateOrderRequest;
import com.example.UrbanFit.models.DTO.OrderDto;
import com.example.UrbanFit.models.DTO.OrderItemDto;
import com.example.UrbanFit.models.enums.OrderStatus;
import com.example.UrbanFit.models.enums.PaymentStatus;
import com.example.UrbanFit.repository.CartItemRepo;
import com.example.UrbanFit.repository.OrderRepo;
import jakarta.transaction.Transactional;
import org.springframework.stereotype.Service;

import java.util.List;

@Service
@Transactional
public class OrderService implements OrderServiceInterface {

    private final OrderRepo orderRepo;
    private final CartItemRepo cartRepo;

    public OrderService(OrderRepo orderRepo, CartItemRepo cartRepo) {
        this.orderRepo = orderRepo;
        this.cartRepo = cartRepo;
    }

    @Override
    public OrderDto createFromCart(Users user, CreateOrderRequest req) {
        List<CartItem> cartItems = cartRepo.findByUser_Id(user.getId());
        if (cartItems.isEmpty()) {
            throw new IllegalStateException("Cart is empty");
        }

        Order order = new Order();
        order.setUser(user);
        order.setStatus(OrderStatus.PENDING);
        order.setPaymentStatus(PaymentStatus.UNPAID);
        order.setShippingName(req.getShippingName());
        order.setShippingPhone(req.getShippingPhone());
        order.setShippingAddress(req.getShippingAddress());

        double total = 0.0;
        for (CartItem ci : cartItems) {
            Product product = ci.getProduct();
            double unitPrice = product.getPrice();
            int qty = ci.getQuantity();
            total += unitPrice * qty;

            OrderItem oi = new OrderItem(product, unitPrice, qty);
            order.addItem(oi);
        }
        order.setTotal(total);

        Order saved = orderRepo.save(order);
        cartRepo.deleteByUser_Id(user.getId()); // чистим количката

        return toDto(saved);
    }

    @Override
    @Transactional()
    public List<OrderDto> getMyOrders(Users user) {
        return orderRepo.findByUser_IdOrderByCreatedAtDesc(user.getId())
                .stream().map(this::toDto).toList();
    }

    @Override
    @Transactional()
    public OrderDto getMyOrderById(Users user, Long orderId) {
        Order order = orderRepo.findByIdAndUser_Id(orderId, user.getId())
                .orElseThrow(() -> new IllegalArgumentException("Order not found"));
        return toDto(order);
    }

    // helpers
    private OrderDto toDto(Order order) {
        List<OrderItemDto> items = order.getItems().stream()
                .map(oi -> new OrderItemDto(
                        oi.getProduct().getId(),
                        oi.getProduct().getName(),
                        oi.getQuantity(),
                        oi.getUnitPrice()
                )).toList();

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
