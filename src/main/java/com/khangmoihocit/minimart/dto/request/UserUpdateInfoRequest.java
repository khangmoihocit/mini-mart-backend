package com.khangmoihocit.minimart.dto.request;

import com.khangmoihocit.minimart.entity.Role;
import jakarta.validation.constraints.Email;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.Past;
import lombok.*;
import lombok.experimental.FieldDefaults;

import java.time.LocalDate;
import java.time.LocalDateTime;
import java.util.List;
import java.util.Set;

@Data
@AllArgsConstructor
@NoArgsConstructor
@Builder
@FieldDefaults(level = AccessLevel.PRIVATE)
public class UserUpdateInfoRequest {
    @NotBlank(message = "Họ tên không được để trống.")
    String fullName;

    @Email(message = "Email không đúng định dạng.")
    String email;

    @NotBlank(message = "Số điện thoại không được trống")
    String phoneNumber;
    String address;
    Boolean isActive;

    @Past(message = "Ngày sinh không được lớn hơn ngày hôm nay")
    LocalDate dateOfBirth;

    String roleName;
}
