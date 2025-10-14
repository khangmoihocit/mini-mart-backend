package com.khangmoihocit.minimart.entity;

import jakarta.persistence.*;
import lombok.*;
import lombok.experimental.FieldDefaults;

import java.time.LocalDate;
import java.time.LocalDateTime;
import java.util.HashSet;
import java.util.Set;

@AllArgsConstructor
@NoArgsConstructor
@Getter
@Setter
@Builder
@FieldDefaults(level = AccessLevel.PRIVATE)
@Entity
@Table(name = "users")
public class User {
    @Id
    @GeneratedValue(strategy = GenerationType.UUID)
    String id;

    @Column(name = "fullname", nullable = false, length = 100)
    String fullName;

    @Column(name = "email", nullable = false, unique = true, length = 100)
    String email;

    @Column(name="phone_number", nullable = false, length = 20)
    String phoneNumber;

    @Column(name="address", length = 255)
    String address;

    @Column(name="password", nullable = false, length = 255)
    String password;

    @Column(name = "is_active")
    @Builder.Default
    Boolean isActive = true;

    @Column(name = "date_of_birth")
    LocalDate dateOfBirth;

    @ManyToOne(fetch = FetchType.EAGER, targetEntity = Role.class)
    @JoinColumn(name = "role_id")
    Role role;

    @Column(name = "created_at")
    LocalDateTime createdAt;

    @Column(name = "updated_at")
    LocalDateTime updatedAt;


    /* @PrePersist và @PreUpdate là các annotation của JPA (Java Persistence API)
        dùng để tự động thực thi một phương thức ngay trước khi một đối tượng (entity) được lưu
        hoặc cập nhật xuống cơ sở dữ liệu.
        Chúng được gọi là "Lifecycle Callbacks".*/
    // Sẽ chạy KHI VÀ CHỈ KHI bạn tạo mới một bản ghi
    @PrePersist
    protected void onCreate() {
        createdAt = LocalDateTime.now();
        updatedAt = LocalDateTime.now();
    }

    // Sẽ chạy MỖI KHI cập nhật một bản ghi đã tồn tại
    @PreUpdate
    protected void onUpdate() {
        updatedAt = LocalDateTime.now();
    }
}
