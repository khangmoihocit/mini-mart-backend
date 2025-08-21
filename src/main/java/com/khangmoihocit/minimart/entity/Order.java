package com.khangmoihocit.minimart.entity;

import com.khangmoihocit.minimart.enums.OrderStatus;
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
@Table(name = "orders")
public class Order {
    @Id
    @GeneratedValue(strategy = GenerationType.UUID)
    String id;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "user_id")
    User user;

    @Column(name = "fullname", nullable = false, length = 100)
    String fullName;

    @Column(name = "email", nullable = false, length = 100)
    String email;

    @Column(name = "phone_number", nullable = false, length = 20)
    String phoneNumber;

    @Column(name = "shipping_address", nullable = false)
    String shippingAddress;

    @Column(name = "note")
    String note;

    @Enumerated(EnumType.STRING)
    @Column(name = "status")
    @Builder.Default
    OrderStatus status = OrderStatus.PENDING;

    @Column(name = "total_money", nullable = false, precision = 12, scale = 2)
    BigDecimal totalMoney;

    @Column(name = "shipping_method", length = 100)
    String shippingMethod;

    @Column(name = "payment_method", length = 100)
    String paymentMethod;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "coupon_id")
    Coupon coupon;

    @Column(name = "order_date")
    LocalDateTime orderDate;

    @PrePersist
    protected void onCreate() {
        orderDate = LocalDateTime.now();
    }

}
