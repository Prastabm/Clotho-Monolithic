package com.clotho.monolithic.order.dto;

import lombok.*;

@Data
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class OrderLineItemDto {
    private String skuCode;
    private Double price;
    private Integer quantity;
}
