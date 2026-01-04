package com.khangmoihocit.minimart.service;

import com.khangmoihocit.minimart.dto.request.AddToCartRequest;
import com.khangmoihocit.minimart.dto.request.UpdateCartItemRequest;
import com.khangmoihocit.minimart.dto.response.CartResponse;

public interface CartService {
    /**
     * Lấy giỏ hàng của user hiện tại
     */
    CartResponse getMyCart();

    /**
     * Thêm sản phẩm vào giỏ hàng
     * Nếu sản phẩm đã có trong giỏ → cộng thêm số lượng
     * Nếu chưa có → tạo mới cart item
     */
    CartResponse addToCart(AddToCartRequest request);

    /**
     * Cập nhật số lượng của một item trong giỏ hàng
     */
    CartResponse updateCartItem(String cartDetailId, UpdateCartItemRequest request);

    /**
     * Xóa một item khỏi giỏ hàng
     */
    CartResponse removeCartItem(String cartDetailId);

    /**
     * Xóa toàn bộ giỏ hàng (clear cart)
     */
    void clearCart();

    /**
     * Lấy số lượng items trong giỏ hàng
     */
    int getCartItemCount();
}

