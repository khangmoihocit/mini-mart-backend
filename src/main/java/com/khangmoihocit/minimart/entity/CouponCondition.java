package com.khangmoihocit.minimart.entity;

import jakarta.persistence.*;
import lombok.*;
import lombok.experimental.FieldDefaults;

@AllArgsConstructor
@NoArgsConstructor
@Getter
@Setter
@Builder
@FieldDefaults(level = AccessLevel.PRIVATE)
@Entity
@Table(name = "coupon_conditions")
public class CouponCondition {
    @Id
    @GeneratedValue(strategy = GenerationType.UUID)
    String id;

    @ManyToOne
    @JoinColumn(name = "coupon_id", nullable = false)
    Coupon coupon;

    @Column(name = "attribute", nullable = false)
    String attribute;

    @Column(name = "operator", nullable = false, length = 10)
    String operator;

    @Column(name = "value", nullable = false)
    String value;
}
