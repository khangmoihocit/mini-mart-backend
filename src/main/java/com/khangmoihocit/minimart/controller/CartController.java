package com.khangmoihocit.minimart.controller;

import com.khangmoihocit.minimart.dto.request.AddToCartRequest;
import com.khangmoihocit.minimart.dto.request.UpdateCartItemRequest;
import com.khangmoihocit.minimart.dto.response.ApiResponse;
import com.khangmoihocit.minimart.dto.response.CartResponse;
import com.khangmoihocit.minimart.service.CartService;
import jakarta.validation.Valid;
import lombok.AccessLevel;
import lombok.RequiredArgsConstructor;
import lombok.experimental.FieldDefaults;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

@Slf4j
@RestController
@RequestMapping("/api/v1/cart")
@RequiredArgsConstructor
@FieldDefaults(level = AccessLevel.PRIVATE, makeFinal = true)
public class CartController {
    CartService cartService;

    /**
     * Lấy giỏ hàng của user hiện tại
     * GET /api/v1/cart
     */
    @GetMapping
    public ResponseEntity<ApiResponse<CartResponse>> getMyCart() {
        CartResponse cart = cartService.getMyCart();
        return ResponseEntity.ok(ApiResponse.<CartResponse>builder()
                .message("Lấy giỏ hàng thành công!")
                .result(cart)
                .build());
    }

    /**
     * Thêm sản phẩm vào giỏ hàng
     * POST /api/v1/cart/add
     */
    @PostMapping("/add")
    public ResponseEntity<ApiResponse<CartResponse>> addToCart(@Valid @RequestBody AddToCartRequest request) {
        CartResponse cart = cartService.addToCart(request);
        return ResponseEntity.ok(ApiResponse.<CartResponse>builder()
                .message("Thêm sản phẩm vào giỏ hàng thành công!")
                .result(cart)
                .build());
    }

    /**
     * Cập nhật số lượng của một item trong giỏ hàng
     * PUT /api/v1/cart/items/{cartDetailId}
     */
    @PutMapping("/items/{cartDetailId}")
    public ResponseEntity<ApiResponse<CartResponse>> updateCartItem(
            @PathVariable String cartDetailId,
            @Valid @RequestBody UpdateCartItemRequest request) {
        CartResponse cart = cartService.updateCartItem(cartDetailId, request);
        return ResponseEntity.ok(ApiResponse.<CartResponse>builder()
                .message("Cập nhật giỏ hàng thành công!")
                .result(cart)
                .build());
    }

    /**
     * Xóa một item khỏi giỏ hàng
     * DELETE /api/v1/cart/items/{cartDetailId}
     */
    @DeleteMapping("/items/{cartDetailId}")
    public ResponseEntity<ApiResponse<CartResponse>> removeCartItem(@PathVariable String cartDetailId) {
        CartResponse cart = cartService.removeCartItem(cartDetailId);
        return ResponseEntity.ok(ApiResponse.<CartResponse>builder()
                .message("Xóa sản phẩm khỏi giỏ hàng thành công!")
                .result(cart)
                .build());
    }

    /**
     * Xóa toàn bộ giỏ hàng
     * DELETE /api/v1/cart/clear
     */
    @DeleteMapping("/clear")
    public ResponseEntity<ApiResponse<Void>> clearCart() {
        cartService.clearCart();
        return ResponseEntity.ok(ApiResponse.<Void>builder()
                .message("Đã xóa toàn bộ giỏ hàng!")
                .build());
    }

    /**
     * Lấy số lượng items trong giỏ hàng
     * GET /api/v1/cart/count
     */
    @GetMapping("/count")
    public ResponseEntity<ApiResponse<Integer>> getCartItemCount() {
        int count = cartService.getCartItemCount();
        return ResponseEntity.ok(ApiResponse.<Integer>builder()
                .message("Lấy số lượng items thành công!")
                .result(count)
                .build());
    }
}

