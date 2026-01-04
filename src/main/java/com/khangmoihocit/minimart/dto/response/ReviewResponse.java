package com.khangmoihocit.minimart.dto.response;

import lombok.*;
import lombok.experimental.FieldDefaults;

import java.time.LocalDateTime;

@Data
@NoArgsConstructor
@AllArgsConstructor
@Builder
@FieldDefaults(level = AccessLevel.PRIVATE)
public class ReviewResponse {
    String id;
    String userId;
    String userName;
    String userEmail;
    String productId;
    String productName;
    Integer rating;
    String comment;
    LocalDateTime createdAt;
}

