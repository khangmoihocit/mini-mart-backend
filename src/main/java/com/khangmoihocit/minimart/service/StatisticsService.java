package com.khangmoihocit.minimart.service;

import com.khangmoihocit.minimart.dto.response.*;

import java.time.LocalDate;
import java.util.List;

public interface StatisticsService {
    /**
     * Lấy thống kê tổng quan về sản phẩm
     */
    ProductStatisticsResponse getProductStatistics();

    /**
     * Lấy thống kê tổng quan về đơn hàng
     */
    OrderStatisticsResponse getOrderStatistics();

    /**
     * Lấy danh sách sản phẩm bán chạy nhất
     * @param limit số lượng sản phẩm muốn lấy
     */
    List<TopProductResponse> getTopSellingProducts(int limit);

    /**
     * Lấy danh sách sản phẩm bán chạy nhất theo khoảng thời gian
     * @param startDate ngày bắt đầu
     * @param endDate ngày kết thúc
     * @param limit số lượng sản phẩm muốn lấy
     */
    List<TopProductResponse> getTopSellingProductsByDateRange(LocalDate startDate, LocalDate endDate, int limit);

    /**
     * Lấy doanh thu theo ngày trong khoảng thời gian
     * @param startDate ngày bắt đầu
     * @param endDate ngày kết thúc
     */
    List<RevenueByDateResponse> getRevenueByDateRange(LocalDate startDate, LocalDate endDate);

    /**
     * Lấy thống kê theo danh mục
     */
    List<CategoryStatisticsResponse> getCategoryStatistics();
}

