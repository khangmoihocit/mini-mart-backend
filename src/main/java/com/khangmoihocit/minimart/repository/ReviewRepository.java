package com.khangmoihocit.minimart.repository;
import java.util.Optional;
import java.util.List;

import org.springframework.stereotype.Repository;
import org.springframework.data.repository.query.Param;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.jpa.repository.JpaRepository;
import com.khangmoihocit.minimart.entity.Review;
@Repository
public interface ReviewRepository extends JpaRepository<Review, String> {

    boolean existsByUserIdAndProductId(String userId, String productId);

    List<Review> findByProductIdAndRating(String productId, Integer rating);

    @Query("SELECT COUNT(r) FROM Review r WHERE r.product.id = :productId")
    Long countByProductId(@Param("productId") String productId);

    @Query("SELECT AVG(r.rating) FROM Review r WHERE r.product.id = :productId")
    Double getAverageRatingByProductId(@Param("productId") String productId);

    Optional<Review> findByUserIdAndProductId(String userId, String productId);

    List<Review> findByUserId(String userId);

    List<Review> findByProductId(String productId);


}


