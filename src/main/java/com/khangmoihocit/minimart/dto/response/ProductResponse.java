package com.khangmoihocit.minimart.dto.response;

import lombok.*;
import lombok.experimental.FieldDefaults;

import java.math.BigDecimal;
import java.time.LocalDateTime;
import java.util.List;

@Data
@AllArgsConstructor
@NoArgsConstructor
@Builder
@FieldDefaults(level = AccessLevel.PRIVATE)
public class ProductResponse {
    String id;
    String name;
    BigDecimal price;
    BigDecimal salePrice;
    String description;
    Integer stockQuantity = 0;
    LocalDateTime createdAt;
    LocalDateTime updatedAt;

    CategoryResponse category;
    List<String> images;
}
