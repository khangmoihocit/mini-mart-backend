package com.khangmoihocit.minimart.dto.response;

import lombok.*;
import lombok.experimental.FieldDefaults;

import java.math.BigDecimal;

@Data
@AllArgsConstructor
@NoArgsConstructor
@Builder
@FieldDefaults(level = AccessLevel.PRIVATE)
public class OrderStatisticsResponse {
    Long totalOrders;
    Long pendingOrders;
    Long processingOrders;
    Long shippedOrders;
    Long deliveredOrders;
    Long cancelledOrders;
    BigDecimal totalRevenue; // Tổng doanh thu (chỉ tính đơn hàng đã giao)
    BigDecimal pendingRevenue; // Doanh thu đang chờ xử lý
    BigDecimal averageOrderValue;
}

