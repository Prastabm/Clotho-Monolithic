package com.clotho.monolithic.order.service;

import com.clotho.monolithic.cart.model.Cart;
import com.clotho.monolithic.cart.service.CartService;
import com.clotho.monolithic.inventory.service.InventoryService;
import com.clotho.monolithic.order.dto.OrderLineItemDto;
import com.clotho.monolithic.order.dto.OrderRequest;
import com.clotho.monolithic.order.model.Order;
import com.clotho.monolithic.order.model.OrderLineItem;
import com.clotho.monolithic.order.repository.OrderRepository;
import com.stripe.exception.StripeException;
import com.stripe.model.Address;
import com.stripe.model.Charge;
import com.stripe.model.PaymentIntent;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.time.LocalDateTime;
import java.util.List;
import java.util.UUID;

@Slf4j
@Service
@RequiredArgsConstructor
public class OrderService {

    private final OrderRepository orderRepository;
    // **MODIFICATION**: Inject dependent services
    private final CartService cartService;
    private final InventoryService inventoryService;


    /**
     * **NEW METHOD**
     * This method contains all the logic that must run after a successful payment.
     * The @Transactional annotation ensures all database operations are atomic.
     */
    @Transactional
    public void processSuccessfulPayment(PaymentIntent intent) throws StripeException {
        // 1. Get user email from metadata
        String email = intent.getMetadata().get("userEmail");
        if (email == null || email.isBlank()) {
            throw new IllegalStateException("User email not found in payment metadata for intent: " + intent.getId());
        }

        // 2. Retrieve Charge and extract the shipping address
        String chargeId = intent.getLatestCharge();
        Charge charge = Charge.retrieve(chargeId);
        Address shippingAddress = charge.getBillingDetails().getAddress();
        String formattedAddress = "N/A";
        if (shippingAddress != null) {
            formattedAddress = String.join(", ",
                    shippingAddress.getLine1(),
                    shippingAddress.getCity(),
                    shippingAddress.getState(),
                    shippingAddress.getPostalCode(),
                    shippingAddress.getCountry()
            ).replaceAll(", null", "").replaceAll("null, ", "").replaceAll(", ,", ",");
        }

        // 3. Get user's cart and reduce stock
        List<Cart> cartItems = cartService.getUserCart(email);
        cartItems.forEach(item -> inventoryService.reduceStock(item.getSkuCode(), item.getQuantity()));

        // 4. Create the OrderRequest from the cart items
        OrderRequest orderRequest = new OrderRequest();
        orderRequest.setAddress(formattedAddress);
        orderRequest.setOrderLineItems(cartItems.stream()
                .map(item -> OrderLineItemDto.builder()
                        .skuCode(item.getSkuCode())
                        .category(item.getCategory())
                        .price(item.getPrice())
                        .quantity(item.getQuantity())
                        .build())
                .toList());

        // 5. Place the order and clear the cart
        this.placeOrder(orderRequest, email);
        cartService.clearCart(email);

        log.info("Successfully created order {} for user {}", orderRequest.getAddress(), email);
    }

    // This is your original method, now called from within the transaction
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

    public List<Order> getAllOrders() {
        return orderRepository.findAll();
    }

    private OrderLineItem mapToEntity(OrderLineItemDto dto) {
        return OrderLineItem.builder()
                .skuCode(dto.getSkuCode())
                .category(dto.getCategory())
                .price(dto.getPrice())
                .quantity(dto.getQuantity())
                .build();
    }
}