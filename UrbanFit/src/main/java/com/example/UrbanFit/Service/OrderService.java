package com.example.UrbanFit.Service;

import com.example.UrbanFit.Service.interfaces.OrderServiceInterface;
import com.example.UrbanFit.models.*;
import com.example.UrbanFit.models.DTO.order.CreateOrderRequest;
import com.example.UrbanFit.models.DTO.order.OrderDto;
import com.example.UrbanFit.models.DTO.order.OrderItemDto;
import com.example.UrbanFit.models.enums.OrderStatus;
import com.example.UrbanFit.models.enums.PaymentStatus;
import com.example.UrbanFit.repository.CartItemRepo;
import com.example.UrbanFit.repository.OrderRepo;
import com.example.UrbanFit.repository.ProductVariantRepo;
import jakarta.transaction.Transactional;
import org.springframework.stereotype.Service;

import java.util.List;

@Service
@Transactional
public class OrderService implements OrderServiceInterface {

    private final OrderRepo orderRepo;
    private final CartItemRepo cartRepo;
    private final ProductVariantRepo variantRepo;

    public OrderService(OrderRepo orderRepo, CartItemRepo cartRepo, ProductVariantRepo variantRepo) {
        this.orderRepo = orderRepo;
        this.cartRepo = cartRepo;
        this.variantRepo = variantRepo;
    }

    @Override
    public OrderDto createFromCart(Users user, CreateOrderRequest req) {
        List<CartItem> cartItems = cartRepo.findByUser_Id(user.getId());
        if (cartItems.isEmpty()) throw new IllegalStateException("Cart is empty");

        Order order = new Order();
        order.setUser(user);
        order.setStatus(OrderStatus.PENDING);
        order.setPaymentStatus(PaymentStatus.UNPAID);
        order.setShippingName(req.getShippingName());
        order.setShippingPhone(req.getShippingPhone());
        order.setShippingAddress(req.getShippingAddress());

        double total = 0.0;

        for (CartItem ci : cartItems) {
            Long variantId = ci.getVariant().getId();

            ProductVariant managed = variantRepo.findById(variantId)
                    .orElseThrow(() -> new IllegalArgumentException("Variant not found"));

            Product p = managed.getProduct();

            if (!p.isActive()) {
                throw new IllegalArgumentException("Product is not available: " + p.getName());
            }

            int qty = ci.getQuantity();
            int stock = managed.getStockQty() == null ? 0 : managed.getStockQty();

            if (qty > stock) {
                throw new IllegalArgumentException("Not enough stock for size " + managed.getSize());
            }

            double unitPrice = p.getPrice();
            total += unitPrice * qty;

            // snapshot order item
            OrderItem oi = new OrderItem(
                    p.getId(),
                    p.getName(),
                    managed.getId(),
                    managed.getSize(),
                    p.getType().name(),
                    p.getAudience().name(),
                    unitPrice,
                    qty
            );

            order.addItem(oi);

            // decrease stock
            managed.setStockQty(stock - qty);
        }

        order.setTotal(total);

        Order saved = orderRepo.save(order);
        cartRepo.deleteByUser_Id(user.getId());

        return toDto(saved);
    }

    @Override
    @Transactional(Transactional.TxType.SUPPORTS)
    public List<OrderDto> getMyOrders(Users user) {
        return orderRepo.findByUser_IdOrderByCreatedAtDesc(user.getId())
                .stream().map(this::toDto).toList();
    }

    @Override
    @Transactional(Transactional.TxType.SUPPORTS)
    public OrderDto getMyOrderById(Users user, Long orderId) {
        Order order = orderRepo.findByIdAndUser_Id(orderId, user.getId())
                .orElseThrow(() -> new IllegalArgumentException("Order not found"));
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
