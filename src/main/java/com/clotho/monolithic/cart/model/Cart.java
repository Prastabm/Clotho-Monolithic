package com.clotho.monolithic.cart.model;

import jakarta.persistence.*;
import lombok.*;

@Entity
@Table(name = "cart_items")
@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class Cart {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    private String skuCode; // or skuCode
    private String category;
    private String email;
    private Integer quantity;
    private Double price; // optional: useful during checkout to lock price
}
