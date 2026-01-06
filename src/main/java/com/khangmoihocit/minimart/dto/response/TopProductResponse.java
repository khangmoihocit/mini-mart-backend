package com.khangmoihocit.minimart.dto.response;

import lombok.*;
import lombok.experimental.FieldDefaults;

import java.math.BigDecimal;

@Data
@AllArgsConstructor
@NoArgsConstructor
@Builder
@FieldDefaults(level = AccessLevel.PRIVATE)
public class TopProductResponse {
    String productId;
    String productName;
    Long totalSold;
    BigDecimal totalRevenue;
}

