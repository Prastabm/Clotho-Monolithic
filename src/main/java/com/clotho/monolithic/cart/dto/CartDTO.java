package com.clotho.monolithic.cart.dto;

import lombok.*;

@Data
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class CartDTO {
    private String skuCode;
    private Integer quantity;
    private Double price;
}
