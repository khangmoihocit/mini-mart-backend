package com.khangmoihocit.minimart.dto.request;

import com.khangmoihocit.minimart.entity.Role;
import jakarta.validation.constraints.Email;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.Past;
import jakarta.validation.constraints.Size;
import lombok.*;
import lombok.experimental.FieldDefaults;

import java.time.LocalDate;

@Data
@AllArgsConstructor
@NoArgsConstructor
@Builder
@FieldDefaults(level = AccessLevel.PRIVATE)
public class UserCreationRequest {
    @NotBlank(message = "Họ tên không được để trống.")
    String fullName;

    @Email(message = "Email không đúng định dạng.")
    String email;

    @NotBlank(message = "Mật khẩu không được trống.")
    @Size(min = 8, message = "Mật khẩu phải từ 8 kí tự")
    String password;

    @NotBlank(message = "Số điện thoại không được trống")
    String phoneNumber;

}
