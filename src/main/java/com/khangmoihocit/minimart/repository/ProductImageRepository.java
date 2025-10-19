package com.khangmoihocit.minimart.repository;

import com.khangmoihocit.minimart.entity.ProductImage;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.List;

@Repository
public interface ProductImageRepository extends JpaRepository<ProductImage, String> {
    List<ProductImage> findByProductIdIn(List<String> productIds);
    List<ProductImage> findByProductId(String productId);
}
