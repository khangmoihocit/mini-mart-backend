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
@Table(name = "product_sizes")
public class ProductSize {
    @Id
    @GeneratedValue(strategy = GenerationType.UUID)
    String id;

    @Column(name = "size_name", nullable = false)
    String sizeName;

    @Column(name = "quantity", nullable = false)
    @Builder.Default
    Integer quantity = 0;

    @ManyToOne
    @JoinColumn(name = "product_id", nullable = false)
    Product product;
}

