package com.clotho.monolithic.order.repository;


import com.clotho.monolithic.order.model.OrderLineItem;
import org.springframework.data.jpa.repository.JpaRepository;

public interface OrderLineItemRepository extends JpaRepository<OrderLineItem, Long> {
}
