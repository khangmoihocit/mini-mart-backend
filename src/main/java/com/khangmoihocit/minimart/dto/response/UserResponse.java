package com.khangmoihocit.minimart.dto.response;

import com.khangmoihocit.minimart.entity.Role;
import jakarta.persistence.Column;
import jakarta.persistence.FetchType;
import jakarta.persistence.JoinColumn;
import jakarta.persistence.ManyToOne;
import lombok.*;
import lombok.experimental.FieldDefaults;

import java.time.LocalDate;
import java.time.LocalDateTime;

@Data
@AllArgsConstructor
@NoArgsConstructor
@Builder
@FieldDefaults(level = AccessLevel.PRIVATE)
public class UserResponse {
    String id;
    String fullName;
    String email;
    String phoneNumber;
    String address;
    Boolean isActive;
    LocalDate dateOfBirth;
    Role role;
    LocalDateTime createdAt;
    LocalDateTime updatedAt;
}
