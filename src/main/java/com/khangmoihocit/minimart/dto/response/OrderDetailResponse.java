package com.khangmoihocit.minimart.dto.response;

import lombok.*;
import lombok.experimental.FieldDefaults;

import java.math.BigDecimal;

@Data
@NoArgsConstructor
@AllArgsConstructor
@Builder
@FieldDefaults(level = AccessLevel.PRIVATE)
public class OrderDetailResponse {
    String id;
    String productId;
    String productName;
    String productImage;
    String productSize;
    BigDecimal price;
    Integer numberOfProducts;
    BigDecimal totalMoney;
}

