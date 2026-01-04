package com.khangmoihocit.minimart.repository;


import com.khangmoihocit.minimart.entity.Product;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.EntityGraph;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.JpaSpecificationExecutor;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;

import java.util.List;

public interface ProductRepository extends JpaRepository<Product, String>, JpaSpecificationExecutor<Product> {
    @Query("SELECT p FROM Product p JOIN FETCH p.category")
    List<Product> findAllWithCategory();

    @Query(value = "SELECT * FROM products p WHERE " +
            "(:keyword IS NULL OR :keyword = '' OR " +
            "p.name LIKE CONCAT('%', :keyword, '%') OR " +
            "p.description LIKE CONCAT('%', :keyword, '%'))",

            countQuery = "SELECT count(*) FROM products p WHERE " +
                    "(:keyword IS NULL OR :keyword = '' OR " +
                    "p.name LIKE CONCAT('%', :keyword, '%') OR " +
                    "p.description LIKE CONCAT('%', :keyword, '%'))",
            nativeQuery = true)
    Page<Product> searchByKeyword(@Param("keyword") String keyword, Pageable pageable);

    @Query(value = "SELECT * FROM products p WHERE p.category_id = :categoryId LIMIT 10", nativeQuery = true)
    List<Product> findByCategoryId(@Param("categoryId") String categoryId);
}
