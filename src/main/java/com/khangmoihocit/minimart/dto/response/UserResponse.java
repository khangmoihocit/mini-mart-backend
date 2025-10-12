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
import java.util.Set;

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
    RoleResponse role;
    LocalDateTime createdAt;
    LocalDateTime updatedAt;
}
