package com.khangmoihocit.minimart.repository;

import com.khangmoihocit.minimart.entity.Order;
import com.khangmoihocit.minimart.enums.OrderStatus;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.List;

@Repository
public interface OrderRepository extends JpaRepository<Order, String> {
    Page<Order> findAllByOrderByOrderDateDesc(Pageable pageable);
    Page<Order> findByUserIdOrderByOrderDateDesc(String userId, Pageable pageable);
    Page<Order> findByStatusOrderByOrderDateDesc(OrderStatus status, Pageable pageable);
    List<Order> findByUserIdOrderByOrderDateDesc(String userId);
}


