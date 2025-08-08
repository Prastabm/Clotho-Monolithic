package com.clotho.monolithic.cart.service;

import com.clotho.monolithic.cart.dto.CartDTO;
import com.clotho.monolithic.cart.model.Cart;
import com.clotho.monolithic.cart.repository.CartRepository;
import com.clotho.monolithic.order.dto.OrderLineItemDto;
import lombok.RequiredArgsConstructor;
import org.springframework.security.access.AccessDeniedException;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional; // Import this

import java.util.List;

@Service
@RequiredArgsConstructor
public class CartService {
    // ... (no changes to the rest of the class)
    private final CartRepository cartRepository;

    public void addToCart(String email, CartDTO dto) {
        Cart item = Cart.builder()
                .skuCode(dto.getSkuCode())
                .email(email)
                .quantity(dto.getQuantity())
                .price(dto.getPrice())
                .build();
        cartRepository.save(item);
    }
    public void updateQuantity(String email, Long itemId, int quantity) {
        Cart cartItem = cartRepository.findById(itemId)
                .orElseThrow(() -> new RuntimeException("Cart item not found"));

        if (!cartItem.getEmail().equals(email)) {
            throw new RuntimeException("Unauthorized to update this cart item");
        }

        cartItem.setQuantity(quantity);
        cartRepository.save(cartItem);
    }

    public List<Cart> getUserCart(String email) {
        return cartRepository.findByEmail(email);
    }

    @Transactional // Add this annotation
    public void clearCart(String email) {
        cartRepository.deleteByEmail(email);
    }

    public List<OrderLineItemDto> convertCartToOrderItems(String email) {
        return getUserCart(email).stream()
                .map(item -> OrderLineItemDto.builder()
                        .skuCode(item.getSkuCode())
                        .price(item.getPrice())
                        .quantity(item.getQuantity())
                        .build())
                .toList();
    }

    public void removeCartItemByIdAndEmail(Long id, String email) {
        Cart cart = cartRepository.findById(id)
                .orElseThrow(() -> new RuntimeException("Cart item not found"));

        if (!cart.getEmail().equals(email)) {
            throw new AccessDeniedException("Unauthorized to delete this cart item");
        }

        cartRepository.deleteById(id);
    }
}