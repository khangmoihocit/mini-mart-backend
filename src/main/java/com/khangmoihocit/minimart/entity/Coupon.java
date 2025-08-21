package com.khangmoihocit.minimart.entity;

import jakarta.persistence.*;
import lombok.*;
import lombok.experimental.FieldDefaults;
import java.math.BigDecimal;
import java.time.LocalDateTime;

@AllArgsConstructor
@NoArgsConstructor
@Getter
@Setter
@Builder
@FieldDefaults(level = AccessLevel.PRIVATE)
@Entity
@Table(name = "coupons")
public class Coupon {
    @Id
    @GeneratedValue(strategy = GenerationType.UUID)
    String id;

    @Column(name = "code", nullable = false, unique = true, length = 50)
    String code;

    @Column(name = "is_active")
    @Builder.Default
    Boolean isActive = true;

    @Column(name = "discount_amount", nullable = false, precision = 12, scale = 2)
    BigDecimal discountAmount;

    @Column(name = "expiry_date", nullable = false)
    LocalDateTime expiryDate;
}
