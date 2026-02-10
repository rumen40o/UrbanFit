package com.example.UrbanFit.repository;

import com.example.UrbanFit.models.Order;
import com.example.UrbanFit.models.enums.OrderStatus;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.List;
import java.util.Optional;

public interface OrderRepo extends JpaRepository<Order, Long> {
    List<Order> findByUser_IdOrderByCreatedAtDesc(Long userId);
    Page<Order> findAllByOrderByCreatedAtDesc(Pageable pageable);
    Page<Order> findByStatusOrderByCreatedAtDesc(OrderStatus status, Pageable pageable);
    Optional<Order> findByIdAndUser_Id(Long id, Long userId);

    // find orders that have items referencing a product id snapshot
    @org.springframework.data.jpa.repository.Query("select distinct o from Order o join o.items i where i.productId = :productId")
    java.util.List<Order> findByItemProductId(@org.springframework.data.repository.query.Param("productId") Long productId);
}
