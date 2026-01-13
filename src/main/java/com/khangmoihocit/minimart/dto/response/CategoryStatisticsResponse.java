package com.khangmoihocit.minimart.dto.response;

import lombok.*;
import lombok.experimental.FieldDefaults;

import java.math.BigDecimal;

@Data
@AllArgsConstructor
@NoArgsConstructor
@Builder
@FieldDefaults(level = AccessLevel.PRIVATE)
public class CategoryStatisticsResponse {
    String categoryId;
    String categoryName;
    Long productCount;
    Long totalSold;
    BigDecimal totalRevenue;
}

