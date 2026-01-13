package com.khangmoihocit.minimart.dto.response;
import java.math.BigDecimal;

import lombok.experimental.FieldDefaults;
import lombok.*;


@FieldDefaults(level = AccessLevel.PRIVATE)
@Builder
@NoArgsConstructor
@AllArgsConstructor
@Data
public class CartItemResponse {
    String id;
    String productId;
    String productName;
    BigDecimal price;
    BigDecimal salePrice;
    String imageUrl;
    Integer quantity;
    BigDecimal subtotal; // Tổng tiền của item này (price * quantity)

    // Thông tin size nếu có
    String productSizeId;
    String sizeName;
    Integer availableQuantity; // Số lượng còn lại của size này
}


