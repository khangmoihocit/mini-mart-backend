package com.khangmoihocit.minimart.controller;

import com.khangmoihocit.minimart.dto.response.*;
import com.khangmoihocit.minimart.service.StatisticsService;
import lombok.AccessLevel;
import lombok.RequiredArgsConstructor;
import lombok.experimental.FieldDefaults;
import lombok.extern.slf4j.Slf4j;
import org.springframework.format.annotation.DateTimeFormat;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.*;

import java.time.LocalDate;
import java.util.List;

@Slf4j
@RestController
@RequestMapping("/api/v1/statistics")
@RequiredArgsConstructor
@FieldDefaults(level = AccessLevel.PRIVATE, makeFinal = true)
public class StatisticsController {

    StatisticsService statisticsService;

    /**
     * Lấy thống kê tổng quan về sản phẩm
     * GET /api/v1/statistics/products
     */
    @GetMapping("/products")
    @PreAuthorize("hasRole('ADMIN')")
    public ResponseEntity<ApiResponse<ProductStatisticsResponse>> getProductStatistics() {
        ProductStatisticsResponse statistics = statisticsService.getProductStatistics();
        return ResponseEntity.ok(ApiResponse.<ProductStatisticsResponse>builder()
                .message("Lấy thống kê sản phẩm thành công!")
                .result(statistics)
                .build());
    }

    /**
     * Lấy thống kê tổng quan về đơn hàng
     * GET /api/v1/statistics/orders
     */
    @GetMapping("/orders")
    @PreAuthorize("hasRole('ADMIN')")
    public ResponseEntity<ApiResponse<OrderStatisticsResponse>> getOrderStatistics() {
        OrderStatisticsResponse statistics = statisticsService.getOrderStatistics();
        return ResponseEntity.ok(ApiResponse.<OrderStatisticsResponse>builder()
                .message("Lấy thống kê đơn hàng thành công!")
                .result(statistics)
                .build());
    }

    /**
     * Lấy danh sách sản phẩm bán chạy nhất
     * GET /api/v1/statistics/top-products?limit=10
     */
    @GetMapping("/top-products")
    @PreAuthorize("hasRole('ADMIN')")
    public ResponseEntity<ApiResponse<List<TopProductResponse>>> getTopSellingProducts(
            @RequestParam(defaultValue = "10") int limit) {
        List<TopProductResponse> topProducts = statisticsService.getTopSellingProducts(limit);
        return ResponseEntity.ok(ApiResponse.<List<TopProductResponse>>builder()
                .message("Lấy danh sách sản phẩm bán chạy thành công!")
                .result(topProducts)
                .build());
    }

    /**
     * Lấy danh sách sản phẩm bán chạy nhất theo khoảng thời gian
     * GET /api/v1/statistics/top-products/by-date?startDate=2024-01-01&endDate=2024-12-31&limit=10
     */
    @GetMapping("/top-products/by-date")
    @PreAuthorize("hasRole('ADMIN')")
    public ResponseEntity<ApiResponse<List<TopProductResponse>>> getTopSellingProductsByDateRange(
            @RequestParam @DateTimeFormat(iso = DateTimeFormat.ISO.DATE) LocalDate startDate,
            @RequestParam @DateTimeFormat(iso = DateTimeFormat.ISO.DATE) LocalDate endDate,
            @RequestParam(defaultValue = "10") int limit) {
        List<TopProductResponse> topProducts = statisticsService.getTopSellingProductsByDateRange(startDate, endDate, limit);
        return ResponseEntity.ok(ApiResponse.<List<TopProductResponse>>builder()
                .message("Lấy danh sách sản phẩm bán chạy theo thời gian thành công!")
                .result(topProducts)
                .build());
    }

    /**
     * Lấy doanh thu theo ngày trong khoảng thời gian
     * GET /api/v1/statistics/revenue/by-date?startDate=2024-01-01&endDate=2024-12-31
     */
    @GetMapping("/revenue/by-date")
    @PreAuthorize("hasRole('ADMIN')")
    public ResponseEntity<ApiResponse<List<RevenueByDateResponse>>> getRevenueByDateRange(
            @RequestParam @DateTimeFormat(iso = DateTimeFormat.ISO.DATE) LocalDate startDate,
            @RequestParam @DateTimeFormat(iso = DateTimeFormat.ISO.DATE) LocalDate endDate) {
        List<RevenueByDateResponse> revenue = statisticsService.getRevenueByDateRange(startDate, endDate);
        return ResponseEntity.ok(ApiResponse.<List<RevenueByDateResponse>>builder()
                .message("Lấy thống kê doanh thu theo ngày thành công!")
                .result(revenue)
                .build());
    }

    /**
     * Lấy thống kê theo danh mục
     * GET /api/v1/statistics/categories
     */
    @GetMapping("/categories")
    @PreAuthorize("hasRole('ADMIN')")
    public ResponseEntity<ApiResponse<List<CategoryStatisticsResponse>>> getCategoryStatistics() {
        List<CategoryStatisticsResponse> statistics = statisticsService.getCategoryStatistics();
        return ResponseEntity.ok(ApiResponse.<List<CategoryStatisticsResponse>>builder()
                .message("Lấy thống kê theo danh mục thành công!")
                .result(statistics)
                .build());
    }
}

