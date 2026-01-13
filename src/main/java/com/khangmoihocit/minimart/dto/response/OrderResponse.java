package com.khangmoihocit.minimart.dto.response;

import com.khangmoihocit.minimart.enums.OrderStatus;
import lombok.*;
import lombok.experimental.FieldDefaults;

import java.math.BigDecimal;
import java.time.LocalDateTime;
import java.util.List;

@Data
@NoArgsConstructor
@AllArgsConstructor
@Builder
@FieldDefaults(level = AccessLevel.PRIVATE)
public class OrderResponse {
    String id;
    String userId;
    String fullName;
    String email;
    String phoneNumber;
    String shippingAddress;
    String note;
    OrderStatus status;
    BigDecimal totalMoney;
    String shippingMethod;
    String paymentMethod;
    LocalDateTime orderDate;
    List<OrderDetailResponse> orderDetails;
}

