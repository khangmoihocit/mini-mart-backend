package com.khangmoihocit.minimart.repository;

import com.khangmoihocit.minimart.entity.ProductSize;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.List;

public interface ProductSizeRepository extends JpaRepository<ProductSize, String> {
    List<ProductSize> findByProductId(String productId);
    List<ProductSize> findByProductIdIn(List<String> productIds);
    void deleteByProductId(String productId);
}

