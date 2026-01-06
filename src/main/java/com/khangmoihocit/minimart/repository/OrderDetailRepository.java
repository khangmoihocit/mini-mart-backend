package com.khangmoihocit.minimart.repository;

import com.khangmoihocit.minimart.entity.OrderDetail;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import java.time.LocalDateTime;
import java.util.List;

@Repository
public interface OrderDetailRepository extends JpaRepository<OrderDetail, String> {
    List<OrderDetail> findByOrderId(String orderId);

    // Statistics queries for top selling products
    @Query(value = "SELECT od.product_id as productId, p.name as productName, " +
            "SUM(od.number_of_products) as totalSold, " +
            "SUM(od.total_money) as totalRevenue " +
            "FROM order_details od " +
            "JOIN products p ON od.product_id = p.id " +
            "JOIN orders o ON od.order_id = o.id " +
            "WHERE o.status = 'DELIVERED' " +
            "GROUP BY od.product_id, p.name " +
            "ORDER BY totalSold DESC " +
            "LIMIT :limit", nativeQuery = true)
    List<Object[]> findTopSellingProducts(@Param("limit") int limit);

    // Statistics queries for category performance
    @Query(value = "SELECT c.id as categoryId, c.name as categoryName, " +
            "COUNT(DISTINCT p.id) as productCount, " +
            "COALESCE(SUM(od.number_of_products), 0) as totalSold, " +
            "COALESCE(SUM(od.total_money), 0) as totalRevenue " +
            "FROM categories c " +
            "LEFT JOIN products p ON c.id = p.category_id " +
            "LEFT JOIN order_details od ON p.id = od.product_id " +
            "LEFT JOIN orders o ON od.order_id = o.id AND o.status = 'DELIVERED' " +
            "GROUP BY c.id, c.name " +
            "ORDER BY totalRevenue DESC", nativeQuery = true)
    List<Object[]> findCategoryStatistics();

    // Get top selling products by date range
    @Query(value = "SELECT od.product_id as productId, p.name as productName, " +
            "SUM(od.number_of_products) as totalSold, " +
            "SUM(od.total_money) as totalRevenue " +
            "FROM order_details od " +
            "JOIN products p ON od.product_id = p.id " +
            "JOIN orders o ON od.order_id = o.id " +
            "WHERE o.status = 'DELIVERED' " +
            "AND o.order_date BETWEEN :startDate AND :endDate " +
            "GROUP BY od.product_id, p.name " +
            "ORDER BY totalSold DESC " +
            "LIMIT :limit", nativeQuery = true)
    List<Object[]> findTopSellingProductsByDateRange(@Param("startDate") LocalDateTime startDate,
                                                      @Param("endDate") LocalDateTime endDate,
                                                      @Param("limit") int limit);
}

