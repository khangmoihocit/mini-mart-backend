package com.khangmoihocit.minimart.repository;

import com.khangmoihocit.minimart.entity.ProductImage;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import java.util.List;

@Repository
public interface ProductImageRepository extends JpaRepository<ProductImage, String> {
    List<ProductImage> findByProductIdIn(List<String> productIds);
    List<ProductImage> findByProductId(String productId);

    @Query("SELECT pm FROM ProductImage pm WHERE pm.product.id = :productId AND pm.id NOT IN :ids")
    List<ProductImage> findByProductIdAndIdNotIn(@Param("productId") String productId,
                                                 @Param("ids") List<String> ids);
}
