package com.khangmoihocit.minimart.dto.response;

import com.khangmoihocit.minimart.entity.Category;
import jakarta.persistence.Column;
import jakarta.persistence.JoinColumn;
import jakarta.persistence.ManyToOne;
import lombok.*;
import lombok.experimental.FieldDefaults;
import org.hibernate.annotations.OnDelete;
import org.hibernate.annotations.OnDeleteAction;

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
    String thumbnail;
    String description;
    Integer stockQuantity = 0;
    LocalDateTime createdAt;
    LocalDateTime updatedAt;

    CategoryResponse category;
    List<String> images;
}
