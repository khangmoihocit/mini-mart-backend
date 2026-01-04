package com.khangmoihocit.minimart.dto.response;

import lombok.*;
import lombok.experimental.FieldDefaults;

import java.math.BigDecimal;
import java.util.List;

@Data
@AllArgsConstructor
@NoArgsConstructor
@Builder
@FieldDefaults(level = AccessLevel.PRIVATE)
public class CartResponse {
    String cartId;
    String userId;
    List<CartItemResponse> items;
    Integer totalItems; // Tổng số items trong giỏ
    Integer totalQuantity; // Tổng số lượng sản phẩm
    BigDecimal totalAmount; // Tổng tiền của giỏ hàng
}

