package com.khangmoihocit.minimart.dto.request;

import jakarta.validation.constraints.Email;
import jakarta.validation.constraints.NotBlank;
import lombok.*;
import lombok.experimental.FieldDefaults;

@Data
@NoArgsConstructor
@AllArgsConstructor
@Builder
@FieldDefaults(level = AccessLevel.PRIVATE)
public class OrderRequest {
    @NotBlank(message = "Full name is required")
    String fullName;

    @NotBlank(message = "Email là bắt buộc")
    @Email(message = "Email is not valid")
    String email;

    @NotBlank(message = "Số điện thoại là bắt buộc")
    String phoneNumber;

    @NotBlank(message = "Địa chỉ giao hàng là bắt buộc")
    String shippingAddress;

    String note;

    @NotBlank(message = "Phương thức vận chuyển là bắt buộc")
    String shippingMethod;

    @NotBlank(message = "Phương thức thanh toán là bắt buộc")
    String paymentMethod;
}




