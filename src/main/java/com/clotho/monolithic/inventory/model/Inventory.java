package com.clotho.monolithic.inventory.model;

import jakarta.persistence.*;
import lombok.*;

@Entity
@Table(name = "inventories")
@Data
@AllArgsConstructor
@NoArgsConstructor
@Builder
public class Inventory {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @Column(nullable = false, unique = true)
    private String skuCode; // Linking inventory to product by SKU

    private Integer quantity;
}
