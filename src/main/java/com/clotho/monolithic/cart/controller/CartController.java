package com.clotho.monolithic.cart.controller;

import com.clotho.monolithic.cart.dto.CartDTO;
import com.clotho.monolithic.cart.model.Cart;
import com.clotho.monolithic.cart.service.CartService;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.Authentication;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/api/cart")
@RequiredArgsConstructor
public class CartController {

    private final CartService cartService;

    // Add an item to the cart
    @PostMapping
    public ResponseEntity<String> addToCart(@RequestBody CartDTO dto, Authentication authentication) {
        String email = authentication.getName();
        cartService.addToCart(email, dto);
        return ResponseEntity.ok("Item added to cart");
    }

    // Get all items in the user's cart
    @GetMapping
    public ResponseEntity<List<Cart>> getUserCart(Authentication authentication) {
        String email = authentication.getName();
        return ResponseEntity.ok(cartService.getUserCart(email));
    }

    // Update quantity of a specific cart item
    @PutMapping("/{itemId}")
    public ResponseEntity<String> updateCartItem(
            @PathVariable Long itemId,
            @RequestBody CartDTO dto,
            Authentication authentication) {

        String email = authentication.getName();
        cartService.updateQuantity(email, itemId, dto.getQuantity());
        return ResponseEntity.ok("Cart item quantity updated");
    }

    // Remove a specific item from the cart
    @DeleteMapping("/{itemId}")
    public ResponseEntity<String> removeCartItem(
            @PathVariable Long itemId,
            Authentication authentication) {

        String email = authentication.getName();
        cartService.removeCartItemByIdAndEmail(itemId, email);
        return ResponseEntity.ok("Cart item removed");
    }

    // Clear the entire cart for the user
    @DeleteMapping
    public ResponseEntity<String> clearCart(Authentication authentication) {
        String email = authentication.getName();
        cartService.clearCart(email);
        return ResponseEntity.ok("Cart cleared");
    }
}
