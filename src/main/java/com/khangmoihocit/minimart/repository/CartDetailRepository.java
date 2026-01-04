package com.khangmoihocit.minimart.repository;

import com.khangmoihocit.minimart.entity.CartDetail;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Modifying;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.Optional;

@Repository
public interface CartDetailRepository extends JpaRepository<CartDetail, String> {
    @Query("SELECT cd FROM CartDetail cd JOIN FETCH cd.product p LEFT JOIN FETCH p.category LEFT JOIN FETCH cd.productSize WHERE cd.cart.id = :cartId")
    List<CartDetail> findByCartId(@Param("cartId") String cartId);

    @Query("SELECT cd FROM CartDetail cd WHERE cd.cart.id = :cartId AND cd.product.id = :productId AND " +
           "((:productSizeId IS NULL AND cd.productSize IS NULL) OR cd.productSize.id = :productSizeId)")
    Optional<CartDetail> findByCartIdAndProductIdAndSize(
            @Param("cartId") String cartId,
            @Param("productId") String productId,
            @Param("productSizeId") String productSizeId);

    @Modifying
    @Query("DELETE FROM CartDetail cd WHERE cd.cart.id = :cartId")
    void deleteByCartId(@Param("cartId") String cartId);

    @Query("SELECT COUNT(cd) FROM CartDetail cd WHERE cd.cart.id = :cartId")
    int countByCartId(@Param("cartId") String cartId);
}

