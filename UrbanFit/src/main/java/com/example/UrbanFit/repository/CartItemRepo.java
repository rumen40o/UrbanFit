package com.example.UrbanFit.repository;

import com.example.UrbanFit.models.CartItem;
import com.example.UrbanFit.models.ProductVariant;
import com.example.UrbanFit.models.Users;
import org.springframework.data.jpa.repository.EntityGraph;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.List;
import java.util.Optional;

public interface CartItemRepo extends JpaRepository<CartItem, Long> {

    // зареждаме variant + product наведнъж (за да няма N+1)
    @EntityGraph(attributePaths = {"variant", "variant.product"})
    List<CartItem> findByUser_Id(Long userId);

    Optional<CartItem> findByUserAndVariant(Users user, ProductVariant variant);

    void deleteByUser_Id(Long userId);
    // delete cart items that reference variants of a given product
    void deleteByVariant_Product_Id(Long productId);
}
