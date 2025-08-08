package com.clotho.monolithic.common.controller;

import com.clotho.monolithic.cart.model.Cart;
import com.clotho.monolithic.cart.service.CartService;
import com.clotho.monolithic.inventory.service.InventoryService;
import com.clotho.monolithic.config.StripeService;
import com.stripe.exception.StripeException;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.Authentication;
import org.springframework.web.bind.annotation.*;

import java.util.HashMap;
import java.util.List;
import java.util.Map;

@RestController
@RequestMapping("/api/checkout")
@RequiredArgsConstructor
public class CheckoutController {

    private final CartService cartService;
    private final InventoryService inventoryService;
    private final StripeService stripeService;

    @PostMapping
    public ResponseEntity<Map<String, String>> createPaymentIntent(Authentication authentication) throws StripeException {
        String email = authentication.getName();
        List<Cart> cartItems = cartService.getUserCart(email);

        // Check inventory availability
        cartItems.forEach(item -> inventoryService.checkAvailability(item.getSkuCode(), item.getQuantity()));

        // Calculate amount in cents
        long amount = cartItems.stream()
                .mapToLong(item -> (long) (item.getPrice() * item.getQuantity() * 100))
                .sum();

        // **MODIFICATION**: Add user's email to metadata for reliability
        Map<String, String> metadata = new HashMap<>();
        metadata.put("userEmail", email);

        // **MODIFICATION**: Pass metadata when creating the PaymentIntent
        // Note: You will need to update your StripeService.createPaymentIntent method
        // to accept a metadata Map.
        String clientSecret = stripeService.createPaymentIntent(amount, "inr", metadata); // Changed to INR for consistency

        Map<String, String> response = new HashMap<>();
        response.put("clientSecret", clientSecret);
        return ResponseEntity.ok(response);
    }
}