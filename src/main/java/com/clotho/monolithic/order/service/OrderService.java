package com.clotho.monolithic.order.service;



import com.clotho.monolithic.order.dto.OrderLineItemDto;
import com.clotho.monolithic.order.dto.OrderRequest;
import com.clotho.monolithic.order.model.Order;
import com.clotho.monolithic.order.model.OrderLineItem;
import com.clotho.monolithic.order.repository.OrderRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

import java.time.LocalDateTime;
import java.util.List;
import java.util.UUID;

@Service
@RequiredArgsConstructor
public class OrderService {

    private final OrderRepository orderRepository;

    public void placeOrder(OrderRequest orderRequest, String userEmail) {
        List<OrderLineItem> orderLineItems = orderRequest.getOrderLineItems().stream()
                .map(this::mapToEntity)
                .toList();

        Order order = Order.builder()
                .orderNumber(UUID.randomUUID().toString())
                .orderDate(LocalDateTime.now())
                .status("CREATED")
                .address(orderRequest.getAddress())
                .email(userEmail)
                .orderLineItems(orderLineItems)
                .build();

        orderLineItems.forEach(item -> item.setOrder(order));
        orderRepository.save(order);
    }

    public List<Order> getOrdersByEmail(String email) {
        return orderRepository.findByEmail(email);
    }

    private OrderLineItem mapToEntity(OrderLineItemDto dto) {
        return OrderLineItem.builder()
                .skuCode(dto.getSkuCode())
                .price(dto.getPrice())
                .quantity(dto.getQuantity())
                .build();
    }


}
