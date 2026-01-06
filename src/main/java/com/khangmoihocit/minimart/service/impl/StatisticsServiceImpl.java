package com.khangmoihocit.minimart.service.impl;

import com.khangmoihocit.minimart.dto.response.*;
import com.khangmoihocit.minimart.entity.Order;
import com.khangmoihocit.minimart.enums.OrderStatus;
import com.khangmoihocit.minimart.repository.OrderDetailRepository;
import com.khangmoihocit.minimart.repository.OrderRepository;
import com.khangmoihocit.minimart.repository.ProductRepository;
import com.khangmoihocit.minimart.service.StatisticsService;
import lombok.AccessLevel;
import lombok.RequiredArgsConstructor;
import lombok.experimental.FieldDefaults;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;

import java.math.BigDecimal;
import java.time.LocalDate;
import java.time.LocalDateTime;
import java.time.LocalTime;
import java.util.ArrayList;
import java.util.Comparator;
import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;

@Service
@RequiredArgsConstructor
@FieldDefaults(level = AccessLevel.PRIVATE, makeFinal = true)
@Slf4j
public class StatisticsServiceImpl implements StatisticsService {

    ProductRepository productRepository;
    OrderRepository orderRepository;
    OrderDetailRepository orderDetailRepository;

    @Override
    public ProductStatisticsResponse getProductStatistics() {
        Long totalProducts = productRepository.count();
        Long outOfStockProducts = productRepository.countOutOfStockProducts();
        Long lowStockProducts = productRepository.countLowStockProducts();
        BigDecimal totalInventoryValue = productRepository.calculateTotalInventoryValue();
        BigDecimal averagePrice = productRepository.calculateAveragePrice();

        return ProductStatisticsResponse.builder()
                .totalProducts(totalProducts)
                .outOfStockProducts(outOfStockProducts != null ? outOfStockProducts : 0L)
                .lowStockProducts(lowStockProducts != null ? lowStockProducts : 0L)
                .totalInventoryValue(totalInventoryValue != null ? totalInventoryValue : BigDecimal.ZERO)
                .averagePrice(averagePrice != null ? averagePrice : BigDecimal.ZERO)
                .build();
    }

    @Override
    public OrderStatisticsResponse getOrderStatistics() {
        Long totalOrders = orderRepository.count();
        Long pendingOrders = orderRepository.countByStatus(OrderStatus.PENDING);
        Long processingOrders = orderRepository.countByStatus(OrderStatus.PROCESSING);
        Long shippedOrders = orderRepository.countByStatus(OrderStatus.SHIPPED);
        Long deliveredOrders = orderRepository.countByStatus(OrderStatus.DELIVERED);
        Long cancelledOrders = orderRepository.countByStatus(OrderStatus.CANCELLED);

        BigDecimal totalRevenue = orderRepository.sumTotalMoneyByStatus(OrderStatus.DELIVERED);
        BigDecimal pendingRevenue = orderRepository.sumTotalMoneyByStatus(OrderStatus.PENDING);
        BigDecimal averageOrderValue = orderRepository.calculateAverageOrderValue();

        return OrderStatisticsResponse.builder()
                .totalOrders(totalOrders)
                .pendingOrders(pendingOrders != null ? pendingOrders : 0L)
                .processingOrders(processingOrders != null ? processingOrders : 0L)
                .shippedOrders(shippedOrders != null ? shippedOrders : 0L)
                .deliveredOrders(deliveredOrders != null ? deliveredOrders : 0L)
                .cancelledOrders(cancelledOrders != null ? cancelledOrders : 0L)
                .totalRevenue(totalRevenue != null ? totalRevenue : BigDecimal.ZERO)
                .pendingRevenue(pendingRevenue != null ? pendingRevenue : BigDecimal.ZERO)
                .averageOrderValue(averageOrderValue != null ? averageOrderValue : BigDecimal.ZERO)
                .build();
    }

    @Override
    public List<TopProductResponse> getTopSellingProducts(int limit) {
        List<Object[]> results = orderDetailRepository.findTopSellingProducts(limit);
        List<TopProductResponse> topProducts = new ArrayList<>();

        for (Object[] result : results) {
            TopProductResponse product = TopProductResponse.builder()
                    .productId((String) result[0])
                    .productName((String) result[1])
                    .totalSold(((Number) result[2]).longValue())
                    .totalRevenue((BigDecimal) result[3])
                    .build();
            topProducts.add(product);
        }

        return topProducts;
    }

    @Override
    public List<TopProductResponse> getTopSellingProductsByDateRange(LocalDate startDate, LocalDate endDate, int limit) {
        LocalDateTime startDateTime = startDate.atStartOfDay();
        LocalDateTime endDateTime = endDate.atTime(LocalTime.MAX);

        List<Object[]> results = orderDetailRepository.findTopSellingProductsByDateRange(startDateTime, endDateTime, limit);
        List<TopProductResponse> topProducts = new ArrayList<>();

        for (Object[] result : results) {
            TopProductResponse product = TopProductResponse.builder()
                    .productId((String) result[0])
                    .productName((String) result[1])
                    .totalSold(((Number) result[2]).longValue())
                    .totalRevenue((BigDecimal) result[3])
                    .build();
            topProducts.add(product);
        }

        return topProducts;
    }

    @Override
    public List<RevenueByDateResponse> getRevenueByDateRange(LocalDate startDate, LocalDate endDate) {
        LocalDateTime startDateTime = startDate.atStartOfDay();
        LocalDateTime endDateTime = endDate.atTime(LocalTime.MAX);

        List<Order> orders = orderRepository.findOrdersByDateRangeAndStatus(startDateTime, endDateTime, OrderStatus.DELIVERED);

        // Group orders by date
        Map<LocalDate, List<Order>> ordersByDate = orders.stream()
                .collect(Collectors.groupingBy(order -> order.getOrderDate().toLocalDate()));

        // Calculate revenue for each date
        List<RevenueByDateResponse> revenueList = new ArrayList<>();
        for (Map.Entry<LocalDate, List<Order>> entry : ordersByDate.entrySet()) {
            LocalDate date = entry.getKey();
            List<Order> dailyOrders = entry.getValue();

            BigDecimal dailyRevenue = dailyOrders.stream()
                    .map(Order::getTotalMoney)
                    .reduce(BigDecimal.ZERO, BigDecimal::add);

            RevenueByDateResponse revenue = RevenueByDateResponse.builder()
                    .date(date)
                    .orderCount((long) dailyOrders.size())
                    .totalRevenue(dailyRevenue)
                    .build();
            revenueList.add(revenue);
        }

        // Sort by date
        revenueList.sort(Comparator.comparing(RevenueByDateResponse::getDate));

        return revenueList;
    }

    @Override
    public List<CategoryStatisticsResponse> getCategoryStatistics() {
        List<Object[]> results = orderDetailRepository.findCategoryStatistics();
        List<CategoryStatisticsResponse> categoryStats = new ArrayList<>();

        for (Object[] result : results) {
            CategoryStatisticsResponse stats = CategoryStatisticsResponse.builder()
                    .categoryId((String) result[0])
                    .categoryName((String) result[1])
                    .productCount(((Number) result[2]).longValue())
                    .totalSold(((Number) result[3]).longValue())
                    .totalRevenue((BigDecimal) result[4])
                    .build();
            categoryStats.add(stats);
        }

        return categoryStats;
    }
}

