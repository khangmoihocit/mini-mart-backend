package com.khangmoihocit.minimart.repository;

import com.khangmoihocit.minimart.entity.Order;
import com.khangmoihocit.minimart.enums.OrderStatus;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import java.math.BigDecimal;
import java.time.LocalDateTime;
import java.util.List;

@Repository
public interface OrderRepository extends JpaRepository<Order, String> {
    Page<Order> findAllByOrderByOrderDateDesc(Pageable pageable);
    Page<Order> findByUserIdOrderByOrderDateDesc(String userId, Pageable pageable);
    Page<Order> findByStatusOrderByOrderDateDesc(OrderStatus status, Pageable pageable);
    List<Order> findByUserIdOrderByOrderDateDesc(String userId);

    // Statistics queries
    @Query("SELECT COUNT(o) FROM Order o WHERE o.status = :status")
    Long countByStatus(@Param("status") OrderStatus status);

    @Query("SELECT SUM(o.totalMoney) FROM Order o WHERE o.status = :status")
    BigDecimal sumTotalMoneyByStatus(@Param("status") OrderStatus status);

    @Query("SELECT AVG(o.totalMoney) FROM Order o")
    BigDecimal calculateAverageOrderValue();

    @Query("SELECT o FROM Order o WHERE o.orderDate BETWEEN :startDate AND :endDate ORDER BY o.orderDate DESC")
    List<Order> findOrdersByDateRange(@Param("startDate") LocalDateTime startDate, @Param("endDate") LocalDateTime endDate);

    @Query("SELECT o FROM Order o WHERE o.orderDate BETWEEN :startDate AND :endDate AND o.status = :status")
    List<Order> findOrdersByDateRangeAndStatus(@Param("startDate") LocalDateTime startDate,
                                                @Param("endDate") LocalDateTime endDate,
                                                @Param("status") OrderStatus status);
}

