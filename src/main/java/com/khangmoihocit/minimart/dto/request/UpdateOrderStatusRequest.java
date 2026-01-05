package com.khangmoihocit.minimart.dto.request;

import com.khangmoihocit.minimart.enums.OrderStatus;
import jakarta.validation.constraints.NotNull;
import lombok.*;
import lombok.experimental.FieldDefaults;

@Data
@NoArgsConstructor
@AllArgsConstructor
@Builder
@FieldDefaults(level = AccessLevel.PRIVATE)
public class UpdateOrderStatusRequest {
    @NotNull(message = "Order status is required")
    OrderStatus status;
}

