package com.khangmoihocit.minimart.enums;

import lombok.Getter;
import org.springframework.http.HttpStatus;
import org.springframework.http.HttpStatusCode;

@Getter
public enum ErrorCode {
    // --- Lỗi Chung & Validation (1xxx) ---
    UNCATEGORIZED_EXCEPTION(9999, "Lỗi không xác định.", HttpStatus.INTERNAL_SERVER_ERROR),
    INVALID_REQUEST_DATA(1001, "Dữ liệu không hợp lệ.", HttpStatus.BAD_REQUEST),
    INVALID_DOB(1002, "Ngày sinh không hợp lệ. Bạn phải đủ {min} tuổi.", HttpStatus.BAD_REQUEST),
    INFO_NOT_BLANK(1003, "Thông tin không được để trống", HttpStatus.BAD_REQUEST),

    // --- Lỗi Người dùng & Xác thực (2xxx) ---
    USER_EXISTED(2001, "Tên đăng nhập đã tồn tại.", HttpStatus.BAD_REQUEST),
    EMAIL_EXISTED(2002, "Email này đã được sử dụng.", HttpStatus.BAD_REQUEST),
    USER_NOT_FOUND(2003, "Không tìm thấy người dùng.", HttpStatus.NOT_FOUND),
    INVALID_PASSWORD(2004, "Mật khẩu phải có ít nhất 8 ký tự.", HttpStatus.BAD_REQUEST),
    USERNAME_INVALID(2005, "Tên đăng nhập phải có ít nhất 3 ký tự.", HttpStatus.BAD_REQUEST),
    LOGIN_FAILED(2006, "Sai tên đăng nhập hoặc mật khẩu.", HttpStatus.UNAUTHORIZED),

    // --- Lỗi Phân quyền (21xx) ---
    UNAUTHENTICATED(2101, "Vui lòng đăng nhập để thực hiện chức năng này.", HttpStatus.UNAUTHORIZED), // 401
    UNAUTHORIZED(2102, "Bạn không có quyền truy cập tài nguyên này.", HttpStatus.FORBIDDEN),      // 403

    // --- Lỗi Sản phẩm & Danh mục (3xxx) ---
    PRODUCT_NOT_FOUND(3001, "Không tìm thấy sản phẩm.", HttpStatus.NOT_FOUND),
    CATEGORY_NOT_FOUND(3002, "Không tìm thấy danh mục.", HttpStatus.NOT_FOUND),
    OUT_OF_STOCK(3003, "Sản phẩm đã hết hàng.", HttpStatus.BAD_REQUEST),

    // --- Lỗi Giỏ hàng & Đơn hàng (4xxx) ---
    CART_IS_EMPTY(4001, "Giỏ hàng của bạn đang trống.", HttpStatus.BAD_REQUEST),
    ORDER_NOT_FOUND(4002, "Không tìm thấy đơn hàng.", HttpStatus.NOT_FOUND),
    CANNOT_CANCEL_ORDER(4003, "Không thể hủy đơn hàng ở trạng thái này.", HttpStatus.BAD_REQUEST),

    // --- Lỗi Mã giảm giá (Coupon) (5xxx) ---
    COUPON_NOT_FOUND(5001, "Mã giảm giá không tồn tại hoặc đã hết hạn.", HttpStatus.NOT_FOUND),
    COUPON_EXPIRED(5002, "Mã giảm giá đã hết hạn.", HttpStatus.BAD_REQUEST),
    COUPON_CONDITIONS_NOT_MET(5003, "Đơn hàng không đủ điều kiện áp dụng mã giảm giá.", HttpStatus.BAD_REQUEST),

    ;



    ErrorCode(int code, String message, HttpStatusCode statusCode){
        this.code = code;
        this.message = message;
        this.statusCode = statusCode;
    }

    private int code;
    private String message;
    private HttpStatusCode statusCode;
}
