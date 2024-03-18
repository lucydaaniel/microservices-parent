package com.schabikowski.OrderService.dto;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.math.BigDecimal;

@NoArgsConstructor
@AllArgsConstructor
@Data
public class OrderLinesItemsDto {
    private String skuCode;
    private BigDecimal price;
    private Integer quantity;
}
