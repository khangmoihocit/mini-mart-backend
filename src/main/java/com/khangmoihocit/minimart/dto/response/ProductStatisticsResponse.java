package com.khangmoihocit.minimart.dto.response;

import lombok.*;
import lombok.experimental.FieldDefaults;

import java.math.BigDecimal;

@Data
@AllArgsConstructor
@NoArgsConstructor
@Builder
@FieldDefaults(level = AccessLevel.PRIVATE)
public class ProductStatisticsResponse {
    Long totalProducts;
    Long outOfStockProducts;
    Long lowStockProducts; // Số lượng < 10
    BigDecimal totalInventoryValue;
    BigDecimal averagePrice;
}
