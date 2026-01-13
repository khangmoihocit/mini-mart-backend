package com.khangmoihocit.minimart.service;

import com.khangmoihocit.minimart.dto.request.ReviewRequest;
import com.khangmoihocit.minimart.dto.request.UpdateReviewRequest;
import com.khangmoihocit.minimart.dto.response.ProductRatingResponse;
import com.khangmoihocit.minimart.dto.response.ReviewResponse;
import com.khangmoihocit.minimart.entity.Product;
import com.khangmoihocit.minimart.entity.Review;
import com.khangmoihocit.minimart.entity.User;
import com.khangmoihocit.minimart.enums.ErrorCode;
import com.khangmoihocit.minimart.exception.AppException;
import com.khangmoihocit.minimart.repository.ProductRepository;
import com.khangmoihocit.minimart.repository.ReviewRepository;
import com.khangmoihocit.minimart.repository.UserRepository;
import lombok.AccessLevel;
import lombok.RequiredArgsConstructor;
import lombok.experimental.FieldDefaults;
import lombok.extern.slf4j.Slf4j;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;

@Service
@RequiredArgsConstructor
@FieldDefaults(level = AccessLevel.PRIVATE, makeFinal = true)
@Slf4j
public class ReviewService {
    ReviewRepository reviewRepository;
    ProductRepository productRepository;
    UserRepository userRepository;

    @Transactional
    public ReviewResponse createReview(ReviewRequest request) {
        // Lấy thông tin user hiện tại
        Authentication authentication = SecurityContextHolder.getContext().getAuthentication();
        String currentUserEmail = authentication.getName();

        User user = userRepository.findByEmail(currentUserEmail)
                .orElseThrow(() -> new AppException(ErrorCode.USER_NOT_EXISTED));

        // Kiểm tra sản phẩm có tồn tại
        Product product = productRepository.findById(request.getProductId())
                .orElseThrow(() -> new AppException(ErrorCode.PRODUCT_NOT_FOUND));

        // Kiểm tra user đã review sản phẩm này chưa
        if (reviewRepository.existsByUserIdAndProductId(user.getId(), request.getProductId())) {
            throw new AppException(ErrorCode.REVIEW_ALREADY_EXISTS);
        }

        // Tạo review mới
        Review review = Review.builder()
                .user(user)
                .product(product)
                .rating(request.getRating())
                .comment(request.getComment())
                .build();

        review = reviewRepository.save(review);

        return mapToReviewResponse(review);
    }

    @Transactional
    public ReviewResponse updateReview(String reviewId, UpdateReviewRequest request) {
        // Lấy thông tin user hiện tại
        Authentication authentication = SecurityContextHolder.getContext().getAuthentication();
        String currentUserEmail = authentication.getName();

        User user = userRepository.findByEmail(currentUserEmail)
                .orElseThrow(() -> new AppException(ErrorCode.USER_NOT_EXISTED));

        // Tìm review
        Review review = reviewRepository.findById(reviewId)
                .orElseThrow(() -> new AppException(ErrorCode.REVIEW_NOT_FOUND));

        // Kiểm tra quyền sở hữu
        if (!review.getUser().getId().equals(user.getId())) {
            throw new AppException(ErrorCode.UNAUTHORIZED);
        }

        // Cập nhật review
        review.setRating(request.getRating());
        review.setComment(request.getComment());

        review = reviewRepository.save(review);

        return mapToReviewResponse(review);
    }

    @Transactional
    public void deleteReview(String reviewId) {
        // Lấy thông tin user hiện tại
        Authentication authentication = SecurityContextHolder.getContext().getAuthentication();
        String currentUserEmail = authentication.getName();

        User user = userRepository.findByEmail(currentUserEmail)
                .orElseThrow(() -> new AppException(ErrorCode.USER_NOT_EXISTED));

        // Tìm review
        Review review = reviewRepository.findById(reviewId)
                .orElseThrow(() -> new AppException(ErrorCode.REVIEW_NOT_FOUND));

        // Kiểm tra quyền sở hữu hoặc admin
        if (!review.getUser().getId().equals(user.getId()) &&
            !user.getRole().getName().equals("ADMIN")) {
            throw new AppException(ErrorCode.UNAUTHORIZED);
        }

        reviewRepository.delete(review);
    }

    public ReviewResponse getReviewById(String reviewId) {
        Review review = reviewRepository.findById(reviewId)
                .orElseThrow(() -> new AppException(ErrorCode.REVIEW_NOT_FOUND));

        return mapToReviewResponse(review);
    }

    public List<ReviewResponse> getReviewsByProductId(String productId) {
        // Kiểm tra sản phẩm có tồn tại
        productRepository.findById(productId)
                .orElseThrow(() -> new AppException(ErrorCode.PRODUCT_NOT_FOUND));

        List<Review> reviews = reviewRepository.findByProductId(productId);

        return reviews.stream()
                .map(this::mapToReviewResponse)
                .collect(Collectors.toList());
    }

    public List<ReviewResponse> getMyReviews() {
        // Lấy thông tin user hiện tại
        Authentication authentication = SecurityContextHolder.getContext().getAuthentication();
        String currentUserEmail = authentication.getName();

        User user = userRepository.findByEmail(currentUserEmail)
                .orElseThrow(() -> new AppException(ErrorCode.USER_NOT_EXISTED));

        List<Review> reviews = reviewRepository.findByUserId(user.getId());

        return reviews.stream()
                .map(this::mapToReviewResponse)
                .collect(Collectors.toList());
    }

    public ProductRatingResponse getProductRating(String productId) {
        // Kiểm tra sản phẩm có tồn tại
        Product product = productRepository.findById(productId)
                .orElseThrow(() -> new AppException(ErrorCode.PRODUCT_NOT_FOUND));

        // Tính điểm đánh giá trung bình
        Double averageRating = reviewRepository.getAverageRatingByProductId(productId);
        if (averageRating == null) {
            averageRating = 0.0;
        }

        // Đếm tổng số review
        Long totalReviews = reviewRepository.countByProductId(productId);

        // Tính phân bố đánh giá theo số sao
        Map<Integer, Long> ratingDistribution = new HashMap<>();
        for (int i = 1; i <= 5; i++) {
            List<Review> reviewsByRating = reviewRepository.findByProductIdAndRating(productId, i);
            ratingDistribution.put(i, (long) reviewsByRating.size());
        }

        return ProductRatingResponse.builder()
                .productId(product.getId())
                .productName(product.getName())
                .averageRating(Math.round(averageRating * 10.0) / 10.0) // Làm tròn 1 chữ số thập phân
                .totalReviews(totalReviews)
                .ratingDistribution(ratingDistribution)
                .build();
    }

    public List<ReviewResponse> getAllReviews() {
        List<Review> reviews = reviewRepository.findAll();

        return reviews.stream()
                .map(this::mapToReviewResponse)
                .collect(Collectors.toList());
    }

    private ReviewResponse mapToReviewResponse(Review review) {
        return ReviewResponse.builder()
                .id(review.getId())
                .userId(review.getUser().getId())
                .userName(review.getUser().getFullName())
                .userEmail(review.getUser().getEmail())
                .productId(review.getProduct().getId())
                .productName(review.getProduct().getName())
                .rating(review.getRating())
                .comment(review.getComment())
                .createdAt(review.getCreatedAt())
                .build();
    }
}

