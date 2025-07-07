package com.clotho.monolithic.order.dto;


import lombok.*;

import java.util.List;

@Data
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class OrderRequest {
    private String address;
    private List<OrderLineItemDto> orderLineItems;
}
