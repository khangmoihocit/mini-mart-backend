package com.khangmoihocit.minimart.service;

import com.khangmoihocit.minimart.dto.request.OrderRequest;
import com.khangmoihocit.minimart.dto.request.UpdateOrderStatusRequest;
import com.khangmoihocit.minimart.dto.response.OrderResponse;
import com.khangmoihocit.minimart.dto.response.PageResponse;
import com.khangmoihocit.minimart.enums.OrderStatus;

public interface OrderService {
    /**
     * Tạo đơn hàng từ giỏ hàng hiện tại
     */
    OrderResponse createOrder(OrderRequest request);

    /**
     * Lấy danh sách đơn hàng của user hiện tại
     */
    PageResponse<OrderResponse> getMyOrders(int page, int size);

    /**
     * Lấy chi tiết một đơn hàng
     */
    OrderResponse getOrderById(String orderId);

    /**
     * Hủy đơn hàng (chỉ khi đơn hàng đang ở trạng thái PENDING)
     */
    OrderResponse cancelOrder(String orderId);

    // ==================== Admin APIs ====================

    /**
     * Lấy tất cả đơn hàng (Admin)
     */
    PageResponse<OrderResponse> getAllOrders(int page, int size);

    /**
     * Lấy đơn hàng theo trạng thái (Admin)
     */
    PageResponse<OrderResponse> getOrdersByStatus(OrderStatus status, int page, int size);

    /**
     * Cập nhật trạng thái đơn hàng (Admin)
     */
    OrderResponse updateOrderStatus(String orderId, UpdateOrderStatusRequest request);

    /**
     * Xóa đơn hàng (Admin)
     */
    void deleteOrder(String orderId);
}

