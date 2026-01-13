package com.khangmoihocit.minimart.dto.response;

import lombok.*;
import lombok.experimental.FieldDefaults;

import java.util.Map;

@Data
@NoArgsConstructor
@AllArgsConstructor
@Builder
@FieldDefaults(level = AccessLevel.PRIVATE)
public class ProductRatingResponse {
    String productId;
    String productName;
    Double averageRating;
    Long totalReviews;
    Map<Integer, Long> ratingDistribution; // Map từ số sao (1-5) đến số lượng review
}

