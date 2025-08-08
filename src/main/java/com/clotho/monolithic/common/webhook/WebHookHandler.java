package com.clotho.monolithic.common.webhook;

import com.clotho.monolithic.order.service.OrderService;
import com.stripe.exception.SignatureVerificationException;
import com.stripe.model.Event;
import com.stripe.model.PaymentIntent;
import com.stripe.net.Webhook;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

@Slf4j
@RestController
@RequestMapping("/api/stripe")
@RequiredArgsConstructor
public class WebHookHandler {

    @Value("${stripe.webhook.secret}")
    private String webhookSecret;

    // **MODIFICATION**: Only OrderService is needed here
    private final OrderService orderService;

    @PostMapping("/webhook")
    public ResponseEntity<String> handle(@RequestBody String payload, @RequestHeader("Stripe-Signature") String sigHeader) {
        Event event;
        try {
            event = Webhook.constructEvent(payload, sigHeader, webhookSecret);
        } catch (SignatureVerificationException e) {
            log.error("Stripe webhook error :: Invalid signature.", e);
            return ResponseEntity.status(HttpStatus.BAD_REQUEST).body("Invalid Stripe signature.");
        }

        if ("payment_intent.succeeded".equals(event.getType())) {
            PaymentIntent intent = (PaymentIntent) event.getDataObjectDeserializer()
                    .getObject()
                    .orElse(null);

            if (intent != null) {
                try {
                    // **MODIFICATION**: Delegate all work to the transactional service method
                    orderService.processSuccessfulPayment(intent);
                } catch (Exception e) {
                    log.error("Failed to process successful payment for intent {}: {}", intent.getId(), e.getMessage());
                    // Return an error to Stripe. It might retry sending the webhook.
                    return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).body("Failed to process payment.");
                }
            }
        }

        return ResponseEntity.ok("Success");
    }
}