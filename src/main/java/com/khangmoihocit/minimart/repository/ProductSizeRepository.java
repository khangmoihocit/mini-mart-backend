package com.khangmoihocit.minimart.repository;

import com.khangmoihocit.minimart.entity.ProductSize;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Modifying;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;

public interface ProductSizeRepository extends JpaRepository<ProductSize, String> {
    List<ProductSize> findByProductId(String productId);
    List<ProductSize> findByProductIdIn(List<String> productIds);

    @Modifying
    @Transactional
    void deleteByProductId(String productId);
}

