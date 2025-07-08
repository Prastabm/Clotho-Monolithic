package com.clotho.monolithic.order.controller;

import com.clotho.monolithic.order.dto.OrderRequest;
import com.clotho.monolithic.order.service.OrderService;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.Authentication;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("/api/orders")
@RequiredArgsConstructor
public class OrderController {

    private final OrderService orderService;

    @PostMapping
    @ResponseStatus(HttpStatus.CREATED)
    public String placeOrder(@RequestBody OrderRequest orderRequest,
                             @org.springframework.security.core.annotation.AuthenticationPrincipal String email) {
        orderService.placeOrder(orderRequest, email);
        return "Order placed successfully";
    }

    @GetMapping("/me")
    public ResponseEntity<?> getMyOrders(Authentication authentication) {
        String email = authentication.getName(); // extract current user email
        return ResponseEntity.ok(orderService.getOrdersByEmail(email));
    }
}
