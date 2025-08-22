package com.khangmoihocit.minimart.entity;

import jakarta.persistence.*;
import lombok.*;
import lombok.experimental.FieldDefaults;
import java.time.LocalDateTime;

@AllArgsConstructor
@NoArgsConstructor
@Getter
@Setter
@Builder
@FieldDefaults(level = AccessLevel.PRIVATE)
@Entity
@Table(name = "tokens")
public class Token {
    @Id
    @GeneratedValue(strategy = GenerationType.UUID)
    String id;

    @Column(name = "token", nullable = false, columnDefinition = "TEXT")
    String token;

    @Column(name = "token_type", nullable = false, length = 50)
    String tokenType;

    @Column(name = "expiration_time", nullable = false)
    LocalDateTime expirationTime;

    @Column(name = "revoked")
    @Builder.Default
    Boolean revoked = false; //đã thu hồi

    @Column(name = "expired")
    @Builder.Default
    Boolean expired = false;

    @ManyToOne
    @JoinColumn(name = "user_id", nullable = false)
    User user;
}
