package com.khangmoihocit.minimart.controller;

import com.khangmoihocit.minimart.dto.request.OrderRequest;
import com.khangmoihocit.minimart.dto.request.UpdateOrderStatusRequest;
import com.khangmoihocit.minimart.dto.response.ApiResponse;
import com.khangmoihocit.minimart.dto.response.OrderResponse;
import com.khangmoihocit.minimart.dto.response.PageResponse;
import com.khangmoihocit.minimart.enums.OrderStatus;
import com.khangmoihocit.minimart.service.OrderService;
import jakarta.validation.Valid;
import lombok.AccessLevel;
import lombok.RequiredArgsConstructor;
import lombok.experimental.FieldDefaults;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.*;

@Slf4j
@RestController
@RequestMapping("/api/v1/orders")
@RequiredArgsConstructor
@FieldDefaults(level = AccessLevel.PRIVATE, makeFinal = true)
public class OrderController {
    OrderService orderService;

    @PostMapping
    public ResponseEntity<ApiResponse<OrderResponse>> createOrder(@Valid @RequestBody OrderRequest request) {
        OrderResponse order = orderService.createOrder(request);
        return ResponseEntity.ok(ApiResponse.<OrderResponse>builder()
                .message("Đặt hàng thành công!")
                .result(order)
                .build());
    }


    @GetMapping("/my-orders")
    public ResponseEntity<ApiResponse<PageResponse<OrderResponse>>> getMyOrders(
            @RequestParam(defaultValue = "1") int page,
            @RequestParam(defaultValue = "10") int size
    ) {
        PageResponse<OrderResponse> orders = orderService.getMyOrders(page, size);
        return ResponseEntity.ok(ApiResponse.<PageResponse<OrderResponse>>builder()
                .message("Lấy danh sách đơn hàng thành công!")
                .result(orders)
                .build());
    }


    @GetMapping("/{orderId}")
    public ResponseEntity<ApiResponse<OrderResponse>> getOrderById(@PathVariable String orderId) {
        OrderResponse order = orderService.getOrderById(orderId);
        return ResponseEntity.ok(ApiResponse.<OrderResponse>builder()
                .message("Lấy chi tiết đơn hàng thành công!")
                .result(order)
                .build());
    }


    @PutMapping("/{orderId}/cancel")
    public ResponseEntity<ApiResponse<OrderResponse>> cancelOrder(@PathVariable String orderId) {
        OrderResponse order = orderService.cancelOrder(orderId);
        return ResponseEntity.ok(ApiResponse.<OrderResponse>builder()
                .message("Hủy đơn hàng thành công!")
                .result(order)
                .build());
    }


    @PreAuthorize("hasRole('ADMIN')")
    @GetMapping("/admin/all")
    public ResponseEntity<ApiResponse<PageResponse<OrderResponse>>> getAllOrders(
            @RequestParam(defaultValue = "1") int page,
            @RequestParam(defaultValue = "10") int size
    ) {
        PageResponse<OrderResponse> orders = orderService.getAllOrders(page, size);
        return ResponseEntity.ok(ApiResponse.<PageResponse<OrderResponse>>builder()
                .message("Lấy danh sách tất cả đơn hàng thành công!")
                .result(orders)
                .build());
    }

    /**
     * Lấy đơn hàng theo trạng thái (Admin)
     * GET /api/v1/orders/admin/status/{status}?page=1&size=10
     * status: PENDING, PROCESSING, SHIPPED, DELIVERED, CANCELLED
     */
    @PreAuthorize("hasRole('ADMIN')")
    @GetMapping("/admin/status/{status}")
    public ResponseEntity<ApiResponse<PageResponse<OrderResponse>>> getOrdersByStatus(
            @PathVariable OrderStatus status,
            @RequestParam(defaultValue = "1") int page,
            @RequestParam(defaultValue = "10") int size
    ) {
        PageResponse<OrderResponse> orders = orderService.getOrdersByStatus(status, page, size);
        return ResponseEntity.ok(ApiResponse.<PageResponse<OrderResponse>>builder()
                .message("Lấy danh sách đơn hàng theo trạng thái thành công!")
                .result(orders)
                .build());
    }


    @PreAuthorize("hasRole('ADMIN')")
    @PutMapping("/admin/{orderId}/status")
    public ResponseEntity<ApiResponse<OrderResponse>> updateOrderStatus(
            @PathVariable String orderId,
            @Valid @RequestBody UpdateOrderStatusRequest request
    ) {
        OrderResponse order = orderService.updateOrderStatus(orderId, request);
        return ResponseEntity.ok(ApiResponse.<OrderResponse>builder()
                .message("Cập nhật trạng thái đơn hàng thành công!")
                .result(order)
                .build());
    }


    @PreAuthorize("hasRole('ADMIN')")
    @DeleteMapping("/admin/{orderId}")
    public ResponseEntity<ApiResponse<Void>> deleteOrder(@PathVariable String orderId) {
        orderService.deleteOrder(orderId);
        return ResponseEntity.ok(ApiResponse.<Void>builder()
                .message("Xóa đơn hàng thành công!")
                .build());
    }
}

