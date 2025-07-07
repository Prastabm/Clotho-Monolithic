package com.clotho.monolithic.order.repository;


import com.clotho.monolithic.order.model.Order;
import org.springframework.data.jpa.repository.JpaRepository;

public interface OrderRepository extends JpaRepository<Order, Long> {
}

