package com.khangmoihocit.minimart.controller;

import com.khangmoihocit.minimart.dto.request.ReviewRequest;
import com.khangmoihocit.minimart.dto.request.UpdateReviewRequest;
import com.khangmoihocit.minimart.dto.response.ApiResponse;
import com.khangmoihocit.minimart.dto.response.ProductRatingResponse;
import com.khangmoihocit.minimart.dto.response.ReviewResponse;
import com.khangmoihocit.minimart.service.ReviewService;
import jakarta.validation.Valid;
import lombok.AccessLevel;
import lombok.RequiredArgsConstructor;
import lombok.experimental.FieldDefaults;
import lombok.extern.slf4j.Slf4j;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@Slf4j
@RequiredArgsConstructor
@RestController
@FieldDefaults(level = AccessLevel.PRIVATE, makeFinal = true)
@RequestMapping("${api.prefix}/reviews")
public class ReviewController {
    ReviewService reviewService;

    @PostMapping
    @PreAuthorize("hasAnyRole('USER', 'ADMIN')")
    public ApiResponse<ReviewResponse> createReview(@Valid @RequestBody ReviewRequest request) {
        ReviewResponse result = reviewService.createReview(request);
        return ApiResponse.<ReviewResponse>builder()
                .result(result)
                .message("Tạo đánh giá thành công!")
                .build();
    }

    @PutMapping("/{reviewId}")
    @PreAuthorize("hasAnyRole('USER', 'ADMIN')")
    public ApiResponse<ReviewResponse> updateReview(
            @PathVariable String reviewId,
            @Valid @RequestBody UpdateReviewRequest request) {
        ReviewResponse result = reviewService.updateReview(reviewId, request);
        return ApiResponse.<ReviewResponse>builder()
                .result(result)
                .message("Cập nhật đánh giá thành công!")
                .build();
    }

    @DeleteMapping("/{reviewId}")
    @PreAuthorize("hasAnyRole('USER', 'ADMIN')")
    public ApiResponse<Void> deleteReview(@PathVariable String reviewId) {
        reviewService.deleteReview(reviewId);
        return ApiResponse.<Void>builder()
                .message("Xóa đánh giá thành công!")
                .build();
    }

    @GetMapping("/{reviewId}")
    public ApiResponse<ReviewResponse> getReviewById(@PathVariable String reviewId) {
        ReviewResponse result = reviewService.getReviewById(reviewId);
        return ApiResponse.<ReviewResponse>builder()
                .result(result)
                .build();
    }

    @GetMapping("/product/{productId}")
    public ApiResponse<List<ReviewResponse>> getReviewsByProductId(@PathVariable String productId) {
        List<ReviewResponse> result = reviewService.getReviewsByProductId(productId);
        return ApiResponse.<List<ReviewResponse>>builder()
                .result(result)
                .build();
    }

    @GetMapping("/my-reviews")
    @PreAuthorize("hasAnyRole('USER', 'ADMIN')")
    public ApiResponse<List<ReviewResponse>> getMyReviews() {
        List<ReviewResponse> result = reviewService.getMyReviews();
        return ApiResponse.<List<ReviewResponse>>builder()
                .result(result)
                .build();
    }

    @GetMapping("/product/{productId}/rating")
    public ApiResponse<ProductRatingResponse> getProductRating(@PathVariable String productId) {
        ProductRatingResponse result = reviewService.getProductRating(productId);
        return ApiResponse.<ProductRatingResponse>builder()
                .result(result)
                .build();
    }

    @GetMapping("/all")
    @PreAuthorize("hasRole('ADMIN')")
    public ApiResponse<List<ReviewResponse>> getAllReviews() {
        List<ReviewResponse> result = reviewService.getAllReviews();
        return ApiResponse.<List<ReviewResponse>>builder()
                .result(result)
                .build();
    }
}

