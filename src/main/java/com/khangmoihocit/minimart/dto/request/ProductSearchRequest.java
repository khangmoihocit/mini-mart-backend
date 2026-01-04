package com.khangmoihocit.minimart.dto.request;

import lombok.*;
import lombok.experimental.FieldDefaults;

import java.math.BigDecimal;

@Data
@AllArgsConstructor
@NoArgsConstructor
@Builder
@FieldDefaults(level = AccessLevel.PRIVATE)
public class ProductSearchRequest {
    String keyword;
    String categoryId;
    BigDecimal minPrice;
    BigDecimal maxPrice;
    String sortBy; // price_asc, price_desc, newest
    Integer pageNo;
    Integer pageSize;
}

