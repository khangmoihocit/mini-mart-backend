package com.khangmoihocit.minimart.dto.request;

import com.khangmoihocit.minimart.entity.Role;
import jakarta.validation.constraints.Past;
import lombok.*;
import lombok.experimental.FieldDefaults;

import java.time.LocalDate;
import java.time.LocalDateTime;

@Data
@AllArgsConstructor
@NoArgsConstructor
@Builder
@FieldDefaults(level = AccessLevel.PRIVATE)
public class UserUpdateInfoRequest {
    String fullName;
    String email;
    String phoneNumber;
    String address;
    Boolean isActive;

    @Past(message = "Ngày sinh không được lớn hơn ngày hôm nay")
    LocalDate dateOfBirth;

    Role role;
}
