package com.clotho.monolithic.cart.repository;

import com.clotho.monolithic.cart.model.Cart;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Modifying; // Import this

import java.util.List;

public interface CartRepository extends JpaRepository<Cart, Long> {
    List<Cart> findByEmail(String email);

    @Modifying // Add this annotation
    void deleteByEmail(String email);
}