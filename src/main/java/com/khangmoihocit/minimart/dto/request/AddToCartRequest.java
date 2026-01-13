package com.khangmoihocit.minimart.dto.request;

import lombok.experimental.FieldDefaults;
import lombok.*;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.Min;


@FieldDefaults(level = AccessLevel.PRIVATE)
@Builder
@NoArgsConstructor
@AllArgsConstructor
@Data
public class AddToCartRequest {
    @Min(value = 1, message = "Số lượng phải lớn hơn 0")
    @NotNull(message = "Số lượng không được để trống")
    Integer quantity;

    @NotBlank(message = "Product ID không được để trống")
    String productId;

    String productSizeId; // Optional: nếu sản phẩm có size
}
